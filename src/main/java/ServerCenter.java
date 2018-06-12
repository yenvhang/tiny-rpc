import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by yeyh on 2018/6/12.
 */
public class ServerCenter implements Server {
    private Logger            logger     =LoggerFactory.getLogger(getClass());
    private Map<String,Class> serversMap =new HashMap<>();
    private int port;
    private Executor fixedThreadPool =Executors.newFixedThreadPool(10);
    private Socket socket;
    public ServerCenter(int port) {
        this.port = port;
    }

    @Override
    public void stop() {

    }

    @Override
    public void start() throws IOException {
        ServerSocket serverSocket =new ServerSocket(port);
        logger.info("启动服务...");
        while(true){
            socket =serverSocket.accept();
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                        ObjectOutput objectOutput = new ObjectOutputStream(socket.getOutputStream());
                        String serviceName =inputStream.readUTF();
                        String methodName =inputStream.readUTF();
                        Class<?> [] parameterTypes = (Class<?>[]) inputStream.readObject();
                        Object[] arguments = (Object[]) inputStream.readObject();
                        Class serviceImpl = serversMap.get(serviceName);
                        if(serviceImpl==null){
                            throw new ClassNotFoundException(serviceName +"not found");
                        }
                        Method method  =serviceImpl.getMethod(methodName,parameterTypes);
                        logger.info("开始执行方法"+methodName);

                        Object result =method.invoke(serviceImpl.newInstance(),arguments);
                        logger.info(methodName +"方法执行完成");
                        objectOutput.writeObject(result);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    @Override
    public void registry(Class inf, Class impl) throws ClassNotFoundException {
        if(inf ==null || impl == null){
            throw new ClassNotFoundException("");
        }

        serversMap.put(inf.getName(),impl);
    }
}
