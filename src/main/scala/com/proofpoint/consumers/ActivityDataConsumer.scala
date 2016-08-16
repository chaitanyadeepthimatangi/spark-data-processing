package com.proofpoint.consumers

import com.proofpoint.helper.KafkaHelper
import org.apache.spark.streaming.dstream.DStream
import org.slf4j.LoggerFactory

abstract class ActivityDataConsumer(stream: DStream[String]) {
		def logger = LoggerFactory.getLogger(this.getClass)
		logger.debug("Running activity data consumer")

		try {
	  	val batchSize = KafkaHelper.consumerBatchSize

	  	val currentBatch = collection.mutable.MutableList.empty[String]
	  	val iter = stream
			iter.foreachRDD( rdd => {
				if(rdd.count() > 0){
					val msg : List[String] = rdd.collect().toList
					currentBatch ++=  msg
					if (currentBatch.size >= batchSize) {
						handleMessages(currentBatch)
						currentBatch.clear()
						if (currentBatch.nonEmpty) {
							logger.debug("Batch did not end up empty.  Boo!")
						}
					}
				}
			})

	  // Presumably this consumer has been shut down; flush what's left of the batch
	  if (currentBatch.nonEmpty) {
		  logger.debug("Handling final batch of messages")
		  handleMessages(currentBatch)
	  }

	  logger.info("Nothing left to process; leaving consumer")
	} catch {
	  case t: Throwable => logger.error("Error consuming from Kafka topic", t)
	}

	logger.debug("Shutting down.  No more messages")

  def handleMessages(messages: Seq[String])
}