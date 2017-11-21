package io.candice.server.mysql.handler;

import io.candice.net.connection.MySQLConnection;
import io.candice.net.mysql.ErrorPacket;
import io.candice.route.RouteResultsetNode;
import io.candice.server.session.NonBlockingSession;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * 文件描述:
 * 作者: yinwenjie
 * 日期: 2017-11-03
 */
public class RollbackNodeHandler extends MultiNodeHandler {

    private static final Logger logger = Logger.getLogger(RollbackNodeHandler.class);

    public RollbackNodeHandler(NonBlockingSession session) {
        super(session);
    }

    public void rollback() {
        final int initCount = session.getTargetCount();
        lock.lock();
        try {
            reset(initCount);
        } finally {
            lock.unlock();
        }
        if (session.closed()) {
            decrementCountToZero();
            return;
        }

        // 执行
//        Executor executor = session.getSource().getProcessor().getExecutor();
        int started = 0;
        for (final RouteResultsetNode node : session.getTargetKeys()) {
            if (node == null) {
                try {
                    logger.error("null is contained in RoutResultsetNodes, source = " + session.getSource());
                } catch (Exception e) {
                }
                continue;
            }
            final MySQLConnection conn = session.getTarget(node);
            if (conn != null) {
                conn.setRunning(true);

                if (isFail.get() || session.closed()) {
                    backendConnError(conn, "cancelled by other thread");
                    return;
                }
                conn.setHandler(RollbackNodeHandler.this);
                conn.rollback();

                 ++started;
            }
        }

        if (started < initCount && decrementCountBy(initCount - started)) {
            /**
             * assumption: only caused by front-end connection close. <br/>
             * Otherwise, packet must be returned to front-end
             */
            session.clearConnections();
        }
    }

    @Override
    public void okResponse(byte[] ok, MySQLConnection conn) {
        conn.setRunning(false);
        if (decrementCountBy(1)) {
            if (isFail.get() || session.closed()) {
                notifyError((byte) 1);
            } else {
                session.getSource().write(ok);
            }
        }
    }

    @Override
    public void errorResponse(byte[] data, MySQLConnection conn) {
        ErrorPacket err = new ErrorPacket();
        err.read(data);
        backendConnError(conn, err);
    }

    @Override
    public void rowEofResponse(byte[] eof, MySQLConnection conn) {
        backendConnError(conn, "Unknown response packet for back-end rollback");
    }

    @Override
    public void connectionError(Throwable e, MySQLConnection conn) {
        backendConnError(conn, "connection err for " + conn);
    }

    @Override
    public void connectionAcquired(MySQLConnection conn) {
        logger.error("unexpected invocation: connectionAcquired from rollback");
        conn.release();
    }

    @Override
    public void fieldEofResponse(byte[] header, List<byte[]> fields, byte[] eof, MySQLConnection conn) {
        logger.error(new StringBuilder().append("unexpected packet for ").append(conn).append(" bound by ")
                .append(session.getSource()).append(": field's eof").toString());
    }

    @Override
    public void rowResponse(byte[] row, MySQLConnection conn) {
        logger.error(new StringBuilder().append("unexpected packet for ").append(conn).append(" bound by ")
                .append(session.getSource()).append(": field's eof").toString());
    }
}
