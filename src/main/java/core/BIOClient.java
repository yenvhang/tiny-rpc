package core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import core.domain.RPCRequest;
import core.domain.RPCResponse;


import java.net.Socket;


/**
 * Created by yeyh on 2018/6/25.
 */
public class BIOClient {

    private String host;
    private int port;

    public synchronized void updateSocketAdress(String date){
        String [] dates =date.split(":");
        host=dates[0];
        port= Integer.parseInt(dates[1]);
    }

    public Object send(RPCRequest rpcRequest) throws Throwable {
        //这里进行同步操作
        RPCResponse rpcResponse;
        System.out.println("begin connect");
        Socket socket = new Socket(host,port);
        System.out.println("connect success");
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








