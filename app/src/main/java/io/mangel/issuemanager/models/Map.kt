package io.mangel.issuemanager.models

class Map (val id: String, val parent: Map?, val name: String, val filePath: String?) {
    private val _children = ArrayList<Map>()
    val children: List<Map> = _children

    private val _issues = ArrayList<Issue>()
    val issues: List<Issue> = _issues
}