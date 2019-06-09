package akka.http.websocket.stomp.server

import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import akka.http.scaladsl.server.Directives._
import org.scalatest.{Matchers, WordSpec}
import Directives._

class DirectivesSpec extends WordSpec with Matchers with ScalatestRouteTest {

  private val connectFrame = "CONNECT\naccept-version:1.0,1.1,1.2\nheart-beat:4000,4000\n\n\u0000"
  private val connectedFrame = "CONNECTED\nversion:1.2\nheart-beat:0,0\n\n\u0000"

  val stompRoute =
    path("stomp") {
      stomp
    }

  val wsClient = WSProbe()

  "STOMP Directive" should {

    "return a connected frame when a valid connect frame is received" in {
      WS("/stomp", wsClient.flow, List("v12.stomp")) ~> stompRoute ~> check {

        isWebSocketUpgrade shouldEqual true

        wsClient.sendMessage(connectFrame)
        wsClient.expectMessage(connectedFrame)
      }
    }
  }
}
