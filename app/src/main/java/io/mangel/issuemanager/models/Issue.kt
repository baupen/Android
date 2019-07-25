package io.mangel.issuemanager.models

import java.util.*

class Issue(val wasAddedWithClient: Boolean) {
    val number: Int? = null
    val isMarked: Boolean = false
    val imagePath: String? = null
    val description: String? = null
    val craftsman: Craftsman? = null
    val status: Status = Status()
    val position: Position? = null
}

class Status {
    val registration: Event? = null
    val response: Event? = null
    val review: Event? = null
}
class Event(val time: Date, val author: String)

class Position(val point: Point, val zoomScale: Double, val mapFileID: UUID)
class Point(val x: Double, val y: Double)