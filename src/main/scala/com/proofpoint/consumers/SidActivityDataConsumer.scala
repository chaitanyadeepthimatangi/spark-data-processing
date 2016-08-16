package com.proofpoint.consumers

import com.proofpoint.domain.dao.SidActivityDAO.SidActivity
import com.proofpoint.domain.dao.SidActivityDaoImpl
import com.proofpoint.services.SidActivityServiceImpl
import net.emergingthreats.common.monitoring.Trace
import org.apache.spark.streaming.dstream.DStream
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import org.joda.time.DateTime


class SidActivityDataConsumer(stream: DStream[String])
  extends ActivityDataConsumer(stream) {

	override def logger = LoggerFactory.getLogger(this.getClass)

  @Trace(dispatcher = true)
  def handleMessages(messages: Seq[String]): Unit = {
		try {
	  	val sidEvents: Seq[SidActivity] = messages.map((msg: String) =>
	  	{
				logger.trace(msg)
				val idsEventJson = Json.parse(msg)
				SidActivity(
					idsEventJson.\("sid").as[Long],
					1L,
					idsEventJson.\("date").as[DateTime],
					new DateTime(),
					new DateTime()
				)
	  	})

	  	logger.debug("Processing SID Events")

	  	//Save if there is anything to save

			val sidActivityService = new SidActivityServiceImpl(new SidActivityDaoImpl)

	  	if (sidEvents.nonEmpty) {
				sidActivityService.save(sidEvents)
	  	}
		} catch {
	  	case e: Exception => logger.error("Error consuming from Kafka topic", e)
		}
  }
}
