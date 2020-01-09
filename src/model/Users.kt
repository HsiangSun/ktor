package cn.hsiangsun.model

import cn.hsiangsun.bean.User
import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.int
import me.liuwj.ktorm.schema.varchar

object Users : Table<User>("USER") {
    val id by int("id").primaryKey().bindTo { it.id }
    val name by varchar("name").bindTo { it.name }
    val password by varchar("pwd").bindTo { it.pwd }
}