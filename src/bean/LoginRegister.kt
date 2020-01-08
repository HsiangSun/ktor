package cn.hsiangsun.bean

//class User (var name:String,var password:String)

//模拟数据库数据
/*var Users = Collections.synchronizedMap(
    listOf(User("admin","123456")).associateBy { it.name }.toMutableMap()
)*/

//用户登陆时使用的实体类
class LoginRegister(var user:String,var password:String)