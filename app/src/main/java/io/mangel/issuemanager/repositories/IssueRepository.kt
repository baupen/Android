package io.mangel.issuemanager.repositories

import io.mangel.issuemanager.repositories.base.AuthenticatedRepository

class IssueRepository : AuthenticatedRepository() {
    private var _value = false

    fun setAbnahmemodus(value: Boolean) {
        _value = value
    }
}