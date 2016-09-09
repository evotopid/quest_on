package com.leoschwarz.quest_on

import com.leoschwarz.quest_on.data.Database

trait DatabaseAccess {
  def db: Database
}
