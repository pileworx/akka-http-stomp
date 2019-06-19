package akka.http.websocket.stomp.parser

import org.scalatest.{Matchers, WordSpec}

class FrameWriterSpec extends WordSpec with Matchers {

  private val destination = "/topic/a"
  private val contentType = "application/json"
  private val body = "{\"foo\":\"bar\"}"
  private val headers = Seq(
    StompHeader("destination", destination),
    StompHeader("content-type", contentType)
  )

  private val serializedFull = s"SEND\ndestination:$destination\ncontent-type:$contentType\n\n${body}\u0000"
  private val serializedNoHeaders = s"SEND\n\n${body}\u0000"
  private val serializedNoBody = s"SEND\ndestination:$destination\ncontent-type:$contentType\n\n\u0000"

  "FrameWriter write" should {

    "serialize a frame class into a string with headers and body" in {
      val frame = SendFrame(Some(headers), Some(body))

      val serialized = new FrameWriter().write(frame)

      serialized should equal(serializedFull)
    }

    "serialize a frame class into a string without headers" in {
      val frame = SendFrame(None, Some(body))

      val serialized = new FrameWriter().write(frame)

      serialized should equal(serializedNoHeaders)
    }

    "serialize a frame class into a string without a body" in {
      val frame = SendFrame(Some(headers), None)

      val serialized = new FrameWriter().write(frame)

      serialized should equal(serializedNoBody)
    }
  }
}
