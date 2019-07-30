package io.mangel.issuemanager.events

/**
 * Events issued if the repository content has changed
 *
 * Example: The ConstructionSite repository loaded all construction sites from the database again
 * it must issue a LoadedConstructionSitesEvent so that its consumers can fetch the list of construction sites again
 */
class LoadedUserEvent
class LoadedConstructionSitesEvent
class LoadedMapsEvent
class LoadedIssuesEvent
class LoadedCraftsmenEvent