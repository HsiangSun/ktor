package cn.hsiangsun.exception

//用户登录失败
class InvalidLoginException(override var message: String = "user name or password was wrong") :Exception(message){}