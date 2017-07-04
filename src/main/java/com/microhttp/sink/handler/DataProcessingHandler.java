package com.microhttp.sink.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.microhttp.sink.persistence.PersistenceStoreFactory;
import com.microhttp.sink.persistence.PersistenceStoreType;

/**
 * Data Processing Handler 
 * Initialize respective Persistent Store and Forwards it to them
 * Error handling and respond to client either success or failure 
 * Later retry will be added in this API
 * @author skalaise
 *
 */
public class DataProcessingHandler {

	private final static Logger _log = Logger.getLogger(DataProcessingHandler.class.getName());
	// persistent store - kafka or cassandra 
	final static PersistenceStoreType persistenceType = PersistenceStoreFactory.getPersistenceType();

	/**
	 * Routes to persistent store
	 * @param data
	 * @return
	 */
	public static boolean processIncomingData( final String data ) {
		if( data == null ) return false;
		boolean isProcessed = false;
		try{
			isProcessed = persistenceType.persist(data);
		}catch( Exception ex ){
			_log.log(Level.SEVERE, ex.getMessage());
		}
		return isProcessed;
	}
}
