package io.mangel.issuemanager.models

class Address(
    private val streetAddress: String?,
    private val postalCode: String?,
    private val locality: String?,
    private val country: String?
) {
    override fun toString(): String {
        val lines = ArrayList<String>()
        if (streetAddress != null) {
            lines.add(streetAddress)
        }

        val localityLineEntries = ArrayList<String>()
        if (postalCode != null) {
            localityLineEntries.add(postalCode)
        }
        if (locality != null) {
            localityLineEntries.add(locality)
        }
        if (country != null) {
            localityLineEntries.add(country)
        }
        if (localityLineEntries.isNotEmpty()) {
            lines.add(localityLineEntries.joinToString(separator = " "))
        }

        return lines.joinToString(separator = "\n")
    }
}