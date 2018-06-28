package core;

import core.domain.RPCRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Random;

/**
 * Created by yeyh on 2018/6/12.
 */
public class Client<T> {


    public static <T> T getProxyObject(Class<?> serverInf, BIOClient bioClient) {
        return (T) Proxy.newProxyInstance(serverInf.getClassLoader(), new Class[] { serverInf },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {
                        RPCRequest rpcRequest =new RPCRequest();
                        rpcRequest.setRequestId(String.valueOf(new Random().nextInt()));
                        rpcRequest.setClassName(serverInf.getName());
                        rpcRequest.setMethodName(method.getName());
                        rpcRequest.setParameterTypes(method.getParameterTypes());
                        rpcRequest.setParameters(args);
                        return  bioClient.send(rpcRequest);
                    }
                });
    }



}
