package akka.http.websocket.stomp.server.channel.command

case class Subscribe(topic: String, id: String)
case class Unsubscribe(topic: String, id: String)