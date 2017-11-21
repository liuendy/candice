package io.candice;

import io.candice.route.util.StringUtil;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 文件描述:
 * 作者: yinwenjie
 * 日期: 2017-09-14
 */
public class CandiceContext {

    private static final Logger logger  = Logger.getLogger(CandiceContext.class);

    private static final String PRI_KEY = "privateKey";

    private static final String PUB_KEY = "publicKey";

    /**环境变量 **/
    private static Properties prop    = new Properties();

    private CandiceContext() {

    }

    public static void load(String fp) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fp);
            prop.load(fis);
            for (Object key : prop.keySet()) {
                String kStr = key.toString();
                logger.info("load prop[key:" + kStr + ",val:" + prop.getProperty(kStr) + "]");
                //System.out.println("-->" + kStr + "," + prop.getProperty(kStr));
            }
        } catch (IOException e) {
            logger.error("读取properties文件:" + fp + "异常", e);
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    public static String getProp(String key) {
        return prop.getProperty(key);
    }

    public static String getPubKey() {
        return StringUtil
                .defaultIfBlank(
                        prop.getProperty(PUB_KEY),
                        "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKHGwq7q2RmwuRgKxBypQHw0mYu4BQZ3eMsTrdK8E6igRcxsobUC7uT0SoxIjl1WveWniCASejoQtn/BY6hVKWsCAwEAAQ==");
    }

    public static String getPriKey() {
        return StringUtil
                .defaultIfBlank(
                        prop.getProperty(PRI_KEY),
                        "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAsVURHPJ0rnnmwu6h0UCTl2eQ1zJeAeksVKLSM/1uySTT//njUBYT/wZfTTTq3j9QhiI5/Y/u9O+GV8mLtl8KMQIDAQABAkBZD1IC6bm0DsDYUaSvRPFa7saNbVxNNV/wOb28IqHLHn6A2C7ewLIn00NEeTkFfXeePoKWt248XohP91RCpU0JAiEA7ok6HcYz5jJeqTuqTv18YBiO8mZ3HdyMQaXJRccMxm8CIQC+ULnE9K08Xv31gbWvwKqSAdnlYKrcm6gA7kw23JCJXwIgQpEXH9H9D8OEuTOGXo7M08Bmu+yuVy4CEhvi5E8dGI8CIQCNAaiySgr0mPkW5pTj9B8s8NwtvDK8I0QV9HlZiJA6hwIgabpUz2mhCdteAzzlZY03eoxe5t9QkWNce1tb5LSMmrY=");
    }
}
