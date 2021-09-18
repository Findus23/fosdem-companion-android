package org.matomocamp.companion.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts3
import androidx.room.PrimaryKey
import org.matomocamp.companion.api.MatomoCampUrls
import org.matomocamp.companion.utils.toSlug
import kotlinx.parcelize.Parcelize

@Fts3
@Entity(tableName = Person.TABLE_NAME)
@Parcelize
data class Person(
        @PrimaryKey
        @ColumnInfo(name = "rowid")
        val id: Long,
        val name: String?
) : Parcelable {

    fun getUrl(year: Int): String? {
        val n = name ?: return null
        return MatomoCampUrls.getPerson(n.toSlug(), year)
    }

    override fun toString(): String = name ?: ""

    companion object {
        const val TABLE_NAME = "persons"
    }
}