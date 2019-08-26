package akka.http.websocket.stomp.parser

import org.scalatest.{Matchers, WordSpec}
import akka.http.websocket.stomp.parser.StompCommand._

class StompFrameSpec extends WordSpec with Matchers {

  val validBody = Some("{\"foo\":\"bar\"}")
  val headers: Seq[StompHeader] = Seq(
    StompHeader("version", "1.2"),
    StompHeader("content-type", "application/json")
  )

  "StompFrame getHeader" should {

    "return Some header if header exists" in {
      val frame = ConnectedFrame(headers, None)
      frame.header("content-type").get.value should equal("application/json")
    }

    "return None if header does not exists" in {
      val frame = ConnectedFrame(headers, None)
      frame.header("destination") should equal(None)
    }

    "return None if headers do not exists" in {
      val frame = ConnectedFrame(Seq(StompHeader("version", "1.2")), None)
      frame.header("destination") should equal(None)
    }
  }

  "StompFrame terminator" should {

    "return unicode null" in {
      StompFrame.terminator should equal("\u0000")
    }
  }

  "StompFrame errorOnBody" should {

    "throw an exception if body exists" in {
      a [FrameException] should be thrownBy {
        StompFrame.errorOnBody(CONNECT, Some("body"))
      }
    }

    "do nothing if body id None" in {
      StompFrame.errorOnBody(CONNECT, None)
    }
  }

  "StompFrame create" should {

    "create a connect frame if CONNECT is the command" in {
      val connect = StompFrame.create(CONNECT, Seq(StompHeader("accept-version", "1.0,1.1,1.2"), StompHeader("host", "localhost")), None)

      connect.command should equal(CONNECT)
    }

    "create a disconnect frame if DISCONNECT is the command" in {
      val connect = StompFrame.create(DISCONNECT, Seq(), None)

      connect.command should equal(DISCONNECT)
    }

    "create a send frame if SEND is the command" in {
      val connect = StompFrame.create(SEND, Seq(StompHeader("destination", "/topic")), None)

      connect.command should equal(SEND)
    }

    "create a subscribe frame if SUBSCRIBE is the command" in {
      val connect = StompFrame.create(SUBSCRIBE, Seq(StompHeader("destination", "/topic"),StompHeader("id", "1234")), None)

      connect.command should equal(SUBSCRIBE)
    }

    "create a error frame if frame does not match" in {
      val connect = StompFrame.create(CONNECTED, Seq(), None)

      connect.command should equal(ERROR)
    }
  }
}