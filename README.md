#candice 
##mysql分库分表中间件
> 借鉴cobar以及heisenberg
- 通讯层使用netty实现
1. 前端交互frontendConnection基于netty实现mysql协议的服务端
2. 后端交互基于异步连接池
- 分库分表
1. 数据做到水平切分
2. 可以自定义规则，灵活配置
