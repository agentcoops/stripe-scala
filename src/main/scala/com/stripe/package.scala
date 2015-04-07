package com

import org.apache.http.conn.ClientConnectionManager

package object stripe {
  var apiKey: String = ""
  var platformKey: String = ""
  var connectionManager: ClientConnectionManager = null
}
