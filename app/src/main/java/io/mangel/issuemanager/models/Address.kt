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

        var localityLine = ""
        if (postalCode != null) {
            localityLine += postalCode
        }
        if (locality != null) {
            if (localityLine != "") {
                localityLine += " "
            }
            localityLine += locality
        }

        if (localityLine != "") {
            lines.add(localityLine)
        }

        if (country != null) {
            lines.add(country)
        }

        return lines.joinToString(separator = "\n")
    }
}