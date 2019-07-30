package io.mangel.issuemanager.models

class ConstructionSite(val id: String, val name: String, val address: Address, val imagePath: String?) {
    private val _maps = ArrayList<Map>()
    val maps: List<Map> = _maps
}