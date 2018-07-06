package top.nvhang.rpc.common.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

import java.io.ByteArrayOutputStream;

/**
 * Created by yeyh on 2018/6/28.
 */
public class SerializationUtil {
    public static byte [] serialize(Object obj){
        Kryo kryo =new Kryo();

        kryo.setRegistrationRequired(false);
        kryo.register(ClassNotFoundException.class,new JavaSerializer());
        Output output= new Output(new ByteArrayOutputStream());
        kryo.writeClassAndObject(output,obj);
        return output.getBuffer();
    }
    public static <T> T deserialize(Class<T> clz,byte [] bytes){
        Kryo kryo =new Kryo();
        kryo.register(ClassNotFoundException.class,new JavaSerializer());
        kryo.setRegistrationRequired(false);
        Input input= new Input(bytes);
        return (T) kryo.readClassAndObject(input);
    }
}
