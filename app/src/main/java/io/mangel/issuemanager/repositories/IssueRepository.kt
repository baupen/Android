package io.mangel.issuemanager.repositories

import io.mangel.issuemanager.factories.ApplicationFactory
import io.mangel.issuemanager.models.Issue
import io.mangel.issuemanager.models.ModelConverter
import io.mangel.issuemanager.services.data.IssueDataService
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class IssueRepository(
    private val issueDataService: IssueDataService,
    private val modelConverter: ModelConverter,
    private val applicationFactory: ApplicationFactory
) {
    var isAbnahmeModusActive = false
    private var initialized = false
    private val issuesParsed = ArrayList<Issue>()
    private val mapToOpenIssues = HashMap<String, Int>()
    private val mapToOpenIssuesRec = HashMap<String, Int>()
    private val mapToToInvestigateIssues = HashMap<String, Int>()
    private val mapToToInvestigateIssuesRec = HashMap<String, Int>()

    // TODO: snoop for updates

    fun getOpenIssuesCount(mapId: String, recursive: Boolean = false) : Int {
        synchronized(this){
            if (!initialized) loadRepository()
            if (recursive) return mapToOpenIssuesRec[mapId] ?: 0
            return mapToOpenIssues[mapId] ?: 0
        }
    }

    fun getToInvestigateIssuesCount(mapId: String, recursive: Boolean = false) : Int {
        synchronized(this){
            if (!initialized) loadRepository()
            if (recursive) return mapToToInvestigateIssuesRec[mapId] ?: 0
            return mapToToInvestigateIssues[mapId] ?: 0
        }
    }

    private fun loadRepository() {
        basicParse()
        calculateMapToCount(mapToOpenIssues) { issue -> issue.status.review == null }
        recursiveMapToCount(mapToOpenIssuesRec, mapToOpenIssues)
        calculateMapToCount(mapToToInvestigateIssues) { i -> i.status.review == null && i.status.response != null }
        recursiveMapToCount(mapToToInvestigateIssuesRec, mapToToInvestigateIssues)
        initialized = true
    }

    private fun basicParse(){
        val entities = issueDataService.getAll()
        val mapRepo = applicationFactory.mapRepository
        val craftRepo = applicationFactory.craftsmanRepository
        for (entity in entities) {
            issuesParsed.add(modelConverter.convert(
                entity,
                mapRepo.getMap(entity.mapId)!!,
                craftRepo.getCraftsman(entity.craftsmanId!!)
            ))
        }
    }

    // issueFilter decides which issues should be counted
    private fun calculateMapToCount(destination: HashMap<String, Int>, issueFilter: (Issue) -> Boolean) {
        for (issue in issuesParsed){
            if (!issueFilter(issue)) continue
            val mapId = issue.map.id
            destination[mapId] = destination[mapId]?.plus(1) ?: 1
        }
    }

    private fun recursiveMapToCount(destination: HashMap<String, Int>, baseMapping: HashMap<String, Int>){
        val mapRepo = applicationFactory.mapRepository
        val rootMapIds =  mapRepo.getChildren(null)!!.map { map -> map.id }
        rootMapIds.forEach { id -> treeAddUp(id, destination, baseMapping, mapRepo) }
    }

    private fun treeAddUp(mapId: String, destination: HashMap<String, Int>, mapIdToOwnIssueCount: HashMap<String, Int>, mapRepo: MapRepository) : Int {
        val ownIssues = mapIdToOwnIssueCount[mapId] ?: 0
        val fromChildren =  mapRepo.getChildren(mapId)!!
            .map { child -> treeAddUp(child.id, destination, mapIdToOwnIssueCount, mapRepo) }.sum()
        val totalNbrOfIssues = ownIssues + fromChildren
        destination[mapId] = totalNbrOfIssues
        return totalNbrOfIssues
    }

}