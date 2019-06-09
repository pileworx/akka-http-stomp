package akka.http.websocket.stomp.parser

object StompCommand extends Enumeration {
  type StompCommand = Value
  val STOMP,
      CONNECT,
      CONNECTED,
      SEND,
      SUBSCRIBE,
      UNSUBSCRIBE,
      BEGIN,
      COMMIT,
      ABORT,
      ACK,
      NACK,
      DISCONNECT,
      MESSAGE,
      RECEIPT,
      ERROR = Value
}

case class StompHeader(name: String, value: String)

import akka.http.websocket.stomp.parser.StompCommand._

case class StompFrame(command: StompCommand,
                      headers: Option[Seq[StompHeader]],
                      body: Option[String]) {

    def getHeader(name: String): Option[StompHeader] = headers match {
        case Some(hl) => hl.collectFirst { case h if h.name.equalsIgnoreCase(name) => h }
        case None => None
    }
}

object StompFrame {
    def apply(command: StompCommand,
              headers: Option[Seq[StompHeader]],
              body: String): StompFrame = {
        val frame = StompFrame(command, headers, parseBody(body))
        validateFrame(frame)
    }

    def errorFrame(msg: String) = {
        StompFrame(ERROR, Some(Seq(StompHeader("content-type", "text/plain"))), Some(msg))
    }

    private def parseBody(body: String) = {
        if(!body.contains("^@"))
            throw FrameException("No termination characters found for frame.")
        val bodyParts = body.split("\\^@")
        if(bodyParts.isEmpty) None else Some(bodyParts(0))
    }

    private def validateFrame(frame: StompFrame): StompFrame = {
        validateBody(frame)
        frame
    }

    private def validateBody(frame: StompFrame): Unit = {
        if(frame.command != SEND && frame.body.nonEmpty)
            throw FrameException(s"Command ${frame.command} must not contain a body.")
    }
}

case class FrameException(msg: String) extends RuntimeException(msg)


