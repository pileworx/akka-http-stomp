package akka.http.websocket.stomp.parser

import akka.http.websocket.stomp.parser.StompCommand._

case class StompHeader(name: String, value: String)

sealed trait StompFrame {

    val command: StompCommand
    val headers: Option[Seq[StompHeader]]
    val body: Option[String]

    def getHeader(name: String): Option[StompHeader] = headers match {
        case Some(hl) => hl.collectFirst { case h if h.name.equalsIgnoreCase(name) => h }
        case None => None
    }
}

object StompFrame {
    val terminator = "\u0000"

    def errorOnBody(command: StompCommand, body: Option[String]): Unit = {
        if(body.nonEmpty)
            throw FrameException(s"Command $command must not contain a body.")
    }

    def create(command: StompCommand, headers: Option[Seq[StompHeader]], body: Option[String]): StompFrame = {
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

sealed trait StompClientFrame extends StompFrame
sealed trait StompServerFrame extends StompFrame

case class ConnectFrame(command: StompCommand,
                        headers: Option[Seq[StompHeader]],
                        body: Option[String]) extends StompClientFrame

object ConnectFrame {
    def apply(headers: Option[Seq[StompHeader]], body: Option[String]): ConnectFrame = {
        StompFrame.errorOnBody(CONNECT, body)
        ConnectFrame(CONNECT, headers, None)
    }
}

case class ConnectedFrame(command: StompCommand,
                          headers: Option[Seq[StompHeader]],
                          body: Option[String]) extends StompServerFrame

object ConnectedFrame {
    def apply(headers: Option[Seq[StompHeader]], body: Option[String]): ConnectedFrame = {
        StompFrame.errorOnBody(CONNECTED, body)
        ConnectedFrame(CONNECTED, headers, None)
    }
}

case class DisconnectFrame(command: StompCommand,
                           headers: Option[Seq[StompHeader]],
                           body: Option[String]) extends StompClientFrame

object DisconnectFrame {
    def apply(headers: Option[Seq[StompHeader]], body: Option[String]): DisconnectFrame = {
        StompFrame.errorOnBody(DISCONNECT, body)
        DisconnectFrame(DISCONNECT, headers, None)
    }
}

case class SendFrame(command: StompCommand,
                     headers: Option[Seq[StompHeader]],
                     body: Option[String]) extends StompClientFrame

object SendFrame {
    def apply(headers: Option[Seq[StompHeader]], body: Option[String]): SendFrame = {
        SendFrame(SEND, headers, body)
    }
}

case class SubscribeFrame(command: StompCommand,
                          headers: Option[Seq[StompHeader]],
                          body: Option[String]) extends StompClientFrame

object SubscribeFrame {
    def apply(headers: Option[Seq[StompHeader]], body: Option[String]): SubscribeFrame = {
        SubscribeFrame(SUBSCRIBE, headers, None)
    }
}

case class UnsubscribeFrame(command: StompCommand,
                            headers: Option[Seq[StompHeader]],
                            body: Option[String]) extends StompClientFrame

object UnsubscribeFrame {
    def apply(headers: Option[Seq[StompHeader]], body: Option[String]): UnsubscribeFrame = {
        UnsubscribeFrame(UNSUBSCRIBE, headers, None)
    }
}

case class MessageFrame(command: StompCommand,
                        headers: Option[Seq[StompHeader]],
                        body: Option[String]) extends StompServerFrame

object MessageFrame {
    def apply(headers: Option[Seq[StompHeader]], body: Option[String]): MessageFrame = {
        MessageFrame(MESSAGE, headers, body)
    }
}

case class ReceiptFrame(command: StompCommand,
                        headers: Option[Seq[StompHeader]],
                        body: Option[String]) extends StompServerFrame

object ReceiptFrame {
    def apply(headers: Option[Seq[StompHeader]]): ReceiptFrame = {
        ReceiptFrame(RECEIPT, headers, None)
    }
}

case class ErrorFrame(command: StompCommand,
                      headers: Option[Seq[StompHeader]],
                      body: Option[String]) extends StompServerFrame

object ErrorFrame {
    def apply(msg: String): ErrorFrame = {
        ErrorFrame(ERROR, Some(Seq(StompHeader("content-type", "text/plain"))), Some(msg))
    }
}

case class FrameException(msg: String) extends RuntimeException(msg)