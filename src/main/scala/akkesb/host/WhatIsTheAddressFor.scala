package akkesb.host

case class WhatIsTheAddressFor(service: String, command: (String, Array[String], Array[String]))
