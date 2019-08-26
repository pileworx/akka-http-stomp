package akka.http.websocket.stomp.bus.event

import akka.http.websocket.stomp.parser.{MessageFrame, SendFrame, StompHeader}
import org.scalatest.{Matchers, WordSpec}

class MessageEventSpec extends WordSpec with Matchers {

  private val destination = "/topic/a"
  private val contentType = "application/json"
  private val body = "{\"foo\":\"bar\"}"
  private val subscriptionId = "77"

  "MessageEvent apply" should {

    "return a valid MessageEvent given a valid SendFrame" in {
      val headers = Seq(
        StompHeader("destination", destination),
        StompHeader("content-type", contentType)
      )
      val send = SendFrame(headers, Some(body))

      val event = MessageEvent(send).withSubscriptionId(subscriptionId)
      val frame = event.frame

      event.destination should equal(destination)
      event.user should equal(None)
      frame.body.get should equal(body)
      frame.getHeader("destination").get.value should equal(destination)
      frame.getHeader("content-type").get.value should equal(contentType)
      frame.getHeader("subscription").get.value should equal(subscriptionId)
      frame.getHeader("message-id").get.value should fullyMatch regex """([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}){1}"""
    }

    "return a MessageEvent missing with an empty destination if the send frame is missing a destination" in {
      val headers = Seq(
        StompHeader("destination", ""),
        StompHeader("content-type", contentType)
      )
      val send = SendFrame(headers, Some(body))

      val frame = MessageEvent(send).withSubscriptionId(subscriptionId).frame

      frame.getHeader("destination").get.value should equal("")
    }

    "return a MessageEvent with a default content-type if the send frame is missing content-type" in {
      val headers = Seq(
        StompHeader("destination", destination)
      )
      val send = SendFrame(headers, Some(body))

      val frame = MessageEvent(send).withSubscriptionId(subscriptionId).frame

      frame.getHeader("content-type").get.value should equal("text/plain")
    }
  }

  "MessageEvent withSubscriptionId" should {

    "append subscription header if headers exist" in {
      val headers = Seq(
        StompHeader("destination", destination),
        StompHeader("content-type", contentType),
        StompHeader("message-id", "1234")
      )
      val event = MessageEvent(destination, headers, Some(body), None)
      val frame = event.withSubscriptionId(subscriptionId).frame

      frame.getHeader("content-type").get.value should equal(contentType)
      frame.getHeader("subscription").get.value should equal(subscriptionId)
    }

    "create subscription header if headers no not exist" in {
      val headers = Seq(
        StompHeader("destination", destination),
        StompHeader("message-id", "1234")
      )
      val event = MessageEvent(destination, headers, Some(body), None)
      val frame = event.withSubscriptionId(subscriptionId).frame

      frame.getHeader("subscription").get.value should equal(subscriptionId)
    }
  }
}