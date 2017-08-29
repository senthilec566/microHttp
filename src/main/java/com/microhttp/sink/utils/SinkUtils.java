package com.microhttp.sink.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.microhttp.sink.NettyServerConfig;
import com.microhttp.sink.persistence.PersistenceStoreFactory;

/**
 * Utility API for Data Collector Process 
 * @author senthilec566
 *
 */
public class SinkUtils {
	
	private final static Logger _log = Logger.getLogger(SinkUtils.class.getName());
	public final static Properties cassandraProps = new Properties();
	public final static Properties kafkaProps = new Properties();
	/**
	 * Parse user arguments 
	 * @param args
	 * @return
	 */
	public static CommandLine parseArgs(String[] args) {
		_log.info("Parsing CommandLine argument ...");
		Options options = new Options();

		Option port = new Option("p", "port", true, "port to start server");
		port.setRequired(true);
		options.addOption(port);
		
		Option type = new Option("t", "type", true, "persistence type Kafka or Cassandra k - kafka  , c - cassandra ");
		type.setRequired(true);
		options.addOption(type);
		
		Option kafka = new Option("conf", "kafka or cassandra", true, "config path");
		kafka.setRequired(false);
		options.addOption(kafka);
		
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (Exception ex) {
			formatter.printHelp("DataReceiver", options);
			_log.log(Level.SEVERE, ex.getMessage());
			System.exit(1);
		}
		return cmd;
	}
	
	public static void initStore(String type , String path ) throws IOException{
		PersistenceStoreFactory.persistencType=type;
		if( type.equalsIgnoreCase("kafka"))
			_initKafkaProps(path);
		else if( type.equalsIgnoreCase("cassandra"))
			_initCassandraProps(path);
	}
	
	/**
	 * Initialize Kafka Sink properites 
	 * @param propPath
	 * @throws IOException
	 */
	public static void _initKafkaProps(String propPath) throws IOException {

		if (null == propPath || propPath.isEmpty()) {
			_log.log(Level.SEVERE, "Properties File Path is Null or Empty");
			throw new IOException(" Properties File Path is Null or Empty ");
		}
		File file = new File(propPath);
		if (!file.exists())
			throw new FileNotFoundException(" File Not Found in Path :" + propPath);
		FileInputStream fileInput = null;
		try {
			fileInput = new FileInputStream(file);
			kafkaProps.load(fileInput);
			_log.info("Succesfully loaded Kafka Properties...");
		} catch (IOException ex) {
			throw ex;
		} finally {
			fileInput.close();
		}
	}
	
	
	/**
	 * Initialize Cassandra Sink properites 
	 * @param propPath
	 * @throws IOException
	 */
	public static void _initCassandraProps(String propPath) throws IOException {

		if (null == propPath || propPath.isEmpty()) {
			_log.log(Level.SEVERE, "Properties File Path is Null or Empty");
			throw new IOException(" Properties File Path is Null or Empty ");
		}
		File file = new File(propPath);
		if (!file.exists())
			throw new FileNotFoundException(" File Not Found in Path :" + propPath);
		FileInputStream fileInput = null;
		try {
			fileInput = new FileInputStream(file);
			cassandraProps.load(fileInput);
			_log.info("Succesfully loaded Cassandra Properties...");
		} catch (IOException ex) {
			throw ex;
		} finally {
			fileInput.close();
		}
	}
	
	/**
	 * Prepare Netty Config Obj based on user inputs
	 * @param cmd
	 * @return
	 */
	public static NettyServerConfig prepareConf( CommandLine cmd )
	{
		NettyServerConfig conf = new NettyServerConfig();
		conf.setBossGroupThreads(1);  
		conf.setWorkerGroupThreads(0); // expose this to user 
		conf.setListenPort(8080);
		String port = cmd.getOptionValue("p");
		if( null != port && !port.isEmpty() )
			conf.setListenPort(Integer.parseInt(port));
		conf.setSslEnabled(false);
		conf.setHostName();
		return conf;
	}
}
