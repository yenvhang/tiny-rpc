package top.nvhang.rpc.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import top.nvhang.rpc.common.RPCRequest;
import top.nvhang.rpc.common.utils.SerializationUtil;

import java.util.List;

/**
 * Created by yeyh on 2018/6/28.
 */
public class RPCDecoder extends ByteToMessageDecoder{

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
            throws Exception {
         int length =in.readableBytes();
        byte [] array =new byte[length];
        in.getBytes(in.readerIndex(),array);
        out.add(SerializationUtil.deserialize(RPCRequest.class,array));


    }



}
