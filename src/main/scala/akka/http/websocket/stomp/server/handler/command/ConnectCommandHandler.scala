package akka.http.websocket.stomp.server.handler.command

import akka.actor.ActorRef
import akka.http.websocket.stomp.parser.{ConnectedFrame, ErrorFrame, FrameException, StompFrame, StompHeader}
import akka.http.websocket.stomp.server.channel.User

case class ConnectCommandHandler() extends CommandHandler {

  def handle(frame: StompFrame, clientConnection: ActorRef): Unit = {
    try {
      val headers = Seq(
        getVersionHeader(frame),
        StompHeader("heart-beat", "0,0"))

      val body: Option[String] = None

      clientConnection ! ConnectedFrame(headers, body)

    } catch {
      case e: FrameException => clientConnection ! ErrorFrame(e.getMessage)
    }
  }

  private def getVersionHeader(frame: StompFrame) = frame.getHeader("accept-version") match {
    case Some(vh: StompHeader) => StompHeader("version", getVersion(vh))
    case None => throw FrameException("No version header found for CONNECT")
  }

  private def authenticate(frame: StompFrame, clientConnection: ActorRef): Unit =
    frame.getHeader("login").foreach { login => clientConnection ! User(login.value) }

  private def getVersion(vh: StompHeader) =
    if (vh.value.contains(",")) vh.value.split(",").max else vh.value
}