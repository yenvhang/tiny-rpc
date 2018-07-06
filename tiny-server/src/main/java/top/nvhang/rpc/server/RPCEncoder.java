package top.nvhang.rpc.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import top.nvhang.rpc.common.utils.SerializationUtil;

/**
 * Created by yeyh on 2018/6/28.
 */
public class RPCEncoder extends MessageToByteEncoder{

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        out.writeBytes(SerializationUtil.serialize(msg));
    }
}
