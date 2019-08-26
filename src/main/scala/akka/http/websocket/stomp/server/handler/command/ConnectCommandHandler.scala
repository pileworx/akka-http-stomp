package akka.http.websocket.stomp.server.handler.command

import akka.actor.ActorRef
import akka.http.websocket.stomp.parser.{ConnectedFrame, ErrorFrame, FrameException, StompFrame, StompHeader}
import akka.http.websocket.stomp.server.channel.User

case class ConnectCommandHandler() extends CommandHandler {

  def handle(frame: StompFrame, clientConnection: ActorRef): Unit = {
    try {
      val headers = Seq(
        versionHeader(frame),
        StompHeader("heart-beat", "0,0"))

      val body: Option[String] = None

      clientConnection ! ConnectedFrame(headers, body)

    } catch {
      case e: FrameException => clientConnection ! ErrorFrame(e.getMessage)
    }
  }

  private[this] def versionHeader(frame: StompFrame) = frame.header("accept-version") match {
    case Some(vh: StompHeader) => StompHeader("version", version(vh))
    case None => throw FrameException("No version header found for CONNECT")
  }

  private[this] def authenticate(frame: StompFrame, clientConnection: ActorRef): Unit =
    frame.header("login").foreach { login => clientConnection ! User(login.value) }

  private[this] def version(vh: StompHeader) =
    if (vh.value.contains(ConnectCommandHandler.commaSeparated))
      vh.value.split(ConnectCommandHandler.commaSeparated).max
    else
      vh.value
}

object ConnectCommandHandler {
  val commaSeparated = ","
}