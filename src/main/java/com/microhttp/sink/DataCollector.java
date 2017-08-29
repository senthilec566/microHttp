package com.microhttp.sink;

import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;

import com.microhttp.sink.utils.SinkUtils;


/**
 * Data Collector this accept only POST events
 * Collects data from different systems and persist in Kafka or Cassandra etc
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
