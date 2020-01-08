package cn.hsiangsun.model

import cn.hsiangsun.bean.User
import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.int
import me.liuwj.ktorm.schema.varchar

object Users : Table<User>("USER") {
    val id by int("id").primaryKey()
    val name by varchar("name")
    val password by varchar("pwd")
}