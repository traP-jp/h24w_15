package jp.trap.conqest.models

import org.jetbrains.exposed.sql.Table

object Counter: Table() {
    val count=integer("count")
}