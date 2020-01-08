package cn.hsiangsun.bean

import me.liuwj.ktorm.entity.Entity

interface User :Entity<User>{
    //字段将会被编译查看
    companion object : Entity.Factory<User>()

    val id :Int
    var name:String
    var pwd:String
}