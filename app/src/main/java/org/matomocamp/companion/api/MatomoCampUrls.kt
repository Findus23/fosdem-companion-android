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
    val localNavigation
        get() = "https://nav.fosdem.org/"
    val volunteer
        get() = "https://fosdem.org/volunteer/"

    fun getEvent(slug: String, year: Int): String {
        return "https://fosdem.org/$year/schedule/event/$slug/"
    }

    fun getPerson(slug: String, year: Int): String {
        return "https://fosdem.org/$year/schedule/speaker/$slug/"
    }

    fun getLocalNavigationToLocation(locationSlug: String): String {
        return "https://nav.fosdem.org/d/$locationSlug/"
    }
}