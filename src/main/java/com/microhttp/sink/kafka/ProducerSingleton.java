package com.microhttp.sink.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import com.microhttp.sink.utils.SinkUtils;

public enum ProducerSingleton {
	_INSTANCE;
	
	private Producer<Long, String> producer = null;
	
	public org.apache.kafka.clients.producer.Producer<Long,String> getKafkaProducer() {
		if( producer == null ){
			 producer = new KafkaProducer<>(SinkUtils.kafkaProps);
			}
			return producer;
	}
}
