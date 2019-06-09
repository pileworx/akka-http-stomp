package akka.http.websocket.stomp.parser

import org.scalatest.{Matchers, WordSpec}

class TextFrameParserSpec extends WordSpec with Matchers {

  private val connectFrame = "CONNECT\naccept-version:1.0,1.1,1.2\nheart-beat:4000,4000\n\n^@"
  private val improperFrame = "CONNECT\naccept-version:1.0,1.1,1.2\nheart-beat:4000,4000\n{\"foo\":\"bar\"}^@"

  "TextFrameParser" should {

    "parse a valid connect frame" in {

      val result = new TextFrameParser(connectFrame).parse()

      result shouldBe StompFrame(
        StompCommand.CONNECT,
        Some(Seq(
          StompHeader("accept-version", "1.0,1.1,1.2"),
          StompHeader("heart-beat", "4000,4000")
        )),
        None)
    }

    "fail if frame is improperly formatter" in {
      a [RuntimeException] should be thrownBy {
        new TextFrameParser(improperFrame).parse()
      }
    }
  }
}
