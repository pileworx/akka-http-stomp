package akka.http.websocket.stomp.parser

import akka.http.websocket.stomp.parser.StompCommand._
import akka.http.websocket.stomp.parser.StompHeader._

sealed trait StompFrame {

    val command: StompCommand
    val headers: Seq[StompHeader]
    val body: Option[String]

    def getHeader(name: String): Option[StompHeader] =
        headers.collectFirst { case h if h.name.equalsIgnoreCase(name) => h }
}

object StompFrame {
    val terminator = "\u0000"

    def errorOnBody(command: StompCommand, body: Option[String]): Unit = {
        if(body.nonEmpty)
            throw FrameException(s"Command $command must not contain a body.")
    }

    def validateHeaders(command: StompCommand, required: Seq[String], headers: Seq[StompHeader]): Unit = {
        if(!StompHeader.containsHeaders(required, headers))
            throw FrameException(s"Command $command must contain headers ${required.mkString(", ")}, only found ${headers.map(sh => sh.name).mkString(", ")}")
    }

    def create(command: StompCommand, headers: Seq[StompHeader], body: Option[String]): StompFrame = {
        command match {
            case CONNECT => ConnectFrame(headers, body)
            case DISCONNECT => DisconnectFrame(headers, body)
            case SEND => SendFrame(headers, body)
            case SUBSCRIBE => SubscribeFrame(headers, body)
            case UNSUBSCRIBE => UnsubscribeFrame(headers, body)
            case _ => ErrorFrame(s"Command $command is not supported.")
        }
    }
}

import StompFrame._

sealed trait StompClientFrame extends StompFrame
sealed trait StompServerFrame extends StompFrame

case class ConnectFrame(command: StompCommand,
                        headers: Seq[StompHeader],
                        body: Option[String]) extends StompClientFrame

object ConnectFrame {
    def apply(headers: Seq[StompHeader], body: Option[String]): ConnectFrame = {
        validateHeaders(CONNECT, Seq(acceptVersion, host), headers)
        errorOnBody(CONNECT, body)
        ConnectFrame(CONNECT, headers, None)
    }
}

case class ConnectedFrame(command: StompCommand,
                          headers: Seq[StompHeader],
                          body: Option[String]) extends StompServerFrame

object ConnectedFrame {
    def apply(headers: Seq[StompHeader], body: Option[String]): ConnectedFrame = {
        validateHeaders(CONNECT, Seq(version), headers)
        errorOnBody(CONNECTED, body)
        ConnectedFrame(CONNECTED, headers, None)
    }
}

case class DisconnectFrame(command: StompCommand,
                           headers: Seq[StompHeader],
                           body: Option[String]) extends StompClientFrame

object DisconnectFrame {
    def apply(headers: Seq[StompHeader], body: Option[String]): DisconnectFrame = {
        errorOnBody(DISCONNECT, body)
        DisconnectFrame(DISCONNECT, headers, None)
    }
}

case class SendFrame(command: StompCommand,
                     headers: Seq[StompHeader],
                     body: Option[String]) extends StompClientFrame

object SendFrame {
    def apply(headers: Seq[StompHeader], body: Option[String]): SendFrame = {
        validateHeaders(SEND, Seq(destination), headers)
        SendFrame(SEND, headers, body)
    }
}

case class SubscribeFrame(command: StompCommand,
                          headers: Seq[StompHeader],
                          body: Option[String]) extends StompClientFrame

object SubscribeFrame {
    def apply(headers: Seq[StompHeader], body: Option[String]): SubscribeFrame = {
        validateHeaders(SUBSCRIBE, Seq(destination, id), headers)
        SubscribeFrame(SUBSCRIBE, headers, None)
    }
}

case class UnsubscribeFrame(command: StompCommand,
                            headers: Seq[StompHeader],
                            body: Option[String]) extends StompClientFrame

object UnsubscribeFrame {
    def apply(headers: Seq[StompHeader], body: Option[String]): UnsubscribeFrame = {
        validateHeaders(UNSUBSCRIBE, Seq(id), headers)
        UnsubscribeFrame(UNSUBSCRIBE, headers, None)
    }
}

case class MessageFrame(command: StompCommand,
                        headers: Seq[StompHeader],
                        body: Option[String]) extends StompServerFrame

object MessageFrame {
    def apply(headers: Seq[StompHeader], body: Option[String]): MessageFrame = {
        validateHeaders(MESSAGE, Seq(destination, messageId, subscription), headers)
        MessageFrame(MESSAGE, headers, body)
    }
}

case class ReceiptFrame(command: StompCommand,
                        headers: Seq[StompHeader],
                        body: Option[String]) extends StompServerFrame

object ReceiptFrame {
    def apply(headers: Seq[StompHeader]): ReceiptFrame = {
        validateHeaders(RECEIPT, Seq(receiptId), headers)
        ReceiptFrame(RECEIPT, headers, None)
    }
}

case class ErrorFrame(command: StompCommand,
                      headers: Seq[StompHeader],
                      body: Option[String]) extends StompServerFrame

object ErrorFrame { 
    def apply(msg: String, headers: Seq[StompHeader] = Seq()): ErrorFrame = {
        ErrorFrame(ERROR, Seq(StompHeader("message", msg)) ++ headers, None)
    }
}

case class FrameException(msg: String) extends RuntimeException(msg)