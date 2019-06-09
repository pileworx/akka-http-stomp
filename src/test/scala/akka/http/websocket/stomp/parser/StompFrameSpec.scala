package akka.http.websocket.stomp.parser

import akka.http.websocket.stomp.parser.StompCommand._
import org.scalatest.{Matchers, WordSpec}

class StompFrameSpec extends WordSpec with Matchers {

  val validBody = Some("{\"foo\":\"bar\"}")
  val headers = Some(Seq(StompHeader("Content-Type", "application/json")))

  "StompFrame" should {

    "return a StompFrame from apply" in {
      val frame = StompFrame(SEND, headers, validBody)

      frame.headers shouldBe headers
      frame.command shouldBe SEND
      frame.body shouldBe validBody
    }

    "return a StompFrame from apply with no body" in {
      val frame = StompFrame(CONNECT, headers, None)

      frame.headers shouldBe headers
      frame.command shouldBe CONNECT
      frame.body shouldBe None
    }

    "throw an exception if body is present and command is not send" in {
      a [FrameException] should be thrownBy {
        StompFrame(CONNECT, headers, validBody)
      }
    }
  }
}
