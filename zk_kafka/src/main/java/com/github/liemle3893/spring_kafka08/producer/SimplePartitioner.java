package com.github.liemle3893.spring_kafka08.producer;

import kafka.producer.DefaultPartitioner;
import kafka.utils.VerifiableProperties;

import java.nio.ByteBuffer;
import java.util.Random;

public class SimplePartitioner extends DefaultPartitioner {
    public static Random rnd = new Random();

    public SimplePartitioner(VerifiableProperties props) {
        super(props);
    }

    @Override
    public int partition(Object key, int numPartitions) {
        byte[] bytes;
        if (key instanceof byte[]) {
            bytes = (byte[]) key;
        } else {
            bytes = new byte[0];
        }
        return partition0(bytes, numPartitions);
    }

    public int partition0(byte[] key, int a_numPartitions) {
        if (key.length > 0) {
            try {
                ByteBuffer bb = ByteBuffer.wrap(key);
                if (key.length < 8) {
                    return bb.getInt() % a_numPartitions;
                } else {
                    long keyLong = bb.getLong();
                    return (int) (keyLong % a_numPartitions);
                }
            } catch (Exception ex) {
            }
        }

        return rnd.nextInt(a_numPartitions);
    }
}
