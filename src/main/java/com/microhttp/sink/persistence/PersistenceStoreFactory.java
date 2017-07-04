package com.microhttp.sink.persistence;

import com.microhttp.sink.cassandra.CassandraPersistenceImpl;
import com.microhttp.sink.kafka.KafkaPersistenceImpl;

public class PersistenceStoreFactory {

	public static String persistencType = "kafka" ;
	
	public static PersistenceStoreType getPersistenceType() {
		PersistenceStoreType type = null;
		if(persistencType.equalsIgnoreCase("kafka")){
			type = new KafkaPersistenceImpl();
		}else if( persistencType.equalsIgnoreCase("cassandra")){
			type = new CassandraPersistenceImpl();
		}
		if( type == null )
			type = new KafkaPersistenceImpl();
		
		return type; // default return Kafka Persistence 
	}
}
