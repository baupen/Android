package io.mangel.issuemanager.repositories

import io.mangel.issuemanager.events.LoadedMapsEvent
import io.mangel.issuemanager.events.SavedMapsEvent
import io.mangel.issuemanager.models.Map
import io.mangel.issuemanager.models.ModelConverter
import io.mangel.issuemanager.services.data.MapDataService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MapRepository(
    private val mapDataService: MapDataService,
    private val modelConverter: ModelConverter
) {
    init {
        EventBus.getDefault().register(this)
    }

    private val _maps = ArrayList<Map>()
    private val _parentChildMapping = HashMap<String?, ArrayList<Map>>()
    private var initialized = false

    fun getMap(id: String): Map? {
        synchronized(this){
            if (!initialized)loadMaps()
            return _maps.find { m -> m.id == id }
        }
    }

    fun getChildren(id: String?): List<Map>? {
        synchronized(this){
            if (!initialized) loadMaps()
            return if (_parentChildMapping.containsKey(id)) _parentChildMapping[id]!!.toList() else null
        }
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun on(event: SavedMapsEvent) {
        loadMaps()
        EventBus.getDefault().post(LoadedMapsEvent())
    }

    private fun loadMaps() {
        _maps.clear()
        val entityMaps = mapDataService.getAll()

        // the tree is traversed level by level from the roots down
        var lastRoundIds = listOf<String?>(null)
        while (_maps.size != entityMaps.size) {
            val childrenOfLastRoundMaps = entityMaps.filter { m -> lastRoundIds.contains(m.parentId) }
            for (child in childrenOfLastRoundMaps) {
                val parentMap = _maps.find { potentialParent -> potentialParent.id == child.parentId }
                val model = modelConverter.convert(child, parentMap)
                placeDownMap(model)
            }
            lastRoundIds = childrenOfLastRoundMaps.map { m -> m.id }
        }
        // the maps without any children aren't inserted into the map yet
        val idsOfMapsWithoutChildren = entityMaps.map { m -> m.id }.toSet().minus(_parentChildMapping.keys)
        idsOfMapsWithoutChildren.forEach { id -> _parentChildMapping[id] = ArrayList<Map>()}

        initialized = true
    }

    private fun placeDownMap(map: Map){
        _maps.add(map)
        val key = map.parent?.id
        if (!_parentChildMapping.contains(key)) {
            _parentChildMapping[key] = arrayListOf(map)
        } else {
            _parentChildMapping[key]?.add(map)
        }
    }
}