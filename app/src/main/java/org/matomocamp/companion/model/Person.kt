package org.matomocamp.companion.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import org.matomocamp.companion.api.MatomoCampUrls
import org.matomocamp.companion.utils.toSlug
import kotlinx.parcelize.Parcelize

@Fts4
@Entity(tableName = Person.TABLE_NAME)
@Parcelize
data class Person(
        @PrimaryKey
        @ColumnInfo(name = "rowid")
        val id: Long,
        val name: String?
) : Parcelable {

    fun getUrl(year: Int): String {
        return MatomoCampUrls.getPerson(id)    }

    override fun toString(): String = name.orEmpty()

    companion object {
        const val TABLE_NAME = "persons"
    }
}