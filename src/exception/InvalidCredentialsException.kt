package cn.hsiangsun.exception

//用户身份验证错误异常
class InvalidCredentialsException(message:String = "Invalid token") : Exception(message){}