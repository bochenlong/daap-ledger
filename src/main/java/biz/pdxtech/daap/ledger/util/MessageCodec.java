package biz.pdxtech.daap.ledger.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bochenlong on 16-11-3.
 */
public class MessageCodec {
    private static ConcurrentHashMap<Class<?>, Schema<?>> cschema
            = new ConcurrentHashMap();

    public static <T> byte[] toBytes(T t) {
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        Class clazz = t.getClass();
        Schema schema = cschema.computeIfAbsent(clazz, a -> RuntimeSchema.createFrom(clazz));
        return ProtostuffIOUtil.toByteArray(t, schema, buffer);
    }

    public static <T> T toObject(byte[] bytes, Class<T> clazz) {
        try {
            T obj = clazz.newInstance();
            ProtostuffIOUtil.mergeFrom(bytes, obj, (Schema<T>) cschema.getOrDefault(clazz, RuntimeSchema.createFrom(clazz)));
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
