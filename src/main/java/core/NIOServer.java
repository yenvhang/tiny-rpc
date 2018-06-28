package core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import core.domain.RPCRequest;
import core.domain.RPCResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by yeyh on 2018/6/25.
 */
public class NIOServer implements Server {
    private Map<String, Class> serversMap = new HashMap<>();

    public static byte[] grow(byte[] src, int size) {
        byte[] tmp = new byte[src.length + size];
        System.arraycopy(src, 0, tmp, 0, src.length);
        return tmp;
    }

    @Override
    public void stop() {

    }

    @Override
    public void start() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {

            int readyChannels = selector.select();//返回的是Channel的个数
            if (readyChannels == 0) {
                continue;
            }
            Set<SelectionKey> set = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = set.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    System.out.println("服务器建立好连接");

                    ServerSocketChannel tempSocketChannel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = tempSocketChannel.accept();
                    System.out.println("connect channel:" + tempSocketChannel);
                    socketChannel.configureBlocking(false);

                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
                if (key.isReadable()) {
                    int offset = 0;
                    int read = 0;
                    byte[] storage = new byte[1024];
                    System.out.println("服务端开始读数据");
                    RPCResponse rpcResponse = new RPCResponse();
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    System.out.println("read channel:" + socketChannel);
                    socketChannel.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    try {

                        while ((read = socketChannel.read(buffer)) > 0) {

                            //切换成读模式
                            buffer.flip();
                            if (read + offset > storage.length) {
                                storage = grow(storage, storage.length * 2);
                            }

                            System.arraycopy(buffer.array(), 0, storage, offset, read);
                            offset += read;
                            buffer.clear();
                        }
                        System.out.println(new String(storage));
                        Kryo kryo = new Kryo();
                        kryo.setRegistrationRequired(false);
                        RPCRequest rpcRequest = kryo
                                .readObject(new Input(storage), RPCRequest.class);
                        Class clz = serversMap.get(rpcRequest.getClassName());

                        if (clz == null) {
                            throw new ClassNotFoundException(clz + "not found");
                        }

                        Method method = clz.getMethod(rpcRequest.getMethodName(),
                                rpcRequest.getParameterTypes());
                        Object result = method
                                .invoke(clz.newInstance(), rpcRequest.getParameters());
                        if (result instanceof Throwable) {
                            rpcResponse.setError((Throwable) result);
                        } else {
                            rpcResponse.setResult(result);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        rpcResponse.setError(e.getCause());
                    }
                    socketChannel
                            .register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ,
                                    rpcResponse);

                }
                if (key.isWritable()) {
                    System.out.println("服务端写数据");
                    SocketChannel writeChannle = (SocketChannel) key.channel();
                    System.out.println("write channel:" + writeChannle);
                    Kryo kryo = new Kryo();
                    RPCResponse rpcResponse = (RPCResponse) key.attachment();
                    if (rpcResponse != null) {
                        kryo.setRegistrationRequired(false);
                        Output output = new Output(new ByteArrayOutputStream());
                        kryo.writeObject(output, rpcResponse);
                        ByteBuffer byteBuffer = ByteBuffer.wrap(output.getBuffer());

                        while (byteBuffer.hasRemaining()) {
                            writeChannle.write(byteBuffer);
                        }
                    }
                    key.cancel();

                }

                keyIterator.remove();
            }
        }
    }

    @Override
    public void registry(Class inf, Class impl) throws ClassNotFoundException {
        if (inf == null || impl == null) {
            throw new ClassNotFoundException("");
        }

        serversMap.put(inf.getName(), impl);
    }

}
