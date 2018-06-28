package core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import core.domain.RPCRequest;
import core.domain.RPCResponse;
import core.domain.RPCServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import registry.ServiceRegistry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.lang.reflect.Method;

import java.nio.ByteBuffer;
import java.nio.channels.*;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by yeyh on 2018/6/25.
 */

public class NIOServer implements Server,ApplicationContextAware,InitializingBean {

    private Map<String,Object> beansMap=new HashMap<>();
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

        EventLoopGroup group =new NioEventLoopGroup();
        EventLoopGroup child =new NioEventLoopGroup();
        try{
            ServerBootstrap b =new ServerBootstrap();
             b.group(group, child)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(8080)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()

                                    .addLast(new RPCDecoder())
                                    .addLast(new RPCEncoder())
                                    .addLast(new RPCServerHandler(beansMap));
                        }
                    });
            ChannelFuture f =b.bind().sync();
            f.channel().closeFuture().sync();

        }
        catch (Exception e){

        }
        finally {
            try {
                group.shutdownGracefully().sync();
                child.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }

    @Override
    public void registry(Class inf, Object object) throws ClassNotFoundException {
        if (inf == null || object == null) {
            throw new ClassNotFoundException("");
        }

        beansMap.put(inf.getName(),object);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serversMap =applicationContext.getBeansWithAnnotation(RPCServer.class);
        if(serversMap!=null){
            for(Map.Entry<String,Object> entry:serversMap.entrySet()){
                Object object =entry.getValue();
                if(object!=null){
                    String interfaceName =object.getClass().getAnnotation(RPCServer.class).value().getName();
                    beansMap.put(interfaceName,entry.getValue());
                }

            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        ServiceRegistry registry =new ServiceRegistry();
        registry.register("172.21.3.32:8080");
        //启动服务
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //注册服务


    }
}
