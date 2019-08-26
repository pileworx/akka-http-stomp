package akka.http.websocket.stomp.server

import akka.http.websocket.stomp.server.channel.User

trait StompAuthenticationProvider {
  def authenticate(login: String, passcode: String): Option[User]
}