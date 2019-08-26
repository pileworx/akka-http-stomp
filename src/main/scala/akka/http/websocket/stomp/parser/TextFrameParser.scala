package akka.http.websocket.stomp.parser

import akka.http.websocket.stomp.parser.StompCommand.StompCommand
import org.parboiled2._
import scala.util.{Failure, Success}
import shapeless.HList
import shapeless.ops.hlist.ToList

class TextFrameParser(val input: ParserInput) extends Parser with FrameParser[String] {

  def parse(): StompFrame = {
    val parsed = frameParse.run() match {
      case Success(result) => result
      case Failure(e: ParseError) => sys.error(formatError(e, new ErrorFormatter(showTraces = true)))
      case Failure(e) => throw e
    }

    val parts = flatten(parsed)

    StompFrame.create(
      parts(0).asInstanceOf[StompCommand],
      parts(1).asInstanceOf[Seq[StompHeader]],
      parts(2).asInstanceOf[Option[String]])

  }

  private def flatten[H <: HList](h: H)(implicit ev: ToList[H, Any]) = h.toList[Any]

  private def frameParse = rule {
    command ~ newLine ~ headers ~ newLine ~ newLine ~ body.? ~ terminator
  }

  private[this] def command: Rule1[StompCommand] = rule {
    capture("STOMP" | "CONNECT" | "SEND" | "SUBSCRIBE" | "UNSUBSCRIBE" | "BEGIN" | "COMMIT" | "ABORT" | "ACK" | "NACK" | "DISCONNECT") ~> ((cmd: String) => StompCommand.withName(cmd))
  }

  private[this] def headers: Rule1[Seq[StompHeader]] = rule {
    header.+(newLine)
  }

  private[this] def header: Rule1[StompHeader] =  rule {
    capture((CharPredicate.Alpha | "-").+) ~ ":" ~ capture(noneOf(TextFrameParser.rn).+) ~> ((n, v) => StompHeader(n,v))
  }

  private[this] def newLine = rule {
    TextFrameParser.rn | "\n"
  }

  private[this] def body = rule {
    capture(CharPredicate.Printable.+)
  }

  private[this] def terminator = rule {
    StompFrame.terminator
  }
}

object TextFrameParser {
  val rn = "\r\n"
}