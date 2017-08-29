package com.microhttp.sink.cassandra;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.microhttp.sink.persistence.PersistenceStoreType;
import com.microhttp.sink.utils.SinkUtils;

/**
 * Cassandra API to insert values into stream table 
 * @author senthilec566
 *
 */
public class CassandraPersistenceImpl implements PersistenceStoreType {
	private final static Logger _log = Logger.getLogger(CassandraPersistenceImpl.class.getName());
	private final String TABLE_NM=SinkUtils.cassandraProps.getProperty("keyspace")+"."+SinkUtils.cassandraProps.getProperty("table");
	
	/**
	 * Inserts data into cassandra table 
	 */
	@Override
	public boolean persist(String data) {
		boolean processed = false;
		// revisit to set data dynamically 
		StringBuilder sb = new StringBuilder("INSERT INTO ")
			      .append(TABLE_NM).append("(arrival_time, log) ")
			      .append("VALUES (").append(Instant.now().toEpochMilli())
			      .append(", '").append(data).append("');");
		try{
			CassandraConnector.INSTANCE.getSession().execute(sb.toString()); 
			processed = true;
		}catch(Exception ex ){
			_log.log(Level.SEVERE, ex.getMessage());
		}
		return processed;
	}
}
