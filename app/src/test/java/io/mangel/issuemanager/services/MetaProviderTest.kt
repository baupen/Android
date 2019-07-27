package io.mangel.issuemanager.services

import com.google.common.truth.Truth.assertThat
import io.mangel.issuemanager.store.MetaProvider
import io.mangel.issuemanager.store.SqliteEntry
import org.junit.Ignore
import org.junit.Test
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class MetaProviderTest {
    private val metaProvider = MetaProvider()

    @Ignore("will be implemented in the next commit")
    @Test
    fun subclassesOfSqliteEntry_eachProvidesMeta() {
        val classes = SqliteEntry::class.sealedSubclasses

        for (entry in classes) {
            assertThat(metaProvider.getMeta(entry.java)).isNotNull()
            assertMetaMakesSense(entry)
        }
    }

    @Test
    fun tableNames_areUnique() {
        val allTableNames = metaProvider.metas.map { m -> m.key }
        val set = allTableNames.toSet()

        assertThat(allTableNames).hasSize(set.size)
    }

    private fun <T : Any> assertMetaMakesSense(subject: KClass<T>) {
        val constructor = subject.primaryConstructor!!

        val parameterList = ArrayList<String>()
        for (index in 0 until constructor.parameters.size) {
            parameterList.add(index.toString())
        }
        val parameters = parameterList.toArray()

        val element = constructor.call(*parameters)
        val meta = metaProvider.getMeta(subject.java)

        // check columns make sense
        val propertyNames = constructor.parameters.map { p -> p.name }.toTypedArray()
        val columnsNames = meta.getColumns().map { c -> c.first }.toTypedArray()
        assertContentEqual(columnsNames, propertyNames)

        // check unserialize
        val parsedElement = meta.getRowParser().parseRow(parameters)
        assertContentEqual(parsedElement, element)

        // check serialize
        val elementArray = meta.toArray(element)
        assertContentEqual(elementArray, parameters)
    }

    private fun <T : Any> assertContentEqual(one: T, two: T) {
        val serializationService = SerializationService()
        val oneJson = serializationService.serialize(one)
        val twoJson = serializationService.serialize(two)

        assertThat(oneJson).isEqualTo(twoJson)
    }
}