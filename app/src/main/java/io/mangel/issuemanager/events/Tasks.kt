package io.mangel.issuemanager.events

import java.util.*

open class TaskStartedEvent(val taskId: UUID)
class ProgressTaskStartedEvent(taskId: UUID): TaskStartedEvent(taskId)

open class ProgressTaskProgressEvent(taskId: UUID, val progress: Int, val max: Int)

open class TaskFinishedEvent(val taskId: UUID)
