package com.appspot.mondayflicks.util

import com.google.appengine.api.users.User

trait NonEmailNickname {
  def nonEmailNickname(user: User) = user.getNickname.takeWhile(_ != '@')
}
