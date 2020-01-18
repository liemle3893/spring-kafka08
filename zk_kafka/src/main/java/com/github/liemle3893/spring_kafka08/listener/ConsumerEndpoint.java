package com.github.liemle3893.spring_kafka08.listener;

import com.github.liemle3893.spring_kafka08.config.MissingListenerConfig;
import com.github.liemle3893.spring_kafka08.util.MutableConfiguration;
import kafka.consumer.KafkaStream;
import kafka.consumer.Whitelist;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.consumer.ZookeeperConsumerConnector;
import kafka.serializer.Decoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.liemle3893.spring_kafka08.util.BasicThreadFactory;

public class ConsumerEndpoint implements SmartInitializingSingleton, KafkaListenerEndpoint {
	private final Log log = LogFactory.getLog(getClass());
	private final Collection<String> topics;
	private final Pattern topicPattern;
	private final String groupId;
	private final String configBeanRef;
	private ConsumerConnector consumerConnector;
	private BeanFactory beanFactory;
	private ExecutorService executorService;
	private final int threadNum;
	private final Method method;
	private final Object bean;
	private final Deserializer valueDeserializer;
	private final Deserializer keyDeserializer;
	private String clientId;

	private final GenericErrorHandler errorHandler;

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		kafka.consumer.ConsumerConfig configs = getConfigs();
		executorService = Executors.newCachedThreadPool(new BasicThreadFactory.Builder()
				.namingPattern("Consumer:" + configs.clientId() + ":" + method.getName() + "-%d")
				.build());
		consumerConnector = new ZookeeperConsumerConnector(configs);
	}

	private kafka.consumer.ConsumerConfig getConfigs() {
		Map<Object, Object> configs;
		try {
			configs = (MutableConfiguration) beanFactory.getBean(configBeanRef);
		} catch (Exception ex) {
			throw new MissingListenerConfig("Invalid config bean. " + configBeanRef, ex);
		}
		if (configs.get("zookeeper.connect") == null) {
			throw new MissingListenerConfig(configBeanRef
					+ " does not have properties 'zookeeper.connect'");
		}
		// Copy configs
		Properties props = new Properties();
		props.putAll(configs);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		if (clientId != null) {
			props.put("consumer.id", clientId);
		}
		return new kafka.consumer.ConsumerConfig(props);
	}

	public ConsumerEndpoint(Collection<String> topics,
							String rawTopicPattern,
							String groupId,
							String configBeanRef,
							int threadNum,
							Method method,
							Object bean,
							GenericErrorHandler errorHandler,
							Deserializer keyDeserializer,
							Deserializer valueDeserializer) {
		boolean topicsValidated = validateTopics(topics);
		Pattern pattern = validatePattern(rawTopicPattern);
		if (!topicsValidated && pattern == null) {
			throw new IllegalStateException("Invalid state." +
					" Either topics or topic pattern must be non null!");
		}
		this.method = method;
		this.bean = bean;
		this.errorHandler = errorHandler;
		this.topics = topics;
		this.topicPattern = pattern;
		this.groupId = groupId;
		this.configBeanRef = configBeanRef;
		this.threadNum = threadNum;
		this.valueDeserializer = valueDeserializer;
		this.keyDeserializer = keyDeserializer;
	}

	private boolean validateTopics(Collection<String> topics) {
		return topics != null && !topics.isEmpty();
	}

	private Pattern validatePattern(String rawPattern) {
		return StringUtils.isEmpty(rawPattern) ? null : Pattern.compile(rawPattern);
	}

	public void run() {
		Decoder keyDecoder = new DecoderWrapper<>(keyDeserializer);
		Decoder valueDecoder = new DecoderWrapper<>(valueDeserializer);
		if (topicPattern != null) {
			List<KafkaStream> kafkaStreams
					= consumerConnector.createMessageStreamsByFilter(
					new Whitelist(topicPattern.pattern()), threadNum, keyDecoder, valueDecoder
			);
			kafkaStreams.stream()
					.map(s -> new ConsumerWrapper(topicPattern.pattern(), s, method, bean, errorHandler))
					.forEach(c -> executorService.submit((Runnable) c));
		} else if (validateTopics(topics)) {
			Map<String, Integer> topicCountMap = topics.stream()
					.collect(Collectors.toMap(Function.identity(), x -> threadNum));
			Map<String, List<KafkaStream>> messageStreams
					= consumerConnector.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);
			for (Map.Entry<String, List<KafkaStream>> e : messageStreams.entrySet()) {
				String topic = e.getKey();
				List<KafkaStream> listStreams = e.getValue();
				listStreams.stream()
						.map(s -> new ConsumerWrapper(topic, s, method, bean, errorHandler))
						.forEach(c -> executorService.submit(c));
			}
		} else {
			// No-OP
			// Error was checked when creating this object.
		}
	}

	@Override
	public void afterSingletonsInstantiated() {
	}

	@Override
	public Collection<String> getTopics() {
		return this.topics;
	}

	@Override
	public String getTopicPattern() {
		return Optional.ofNullable(topicPattern).map(p -> p.pattern()).orElse(null);
	}

	@Override
	public String getGroupId() {
		return groupId;
	}

	static class DecoderWrapper<T> implements Decoder<T> {
		private final Deserializer<T> delegate;

		DecoderWrapper(Deserializer<T> delegate) {
			this.delegate = delegate;
		}

		@Override
		public T fromBytes(byte[] bytes) {
			return delegate.deserialize(null, bytes);
		}
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}


}
