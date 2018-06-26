# tiny-rpc
> a tiny-rpc for learning socket,netty,jdk proxy,zookeeper etc...
## 关于
tiny-rpc 是为了学习rpc框架而开发的。通过阅读 dubbo,grpc 等源码,一步步构建一个简易版的rpc。
开发过程中使用到了基础的socket,jdk proxy,反射,nio等api。而后引进了netty替代了socket,引进 kryo,hessian 替换 java 的序列化。引入 zk 作为服务的注册中心,Spring 作为容器管理对象的创建。  
'tiny-rpc'是逐步构建的，历史版本见[`branch-log.md`](https://github.com/yenvhang/tiny-rpc/blob/master/branch-log.md)。



  
