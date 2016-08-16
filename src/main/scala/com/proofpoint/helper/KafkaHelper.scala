package com.proofpoint.helper

import java.util.Properties

import com.typesafe.config.ConfigFactory
import kafka.consumer.ConsumerConfig
import kafka.producer.{Producer, ProducerConfig}
import net.emergingthreats.common.functions.Inet
import org.slf4j.{Logger, LoggerFactory}

object KafkaHelper {
  val config = ConfigFactory.load()
  //val consumerBatchSize = config.getInt("kafka.consumer.batchSize")
  //val batchMaxWait = config.getLong("kafka.batch.maxWait")

  def logger = LoggerFactory.getLogger(this.getClass)

  val consumerBatchSize =
	config.getInt("kafka.consumer.batchSize")

  def buildConsumerConfig(): ConsumerConfig = {
	val props = new Properties()
	props.put("zookeeper.connect",
	  config.getString("kafka.zookeeper.address"))
	props.put("group.id",
	  config.getString("kafka.consumerGroupId"))
	props.put("zookeeper.session.timeout.ms", "10000")
	props.put("zookeeper.sync.time.ms", "200")
	props.put("auto.commit.interval.ms", "1000")
	logger.debug(props.toString)
	new ConsumerConfig(props)
  }

  def extractFirstOctet(ip: String): String = {
	val addr = Inet.parseIpAddress(ip).getHostAddress
	addr.substring(0, addr.indexOf("."))
  }
}
