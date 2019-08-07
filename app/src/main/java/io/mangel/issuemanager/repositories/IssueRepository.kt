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
    private val mapIdToOpenIssueCount = HashMap<String, Int>()  // contains both, those in normal and in Abnahmemodus
    private val mapIdToInvestigationCount = HashMap<String, Int>()

    // TODO: snoop for updates

    fun getMapIdToOpenIssueCount() : HashMap<String, Int> {
      synchronized(this){
          if (!initialized) loadRepository()
          return mapIdToOpenIssueCount
      }
    }

    fun getMapIdToInvestigationCount() : HashMap<String, Int> {
        synchronized(this){
            if (!initialized) loadRepository()
            return mapIdToInvestigationCount
        }
    }

    private fun loadRepository(){
        simpleParse()
        setUpIssuesMapping(mapIdToOpenIssueCount) { issue -> issue.status.review == null}
        setUpIssuesMapping(mapIdToInvestigationCount) {
                issue -> issue.status.response != null && issue.status.review == null
        }
        initialized = true
    }

    private fun simpleParse(){
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
    private fun setUpIssuesMapping(destination: HashMap<String, Int>, issueFilter: (Issue) -> Boolean) {
        val tmp = HashMap<String, Int>()
        for (issue in issuesParsed){
            if (!issueFilter(issue)) continue
            val mapId = issue.map.id
            tmp[mapId] = tmp[mapId]?.plus(1) ?: 1
        }
        val mapRepo = applicationFactory.mapRepository
        val rootMapIds =  mapRepo.getChildren(null)!!.map { map -> map.id }
        rootMapIds.forEach { id -> treeAddUp(id, destination, tmp, mapRepo) }
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