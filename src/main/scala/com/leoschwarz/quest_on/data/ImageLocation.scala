// Copyright 2014-2016 Leonardo Schwarz (leoschwarz.com)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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