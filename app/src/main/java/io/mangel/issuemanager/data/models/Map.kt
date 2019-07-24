package io.mangel.issuemanager.data.models

class Map (val parent: Map?, val name: String, val filePath: String?) {
    private val _issues = ArrayList<Issue>()
    val issues: List<Issue> = _issues
}