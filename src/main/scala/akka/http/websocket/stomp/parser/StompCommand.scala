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