package org.matomocamp.companion.api

/**
 * This class contains all FOSDEM Urls
 *
 * @author Christophe Beyls
 */
object MatomoCampUrls {

    val schedule
          get() = "https://schedule.matomocamp.org/matomocamp-2021/schedule/export/schedule.xml"
    val website
        get() = "https://matomocamp.org/"


    fun getPerson(id: Number): String {
        return "https://schedule.matomocamp.org/matomocamp-2021/speaker/by-id/$id/"
    }

    fun getLocalNavigationToLocation(): String {
        return "https://matomocamp.org/"
    }
}