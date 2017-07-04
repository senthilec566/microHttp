package com.microhttp.sink.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.Session;

public class CassandraConnectorTest {
	private Cluster cluster;
	 
    private Session session;
 
    public Session connect(String node, Integer port) {
        Builder b = Cluster.builder().addContactPoint(node);
        if (port != null) {
            b.withPort(port);
        }
        cluster = b.build();
 
        session = cluster.connect();
        System.out.println("Connected Successfully");
        return session;
    }
 
    public Session getSession() {
        return this.session;
    }
 
    public void close() {
        session.close();
        cluster.close();
    }
    
    public static void main(String[] args) {
    	CassandraConnectorTest connector = new CassandraConnectorTest();
    	connector.connect("localhost", 9042);
	}
}
