package akka.http.websocket.stomp.parser

class FrameWriter {
  def write(frame: StompFrame): String = {
    val headers = frame.headers match {
      case Some(hl: Seq[StompHeader]) => hl.map(h => s"${h.name}:${h.value}").mkString("\n", "\n", "")
      case None => ""
    }
    val body = frame.body match {
      case Some(b: String) => b
      case None => ""
    }
    s"${frame.command.toString}$headers\n\n$body^@"
  }
}
