package com.microhttp.sink;

import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;

import com.microhttp.sink.utils.SinkUtils;


/**
 * Data Collector this accept only POST events
 * Collects data from different systems and persist in Kafka or Cassandra etc.
 * <example>
 * Ghost or LLDC can directly send events to DataCollector 
 * POST --data {event} https://datacollector/collect/logs/
 * This server responsible for 
 * 1) Accept the incoming request from trusted source  
 * 2) Valdiate if any rules
 * 3) Write it to Kafka or Cassandra 
 * </example>
 * @author skalaise
 *
 */
public class DataCollector {

	private final static Logger _log = Logger.getLogger(DataCollector.class.getName());
	/**
	 * Entry for datacollector 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		_log.info("Starting Data Collector Server ...");
		// Parse Commands
		CommandLine cmd = SinkUtils.parseArgs(args);
		// init persistent store
		SinkUtils.initStore(cmd.getOptionValue("t"), cmd.getOptionValue("conf"));
		NettyServerConfig conf = SinkUtils.prepareConf(cmd);
		NettyServer server = new NettyServer();
		server.start(conf);
		_log.info("Server started and ready to accept events ...");
	}
	
}
