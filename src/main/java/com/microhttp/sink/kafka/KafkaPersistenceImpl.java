package com.microhttp.sink.kafka;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.microhttp.sink.persistence.PersistenceStoreType;
import com.microhttp.sink.utils.SinkUtils;

public class KafkaPersistenceImpl  implements PersistenceStoreType {
	private final static Logger _log = Logger.getLogger(KafkaPersistenceImpl.class.getName());
	private final Producer<Long, String> _producer = ProducerSingleton._INSTANCE.getKafkaProducer();
	
	@Override
	public boolean persist(final String data ) {
		boolean persisted = false;
		final long now = Instant.now().toEpochMilli();
		final ProducerRecord<Long, String> record =  new ProducerRecord<Long,String>(SinkUtils.kafkaProps.getProperty("topic"),
													 null, now, now,  data);
		try{
			_producer.send(record
				, new Callback() {
				@Override
				public void onCompletion(RecordMetadata metaData, Exception ex) {
					if (ex != null) {
						_log.log(Level.SEVERE, ex.getMessage());
					}
				}
			});
			persisted = true;
		}catch(Exception ex ){
			_log.log(Level.SEVERE, ex.getMessage());
		}
		return persisted;
	}
}
