package io.mangel.issuemanager.models

import org.threeten.bp.LocalDateTime
import java.util.*

class Issue(val id: String, val map: Map, val number: Int?, val wasAddedWithClient: Boolean) {
    var isMarked: Boolean = false
    var imagePath: String? = null
    var description: String? = null
    var craftsman: Craftsman? = null
    var status: Status = Status()
    var position: Position? = null
}

class Status(
    val registration: Event? = null,
    val response: Event? = null,
    val review: Event? = null
)

class Event(val time: LocalDateTime, val author: String)

class Position(val point: Point?, val zoomScale: Double?, val mapFileID: String?)
class Point(val x: Double, val y: Double)