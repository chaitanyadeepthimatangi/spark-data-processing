package com.proofpoint.streaming

import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka.KafkaUtils

object StreamingContextImpl {
  def main(args: Array[String]): Unit = {
	val config = ConfigFactory.load()
	val conf = new SparkConf()
	  .setMaster(config.getString("spark.app.master"))
	  .setAppName(config.getString("spark.app.name"))
	val ssc = new StreamingContext(conf, Seconds(10))
	ssc.checkpoint("checkpoint")
	val kafkaStream = KafkaUtils.createStream(ssc,
	  config.getString("zookeeper.connect"),
	  config.getString("consumer.group"),
	  Map(config.getString("consumer.topic") -> 5))
	kafkaStream.print()
	ssc.start
	ssc.awaitTermination()
  }
}
