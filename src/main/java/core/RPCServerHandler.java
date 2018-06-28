package core;

import core.domain.RPCRequest;
import core.domain.RPCResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by yeyh on 2018/6/28.
 */
@ChannelHandler.Sharable
public class RPCServerHandler extends SimpleChannelInboundHandler<RPCRequest> {
    private Map<String,Object> beansMap;

    public RPCServerHandler(Map<String, Object> beansMap) {
        this.beansMap = beansMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCRequest req) throws Exception {

        RPCResponse rpcResponse =new RPCResponse();
        rpcResponse.setRequestId(req.getRequestId());
        try{
        String infName=req.getClassName();
        String methodName =req.getMethodName();
        Class<?>[] parameterTypes =req.getParameterTypes();
        Object[] parameters =req.getParameters();
        Object obj =beansMap.get(infName);
        if(obj==null){
            throw new ClassNotFoundException();
        }

            Class clz =obj.getClass();
            Method method =clz.getMethod(methodName,parameterTypes);
            Object result =method.invoke(obj,parameters);
            if(result instanceof Throwable){
                rpcResponse.setError((Throwable) result);
            }
            else{
                rpcResponse.setResult(result);
            }
        }
        catch (Exception e){
            rpcResponse.setError(e.getCause());
        }
        ctx.writeAndFlush(rpcResponse);
    }
}
