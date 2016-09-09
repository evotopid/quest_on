package com.leoschwarz.quest_on.data

sealed trait ImageLocation
case class Local() extends ImageLocation {
  override def toString = "locally"
}
case class Remote(url: String) extends ImageLocation {
  override def toString = "remote:" + url
}

object ImageLocation {
  def fromString(str: String): Option[ImageLocation] = {
    if (str == "locally") {
      Some(Local())
    } else if (str.take(7) == "remote:") {
      Some(Remote(str.substring(7)))
    } else {
      None
    }
  }
}