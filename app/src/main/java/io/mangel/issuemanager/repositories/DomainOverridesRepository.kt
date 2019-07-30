package io.mangel.issuemanager.repositories

import io.mangel.issuemanager.api.DomainOverride
import io.mangel.issuemanager.api.tasks.DomainOverridesTask
import io.mangel.issuemanager.api.tasks.DomainOverridesTaskFinished
import io.mangel.issuemanager.factories.ClientFactory
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DomainOverridesRepository(private val clientFactory: ClientFactory) {
    companion object {
        const val DOMAIN_OVERRIDES_HOST = "https://app.mangel.io"
    }

    init {
        EventBus.getDefault().register(this)
    }

    private val _domainOverrides = ArrayList<DomainOverride>()
    var domainOverrides: List<DomainOverride> = _domainOverrides

    fun loadDomainOverrides() {
        val client = clientFactory.getClient(DOMAIN_OVERRIDES_HOST)
        val domainOverridesTask = DomainOverridesTask(client)
        domainOverridesTask.execute("once")
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDomainOverridesTaskFinished(event: DomainOverridesTaskFinished) {
        _domainOverrides.clear()
        _domainOverrides.addAll(event.domainOverrideRoot.domainOverrides)
    }
}