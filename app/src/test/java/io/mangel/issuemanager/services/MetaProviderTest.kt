package io.mangel.issuemanager.services

import com.google.common.truth.Truth.assertThat
import io.mangel.issuemanager.services.data.store.MetaProvider
import io.mangel.issuemanager.services.data.store.SqliteEntry
import org.junit.Test
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.full.primaryConstructor

class MetaProviderTest {
    private val metaProvider = MetaProvider()

    @Test
    fun supported_containsAllSubclasses() {
        val classes = SqliteEntry::class.sealedSubclasses
        assertThat(metaProvider.supported).hasLength(classes.size)

        val classSet = HashSet(classes)
        assertThat(classSet).hasSize(classes.size)
    }

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
        val allTableNames = metaProvider.supported.map { m -> metaProvider.getMeta(m).getTableName() }
        val set = HashSet(allTableNames)

        assertThat(allTableNames).hasSize(set.size)
    }

    private fun <T : Any> assertMetaMakesSense(subject: KClass<T>) {
        val constructor = subject.primaryConstructor!!

        val parameterList = ArrayList<Any>()
        val parameterListWithNullable = ArrayList<Any?>()
        for ((index, parameter) in constructor.parameters.withIndex()) {
            val value = getValueForType(parameter.type.classifier, index)
            parameterList.add(value)
            if (parameter.type.isMarkedNullable) {
                parameterListWithNullable.add(null)
            } else {
                parameterListWithNullable.add(value)
            }
        }
        val parameters = parameterList.toArray()
        val parametersWithNull = parameterListWithNullable.toArray()

        val element = constructor.call(*parameters)
        val elementWithNull = constructor.call(*parametersWithNull)
        val meta = metaProvider.getMeta(subject.java)

        // check columns make sense
        val propertyNames = constructor.parameters.map { p -> p.name }.toTypedArray()
        val columnsNames = meta.getColumns().map { c -> c.first }.toTypedArray()
        assertContentEqual(columnsNames, propertyNames)

        // check unserialize
        val sqliteParameters = mockSqliteStorageTransform(parameters)
        val parsedElement = meta.getRowParser().parseRow(sqliteParameters)
        assertContentEqual(parsedElement, element)

        // check serialize
        val elementArray = meta.toArray(element)
        assertContentEqual(elementArray, parameters)

        // check unserialize with nullable
        val sqliteparametersWithNull = mockSqliteStorageTransform(parametersWithNull)
        val parsedElementWithNull = meta.getRowParser().parseRow(sqliteparametersWithNull)
        assertContentEqual(parsedElementWithNull, elementWithNull)

        // check serialize with nullable
        val elementArrayWithNull = meta.toArray(elementWithNull)
        assertContentEqual(elementArrayWithNull, parametersWithNull)
    }

    private fun mockSqliteStorageTransform(list: Array<Any?>): Array<Any?> {
        return list.map { value ->
            if (value is Boolean) {
                if (value) {
                    "1"
                } else {
                    "0"
                }
            } else if (value == null) {
                null
            }
            else {
                value.toString()
            }
        }.toTypedArray()
    }

    private fun getValueForType(
        classifier: KClassifier?,
        index: Int
    ): Any {
        return when (classifier) {
            Int::class -> index
            String::class -> index.toString()
            Double::class -> index.toDouble()
            Boolean::class -> index % 2 == 0
            else -> throw IllegalArgumentException("unknown type: " + classifier.toString())
        }
    }

    private fun <T : Any> assertContentEqual(one: T, two: T) {
        val serializationService = SerializationService()
        val oneJson = serializationService.serialize(one)
        val twoJson = serializationService.serialize(two)

        assertThat(oneJson).isEqualTo(twoJson)
    }
}