package org.matomocamp.companion.api

/**
 * This class contains all FOSDEM Urls
 *
 * @author Christophe Beyls
 */
object MatomoCampUrls {

    val schedule
        get() = "https://schedule.matomocamp.org/matomocamp-2022/schedule/export/schedule.xml"
    val rooms
        get() = "https://api.fosdem.org/roomstatus/v1/listrooms"
    val website
        get() = "https://matomocamp.org/"

    fun getPerson(id: Number): String {
        return "https://schedule.matomocamp.org/matomocamp-2021/speaker/by-id/$id/"
    }

    fun getLocalNavigationToLocation(locationSlug: String): String {
        return "https://nav.fosdem.org/d/$locationSlug/"
    }
}