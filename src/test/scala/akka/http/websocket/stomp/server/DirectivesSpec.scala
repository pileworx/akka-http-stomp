package akka.http.websocket.stomp.server

import java.util.UUID

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import akka.http.websocket.stomp.server.Directives._
import org.scalatest.{Matchers, WordSpec}
import scala.concurrent.duration._

class DirectivesSpec extends WordSpec with Matchers with ScalatestRouteTest {
  private val channelPath = "/topic/a"

  private val receiptId = UUID.randomUUID().toString

  private val connectFrame = "CONNECT\naccept-version:1.0,1.1,1.2\nheart-beat:4000,4000\n\n\u0000"
  private val connectedFrame = "CONNECTED\nversion:1.2\nheart-beat:0,0\n\n\u0000"

  private val disconnectFrame = s"DISCONNECT\nreceipt:$receiptId\n\n\u0000"
  private val receiptFrame = s"RECEIPT\nreceipt-id:$receiptId\n\n\u0000"

  private val sendFrame = s"SEND\ndestination:$channelPath\ncontent-type:text/plain\n\nthis is my body\u0000"

  private val subscribeFrame = s"SUBSCRIBE\ndestination:$channelPath\nid:77\n\n\u0000"

  private val stompRoute = path("stomp") {
    stomp(
      topics = Seq(
        channelPath
      )
    )
  }

  "STOMP Directive" should {

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

    "accept a subscribe request and publish to the channel" in {

      val wsClient = WSProbe()

      WS("/stomp", wsClient.flow, List("v12.stomp")) ~> stompRoute ~> check {

        isWebSocketUpgrade shouldEqual true

        wsClient.sendMessage(connectFrame)
        wsClient.expectMessage(connectedFrame)

        wsClient.sendMessage(subscribeFrame)
        wsClient.expectNoMessage(100.millis)

        wsClient.sendMessage(sendFrame)
        val message = wsClient.expectMessage().toString
        message should include("MESSAGE\n")
        message should include("subscription:77\n")
        message should include("message-id:")
        message should include(s"destination:$channelPath\n")
        message should include("content-type:text/plain\n")
        message should include("\n\nthis is my body\u0000")

        wsClient.sendMessage(disconnectFrame)
        wsClient.expectMessage(receiptFrame)
      }
    }
  }
}
