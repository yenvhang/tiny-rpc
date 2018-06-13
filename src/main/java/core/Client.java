package core;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by yeyh on 2018/6/12.
 */
public class Client<T> {
    public static <T> T getProxyObject(Class<?> serverInf, int port) {
        return (T) Proxy.newProxyInstance(serverInf.getClassLoader(), new Class[] { serverInf },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {

                        Socket socket = new Socket(InetAddress.getLocalHost(), port);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                                socket.getOutputStream());

                        ObjectInputStream objectInputStream = null;
                        try {

                            // д���������
                            objectOutputStream.writeUTF(serverInf.getName());
                            // д�뷽����
                            objectOutputStream.writeUTF(method.getName());
                            // д���������
                            objectOutputStream.writeObject(method.getParameterTypes());
                            // д�����
                            objectOutputStream.writeObject(args);
                            objectOutputStream.flush();
                            objectInputStream = new ObjectInputStream(socket.getInputStream());
                            Object object = objectInputStream.readObject();
                            if (object != null && object instanceof Throwable) {
                                throw (Throwable) object;
                            }
                            return object;
                        } finally {
                            if (socket != null) {
                                socket.close();
                            }
                            if (objectInputStream != null) {
                                objectInputStream.close();
                            }
                            if (objectOutputStream != null) {
                                objectOutputStream.close();
                            }
                        }
                    }

                    });
                }

}
