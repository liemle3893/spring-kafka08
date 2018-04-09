package com.sk.zk_kafka.listener;

import java.util.Collection;

public interface KafkaListenerEndpoint {
    Collection<String> getTopics();

    String getTopicPattern();

    String getGroupId();
}
