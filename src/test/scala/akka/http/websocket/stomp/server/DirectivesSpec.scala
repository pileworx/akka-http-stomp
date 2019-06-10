package akka.http.websocket.stomp.server

import java.util.UUID

import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import akka.http.scaladsl.server.Directives._
import org.scalatest.{Matchers, WordSpec}
import Directives._
import akka.http.websocket.stomp.server.channel.StompChannel
import akka.http.websocket.stomp.test.util.FakeChannel

import scala.concurrent.duration._

class DirectivesSpec extends WordSpec with Matchers with ScalatestRouteTest {
  private val channelPath = "/queue/a"

  private val receiptId = UUID.randomUUID().toString

  private val connectFrame = "CONNECT\naccept-version:1.0,1.1,1.2\nheart-beat:4000,4000\n\n\u0000"
  private val connectedFrame = "CONNECTED\nversion:1.2\nheart-beat:0,0\n\n\u0000"

  private val disconnectFrame = s"DISCONNECT\nreceipt:$receiptId\n\n\u0000"
  private val receiptFrame = s"RECEIPT\nreceipt-id:$receiptId\n\n\u0000"

  private val sendFrame = s"SEND\ndestination:$channelPath\ncontent-type:text/plain\n\nthis is my body\u0000"

  private val fakeChannel = system.actorOf(FakeChannel.props)

  private val stompRoute = path("stomp") { stomp }

  "STOMP Directive" should {

    StompChannel.addChannel(channelPath -> fakeChannel)

    "return a connected frame when a valid connect frame is received" in {

      val wsClient = WSProbe()

      WS("/stomp", wsClient.flow, List("v12.stomp")) ~> stompRoute ~> check {

        isWebSocketUpgrade shouldEqual true

        wsClient.sendMessage(connectFrame)
        wsClient.expectMessage(connectedFrame)
      }
    }

    "return a receipt frame when a valid disconnect frame is received" in {

      val wsClient = WSProbe()

      WS("/stomp", wsClient.flow, List("v12.stomp")) ~> stompRoute ~> check {

        isWebSocketUpgrade shouldEqual true

        wsClient.sendMessage(disconnectFrame)
        wsClient.expectMessage(receiptFrame)
      }
    }

    "accept a send command after connecting" in {

      val wsClient = WSProbe()

      WS("/stomp", wsClient.flow, List("v12.stomp")) ~> stompRoute ~> check {

        isWebSocketUpgrade shouldEqual true

        wsClient.sendMessage(connectFrame)
        wsClient.expectMessage(connectedFrame)

        wsClient.sendMessage(sendFrame)
        wsClient.expectNoMessage(100.millis)

        wsClient.sendMessage(disconnectFrame)
        wsClient.expectMessage(receiptFrame)
      }
    }
  }
}
