package akka.http.websocket.stomp.bus.event

import akka.actor.ActorSystem
import akka.http.websocket.stomp.parser.MessageFrame
import akka.testkit.{TestKit, TestProbe}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class LocalEventBusSpec extends TestKit(ActorSystem("MySpec")) with WordSpecLike with Matchers with BeforeAndAfterAll with MockFactory {

  private val channelPath = "/topic/a"

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "LocalEventBus" should {
    val frame = stub[MessageFrame]
    val event = MessageEvent(channelPath, frame, None)
    val probe1 = TestProbe()
    val probe2 = TestProbe()
    val subscriber1 = probe1.ref
    val subscriber2 = probe2.ref
    val localEventBus = new LocalEventBus(channelPath)

    "relay messages to all subscribers" in {
      localEventBus.subscribe(subscriber1, true)
      localEventBus.subscribe(subscriber2, true)
      localEventBus.publish(event)
      probe1.expectMsg(event)
      probe2.expectMsg(event)
    }
  }
}
