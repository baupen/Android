package io.mangel.issuemanager.repositories

import io.mangel.issuemanager.models.Craftsman
import io.mangel.issuemanager.models.ModelConverter
import io.mangel.issuemanager.services.data.CraftsmanDataService

class CraftsmanRepository(
    private val craftsmanDataService: CraftsmanDataService,
    private val modelConverter: ModelConverter
) {

    private var initialized = false
    private var craftsmen = HashMap<String, Craftsman>()

    // TODO: snoop for changes

    fun getCraftsman(id: String) : Craftsman? {
        synchronized(this){
            if (!initialized) loadCraftsmen()
            return if (craftsmen.contains(id)) craftsmen[id] else null
        }
    }

    private fun loadCraftsmen() {
        val entities = craftsmanDataService.getAll()

        for (entity in entities){
            val model = modelConverter.convert(entity)
            craftsmen[model.id] = model
        }
        initialized = true
    }

}