package cn.hsiangsun.bean

class User (var name:String,var password:String)

//模拟数据库数据
/*var Users = Collections.synchronizedMap(
    listOf(User("admin","123456")).associateBy { it.name }.toMutableMap()
)*/

class LoginRegister(var user:String,var password:String)