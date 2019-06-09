package akka.http.websocket.stomp.server.handler.command

import akka.http.websocket.stomp.parser.{FrameException, StompFrame, StompHeader}
import akka.http.websocket.stomp.parser.StompCommand._

case class ConnectCommandHandler() extends CommandHandler {
  override def handle(frame: StompFrame): StompFrame = {

    try {
      val headers: Option[Seq[StompHeader]] = Some(Seq(
        getVersionHeader(frame),
        StompHeader("heart-beat", "0,0")))

      val body: Option[String] = None

      StompFrame(CONNECTED, headers, body)

    } catch {
      case e: FrameException => StompFrame.errorFrame(e.getMessage)
    }
  }

  private def getVersionHeader(frame: StompFrame) = frame.getHeader("accept-version") match {
    case Some(vh: StompHeader) => StompHeader("version", getVersion(vh))
    case None => throw FrameException("No version header found for CONNECT")
  }

  private def getVersion(vh: StompHeader) = {
    if (vh.value.contains(","))
      vh.value.split(",").max
    else
      vh.value
  }
}