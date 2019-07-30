package io.mangel.issuemanager.events

import io.mangel.issuemanager.store.User

/**
 * Events issued if agents outside of the respective repository change the entity
 *
 * So if the SyncRepository modifies some ConstructionSite and saves it to the database
 * it must issue a SavedConstructionSitesEvent so that the ConstructionSiteRepository can pick up the changes
 */

class SavedUserEvent(val user: User)
class SavedConstructionSitesEvent
class SavedMapsEvent
class SavedIssuesEvent
class SavedCraftsmenEvent