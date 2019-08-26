package akka.http.websocket.stomp.parser

case class StompHeader(name: String, value: String)

object StompHeader {
  val acceptVersion: String = "accept-version"
  val ack: String = "ack"
  val contentLength: String = "content-length"
  val contentType: String = "content-type"
  val destination: String = "destination"
  val heartBeat: String = "heart-beat"
  val host: String = "host"
  val id: String = "id"
  val login: String = "login"
  val message: String = "message"
  val messageId: String = "message-id"
  val passcode: String = "passcode"
  val receipt: String = "receipt"
  val receiptId: String = "receipt-id"
  val server: String = "server"
  val session: String = "session"
  val subscription: String = "subscription"
  val transaction: String = "transaction"
  val version: String = "version"

  def containsHeaders(required: Seq[String], headers: Seq[StompHeader]): Boolean = {
    val shs = headers.map { sh => sh.name }
    required.collectFirst { case r if !shs.contains(r) => false }.getOrElse(true)
  }
}