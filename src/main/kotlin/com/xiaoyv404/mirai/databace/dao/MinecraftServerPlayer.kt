package com.xiaoyv404.mirai.databace.dao

import com.xiaoyv404.mirai.databace.Database.db
import org.ktorm.database.*
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.schema.*
import java.time.*
import java.util.*


interface MinecraftServerPlayer : Entity<MinecraftServerPlayer> {

    companion object : Entity.Factory<MinecraftServerPlayer>()
    var id : UUID
    var name : String
    var lastLoginTime : LocalDateTime
}

private val Database.minecraftServerPlayer get() = this.sequenceOf(MinecraftServerPlayers)

fun MinecraftServerPlayer.findById(): MinecraftServerPlayer?{
    return db.minecraftServerPlayer.find { it.id eq this.id }
}


fun MinecraftServerPlayer.save(): Boolean{
    return if (this.findById() == null){
        db.minecraftServerPlayer.add(this)
        false
    }else{
        this.update()
        true
    }
}

fun MinecraftServerPlayer.update(){
    db.minecraftServerPlayer.update(this)
}

object MinecraftServerPlayers : Table<MinecraftServerPlayer>("MinecraftServerPlayers") {
    val id = uuid("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val lastLoginTime = datetime("lastLoginTime").bindTo { it.lastLoginTime }
}