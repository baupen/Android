package io.mangel.issuemanager.repositories

import io.mangel.issuemanager.repositories.base.AuthenticatedRepository

class IssueRepository : AuthenticatedRepository() {
    var isAbnahmeModusActive = false
}