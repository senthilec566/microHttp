## What is μicroHttp?
  Small light weight JVM based http(s) server build on top of Netty Engine

## How it works ?
   μicroHttp Server accepts events from multiple sources , validates , process and respond to clients
   
   ![WorkFlow](https://git.source.akamai.com/projects/PERFAN/repos/microhttp/browse/images/microHttp_DataFlow.png?raw=true "Data Flow")

## Supported DataStores 
   It supports Kafka and Cassandra Store 

## Benchmarking Result (Kafka) :

    ./wrk -c 2000 -d 5m  -t 500  -s scripts/post_data.lua  -R 1000000 http://localhost:8009/
    Running 5m test @ http://localhost:8009/
    500 threads and 2000 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency     2.11m     1.14m    4.12m    57.84%
        Req/Sec   366.82      7.91   395.00     74.11%
        54811397 requests in 5.00m, 4.85GB read
      Requests/sec: 182814.93
      Transfer/sec:     16.56MB

## How to Build this ?
    git clone https://github.com/senthilec566/microHttp.git
    cd microhttp
    mvn clean install assembly:single

  You will find executable jar in target folder.

## Starting Server 

      java  -cp microHttp-0.0.1.jar com.microhttp.sink.DataCollector -p 8009 -t kafka -k /microhttp/kafka.properties &
      -p port  , t type ( kafka or cassandra ) -conf Kafka or Cassandra properties
      
      kafka.properties
     	bootstrap.servers=<<Servers>
		acks=1
		retries=1
		batch.size=16384
		linger.ms=0
		buffer.memory=33554432
		key.serializer=org.apache.kafka.common.serialization.LongSerializer
		value.serializer=org.apache.kafka.common.serialization.StringSerializer
		topic=<<topic> 

	  cassandra.properties
		hostname=localhost
		port=9042
		keyspace=logspace
		table=logs
		
		recommendation :  set proper memory for this server
			nohup java -Xmx4g -Xms4g -XX:MetaspaceSize=96m -XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:G1HeapRegionSize=16M -XX:MinMetaspaceFreeRatio=50 -XX:MaxMetaspaceFreeRatio=80 -cp microHttp-0.0.1.jar com.microhttp.sink.DataCollector -p 8009 -t kafka -k /microhttp/kafka.properties
