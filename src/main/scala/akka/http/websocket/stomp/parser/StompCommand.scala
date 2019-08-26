package akka.http.websocket.stomp.parser

object StompCommand extends Enumeration {
  type StompCommand = Value
  val
  // Client
  STOMP,
  CONNECT,
  DISCONNECT,
  SUBSCRIBE,
  UNSUBSCRIBE,
  SEND,
  ACK,
  NACK,
  BEGIN,
  COMMIT,
  ABORT,
  // Server
  CONNECTED,
  MESSAGE,
  RECEIPT,
  ERROR = Value
}