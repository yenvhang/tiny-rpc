# tiny-rpc
> a tiny-rpc for learning socket,netty,jdk proxy,zookeeper etc...
## 关于

### v1- jdk动态代理,socket 的使用
    使用Proxy.newProxyInstance()生成代理对象。在代理对象的invoke方法中 使用socket连接服务器，并传入被调用方法的相关信息
    服务端获取方法信息，通过反射调用指定方法，并返回相应结果
  

  
