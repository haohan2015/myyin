package com.myyin.common.util;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author admin
 * @Description: TODO
 * @date 2019/4/16 15:20
 */
public class SerializationUtil {

    private static Map<Class<?>,Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    private static Objenesis objenesis = new ObjenesisStd(true);

    public SerializationUtil() {
    }

    public static <T> byte[] serialize(T obj){
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try{
            Schema<T> schema = getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj,schema,buffer);
        }finally {
            buffer.clear();
        }
    }

    public static <T> T deserialize(byte[] data,Class<T> cls){
        T message = objenesis.newInstance(cls);
        Schema<T> schema = getSchema(cls);
        ProtostuffIOUtil.mergeFrom(data,message,schema);
        return message;
    }

    private static <T> Schema<T> getSchema(Class<T> cls){
        return (Schema<T>) cachedSchema.computeIfAbsent(cls, key->{
            Schema<T> schema = RuntimeSchema.createFrom(cls);
            cachedSchema.put(cls,schema);
            return schema;
        });
    }
}
