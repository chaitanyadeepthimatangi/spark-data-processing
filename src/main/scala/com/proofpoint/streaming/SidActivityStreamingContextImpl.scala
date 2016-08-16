package com.proofpoint.streaming

import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils

import com.proofpoint.consumers.SidActivityDataConsumer

import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.joda.time.DateTimeZone
import org.slf4j.LoggerFactory
import kafka.serializer.StringDecoder

object SidActivityStreamingContextImpl {
  def main(args: Array[String]): Unit = {

		def logger = LoggerFactory.getLogger(this.getClass)

		val config = ConfigFactory.load()

		val kafkaParams = Map("metadata.broker.list" -> config.getString("kafka.broker.address"))
  	val topic = config.getString("consumer.topic")

		// By default let's use UTC times
		DateTimeZone.setDefault(DateTimeZone.UTC)
		val conf = new SparkConf()
			.setMaster(config.getString("spark.app.master"))
			.setAppName(config.getString("spark.app.name"))
		val ssc = new StreamingContext(conf, Seconds(10))
		val topicsSet = topic.split(",").toSet
		val stream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)
		val lines = stream.map(_._2)
		topic match {
		     case "ids" => new SidActivityDataConsumer(lines)
		  	 case _ => None
		}
		ssc.start
		ssc.awaitTermination()

	}

}
