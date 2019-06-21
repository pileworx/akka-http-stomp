package akka.http.websocket.stomp.server.channel

import akka.http.websocket.stomp.bus.event.LocalEventBus

import scala.collection.mutable

object ChannelRegistry {

  private val topicReg: mutable.Map[String, StompEventBus] = mutable.Map()

  def topics(channel: Seq[String]): Unit = topicReg.synchronized {
    channel.filter(c => !hasTopic(c)).foreach(c => topicReg += c -> new LocalEventBus(c))
  }
  def hasTopic(channel: String): Boolean = topicReg.contains(channel)
  def getTopic(channel: String): Option[StompEventBus] = topicReg.get(channel)
}
