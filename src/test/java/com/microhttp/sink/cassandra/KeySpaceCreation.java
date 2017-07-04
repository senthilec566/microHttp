package com.microhttp.sink.cassandra;

import com.datastax.driver.core.Session;

public class KeySpaceCreation {

	public void createKeyspace( Session session, 
			String keyspaceName, String replicationStrategy, int replicationFactor) {
			StringBuilder sb = new StringBuilder("CREATE KEYSPACE IF NOT EXISTS ")
			.append(keyspaceName).append(" WITH replication = {")
			.append("'class':'").append(replicationStrategy)
			.append("','replication_factor':").append(replicationFactor)
			.append("};");
			String query = sb.toString();
			session.execute(query);
			System.out.println(" Query Executed Succesfully ");
			}
	
	public void createTable(Session session, String tableName ) {
	    StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
	      .append(tableName).append("(")
	      .append("arrival_time timestamp PRIMARY KEY, ")
	      .append("log text);");
	 
	    String query = sb.toString();
	    session.execute(query);
	    System.out.println(" Query Executed Succesfully ");
	}
	
	public static void main(String[] args) {
		KeySpaceCreation kS = new KeySpaceCreation();
		CassandraConnectorTest connector = new CassandraConnectorTest();
    	connector.connect("localhost", 9042);
    	Session session = connector.getSession() ;
    	kS.createKeyspace(session, "logspace", "SimpleStrategy", 1);
    	kS.createTable(session, "logspace.logs");
	}
}
