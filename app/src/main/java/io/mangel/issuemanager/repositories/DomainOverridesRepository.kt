package io.mangel.issuemanager.repositories

import io.mangel.issuemanager.api.Client
import io.mangel.issuemanager.api.DomainOverride
import io.mangel.issuemanager.api.tasks.DomainOverridesTask
import io.mangel.issuemanager.api.tasks.DomainOverridesTaskFinished
import io.mangel.issuemanager.services.RestHttpService
import io.mangel.issuemanager.services.SerializationService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DomainOverridesRepository(private val httpService: RestHttpService, private val serializationService: SerializationService) {
    companion object {
        const val DOMAIN_OVERRIDES_HOST = "https://app.mangel.io"
    }

    init {
        EventBus.getDefault().register(this)
    }

    private val _domainOverrides = ArrayList<DomainOverride>()
    var domainOverrides: List<DomainOverride> = _domainOverrides

    fun loadDomainOverrides() {
        val client = Client(DOMAIN_OVERRIDES_HOST, httpService, serializationService)
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