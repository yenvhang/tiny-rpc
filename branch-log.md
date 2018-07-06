# tiny-rpc
> a tiny-rpc for learning socket,netty,jdk proxy,zookeeper etc...
## 关于

### v1- jdk动态代理,socket 的使用
    使用Proxy.newProxyInstance()生成代理对象。在代理对象的invoke方法中 使用socket连接服务器，并传入被调用方法的相关信息
    服务端获取方法信息，通过反射调用指定方法，并返回相应结果
  
### v2- nio,kryo的使用。
    在v1 的基础上，将服务端从BIO模式改成NIO。引入新的序列化框架 kryo

### v3- netty,zookeeper 的使用。
    在v2 的基础上将NIO 替换成Netty。并引入 zookeeper 作为注册中心

### v4- 模块化。软负载
    重构代码架构。分出各个模块
    tiny-common 作为基础包。
    tiny-clinet 作为客户端。
    tiny-server 提供服务。
    tiny-rigisty 提供服务的注册和发现。
    tiny-demo 提供示例代码