package cn.hsiangsun.bean

import java.util.*

class User (var name:String,var password:String)

var Users = Collections.synchronizedMap(
    listOf(User("admin","123456")).associateBy { it.name }.toMutableMap()
)

class LoginRegister(var user:String,var password:String)