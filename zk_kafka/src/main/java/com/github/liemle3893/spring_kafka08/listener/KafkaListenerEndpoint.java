package com.github.liemle3893.spring_kafka08.listener;

import java.util.Collection;

public interface KafkaListenerEndpoint {
	Collection<String> getTopics();

	String getTopicPattern();

	String getGroupId();

	default String clientId() {
		return null;
	}
}
