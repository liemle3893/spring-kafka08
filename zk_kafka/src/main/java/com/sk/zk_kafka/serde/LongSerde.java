package com.sk.zk_kafka.serde;

import kafka.serializer.Decoder;
import kafka.serializer.Encoder;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;


@SuppressWarnings("unchecked")
/**
 * https://stackoverflow.com/a/29132118/3647105
 */
public class LongSerde implements Serializer<Long>, Deserializer<Long>, Decoder<Long>, Encoder<Long> {
    @Override
    public Long deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        return fromByteArray(data);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, Long data) {
        if (data == null) {
            return null;
        }
        return toByteArray(data);
    }

    @Override
    public void close() {

    }

    public static byte[] toByteArray(long l) {
        byte[] result = new byte[Long.BYTES];
        for (int i = (Long.BYTES - 1); i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= Long.BYTES;
        }
        return result;
    }

    public static Long fromByteArray(byte[] b) {
        if (b == null || b.length < Long.BYTES) {
            return null;
        }
        long result = 0;
        for (int i = 0; i < Long.BYTES; i++) {
            result <<= Long.BYTES;
            result |= (b[i] & 0xFF);
        }
        return result;
    }

    @Override
    public Long fromBytes(byte[] bytes) {
        return fromByteArray(bytes);
    }

    @Override
    public byte[] toBytes(Long aLong) {
        return toByteArray(aLong);
    }
}
