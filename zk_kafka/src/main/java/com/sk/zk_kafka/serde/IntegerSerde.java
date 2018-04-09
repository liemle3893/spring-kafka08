package com.sk.zk_kafka.serde;

import kafka.serializer.Encoder;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import kafka.serializer.Decoder;

import java.util.Map;

/**
 * https://stackoverflow.com/a/7619315/3647105
 */
public class IntegerSerde implements Serializer<Integer>, Deserializer<Integer>, Decoder<Integer>, Encoder<Integer> {
    @Override
    public Integer deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        return fromByteArray(data);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, Integer data) {
        if (data == null) {
            return null;
        }
        return toByteArray(data);
    }

    @Override
    public void close() {

    }

    static byte[] toByteArray(int value) {
        return new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value};
    }

    // packing an array of 4 bytes to an int, big endian
    static Integer fromByteArray(byte[] bytes) {
        if (bytes == null || bytes.length < Integer.BYTES) {
            return null;
        }
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    @Override
    public Integer fromBytes(byte[] bytes) {
        return fromByteArray(bytes);
    }

    @Override
    public byte[] toBytes(Integer integer) {
        return toByteArray(integer);
    }
}
