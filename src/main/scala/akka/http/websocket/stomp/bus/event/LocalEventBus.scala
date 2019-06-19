package akka.http.websocket.stomp.bus.event

import akka.event.{ActorEventBus, EventBus, LookupClassification}
import akka.http.websocket.stomp.server.channel.StompEventBus

class LocalEventBus(destination: String) extends StompEventBus
  with EventBus
  with LookupClassification
  with ActorEventBus {

  type Event = MessageEvent
  type Classifier = Boolean

  def mapSize = 2

  def classify(message: MessageEvent): Boolean = message.destination.equalsIgnoreCase(destination)

  protected def publish(message: MessageEvent, subscriber: Subscriber): Unit = subscriber ! message
}