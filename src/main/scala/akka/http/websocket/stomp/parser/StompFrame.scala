package akka.http.websocket.stomp.parser

import akka.http.websocket.stomp.parser.StompCommand._

case class StompHeader(name: String, value: String)

case class StompFrame(command: StompCommand,
                      headers: Option[Seq[StompHeader]],
                      body: Option[String]) {

    def getHeader(name: String): Option[StompHeader] = headers match {
        case Some(hl) => hl.collectFirst { case h if h.name.equalsIgnoreCase(name) => h }
        case None => None
    }
}

object StompFrame {
    val terminator = "\u0000"

    def apply(command: StompCommand,
              headers: Option[Seq[StompHeader]],
              body: Option[String]): StompFrame = {
        val frame = new StompFrame(command, headers, body)
        validateFrame(frame)
    }

    def errorFrame(msg: String): StompFrame = {
        StompFrame(ERROR, Some(Seq(StompHeader("content-type", "text/plain"))), Some(msg))
    }

    private def validateFrame(frame: StompFrame): StompFrame = {
        validateBody(frame)
        frame
    }

    private def validateBody(frame: StompFrame): Unit = {
        if(frame.command != SEND && frame.command != ERROR && frame.body.nonEmpty)
            throw FrameException(s"Command ${frame.command} must not contain a body.")
    }
}

case class FrameException(msg: String) extends RuntimeException(msg)


