package com.microhttp.sink.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Cluster.Builder;
import com.microhttp.sink.utils.SinkUtils;

/**
 * Cassandra Server Connector Singleton instance 
 * @author skalaise
 *
 */
public enum CassandraConnector {
INSTANCE;
	
	private Cluster cluster;
    private Session session; // Yet to figure out how to hanlde Session in Cassandra 
    
    /**
     * Build Cluster and Create Session 
     * @return
     */
    public Session getSession() {
    	if( session == null ) {
    		String hostName = SinkUtils.cassandraProps.getProperty("hostname");
    		String port = SinkUtils.cassandraProps.getProperty("port");
    		 Builder b = Cluster.builder().addContactPoint(hostName);
    	        if (port != null) {
    	            b.withPort(Integer.parseInt(port));
    	        }
    	        cluster = b.build();
    	        session = cluster.connect();
    	}
    	return session;
    }
}
