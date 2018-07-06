package top.nvhang.rpc.core;



import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import top.nvhang.rpc.common.RPCRequest;
import top.nvhang.rpc.common.RPCResponse;
import top.nvhang.rpc.registry.ServiceDiscovery;

import java.net.Socket;

/**
 * Created by yeyh on 2018/6/25.
 */
public class BIOClient {


    private ServiceDiscovery serviceDiscovery;

    public void setServiceDiscovery(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public Object send(RPCRequest rpcRequest) throws Throwable {
        //这里进行同步操作
        RPCResponse rpcResponse;
        String url =serviceDiscovery.getServiceUrl();
        System.out.println("begin connect "+ url);
        String [] address  =url.split(":");
        Socket socket = new Socket(address[0],Integer.valueOf(address[1]));
        System.out.println("connect success" +url);
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);

        Output output=new Output(socket.getOutputStream());

        kryo.writeObject(output,rpcRequest);
        output.flush();

        Input input=new Input(socket.getInputStream());

        try {
            //先序列化成 obj
            rpcResponse = kryo.readObject(input, RPCResponse.class);
            return rpcResponse.getResult();
        } catch (Exception e) {
            //序列化失败则换成Throwable
            throw kryo.readObject(input, Throwable.class);
        } finally {
           socket.close();
        }
    }


}








