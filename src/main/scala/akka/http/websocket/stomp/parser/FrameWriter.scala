package akka.http.websocket.stomp.parser

class FrameWriter {
  def write(frame: StompFrame): String = {
    val headers = frame.headers
      .map(h => s"${h.name}:${h.value}")
      .mkString(FrameWriter.newLine, FrameWriter.newLine, FrameWriter.emptyString)
    val body = frame.body match {
      case Some(b: String) => b
      case None => FrameWriter.emptyString
    }
    s"${frame.command.toString}$headers\n\n$body${StompFrame.terminator}"
  }
}

object FrameWriter {
  private val newLine: String = "\n"
  private val emptyString: String = ""
}