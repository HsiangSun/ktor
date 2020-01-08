package cn.hsiangsun.bean

import me.liuwj.ktorm.entity.Entity

interface User :Entity<User>{
    val id :Int
    var name:String
    var pwd:String
}