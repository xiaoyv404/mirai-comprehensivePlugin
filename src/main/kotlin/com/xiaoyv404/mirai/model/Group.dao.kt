package com.xiaoyv404.mirai.model

import com.xiaoyv404.mirai.extension.json
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.enum
import org.ktorm.schema.long
import org.ktorm.schema.typeRef

interface Group : Entity<Group> {
    companion object : Entity.Factory<Group>()

    var id: Long
    val notice: Notice
    val permission: Permissions
    val salutatory: Salutatory
    val type: GroupType
}

object Groups : Table<Group>("Groups") {
    val id = long("id").primaryKey().bindTo { it.id }
    val notice = json<Notice>("notice", typeRef()).bindTo { it.notice }
    val permission = json<Permissions>("permission", typeRef()).bindTo { it.permission }
    val salutatory = json<Salutatory>("salutatory", typeRef()).bindTo { it.salutatory }
    val type = enum<GroupType>("type").bindTo { it.type }
}

enum class GroupType {
    Default,
    MCG,
}

@Suppress("PropertyName")
data class Notice(
    val JoinGreeting: Boolean,
    val AdminBroadcast: Boolean,
    val MinecraftServerMonitor: Boolean
)

@Suppress("PropertyName")
data class Permissions(
    val DebuMe: Permission,
    val NetworkEro: Permission,
    val LocalGallery: Permission,
    val ThesaurusAdd: Permission,
    val SauceNaoSearch: Permission,
    val BiliBiliParsing: Permission,
    val ThesaurusResponse: Permission,
    val MinecraftServerPlayerPermission: Permission
)

data class Permission(
    val all: Boolean,
    val black: List<Long>,
    val white: List<Long>,
    val controller: Controller
)

data class Controller(
    val white: List<Long>,
    val groupAdmin: Boolean
)

@Suppress("PropertyName")
data class Salutatory(
    val JoinGreeting: List<String>
)