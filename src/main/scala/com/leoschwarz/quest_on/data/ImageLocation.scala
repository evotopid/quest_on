package com.leoschwarz.quest_on.data

sealed trait ImageLocation {
  def local: Option[Local]
  def remote: Option[Remote]
}

case class Local() extends ImageLocation {
  override def toString = "locally"
  override val local = Some(this)
  override val remote = None
}

case class Remote(url: String) extends ImageLocation {
  override def toString = "remote:" + url
  override val local = None
  override val remote = Some(this)
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