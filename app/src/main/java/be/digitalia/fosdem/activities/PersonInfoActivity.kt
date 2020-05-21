package be.digitalia.fosdem.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import be.digitalia.fosdem.R
import be.digitalia.fosdem.fragments.PersonInfoListFragment
import be.digitalia.fosdem.model.Person

class PersonInfoActivity : AppCompatActivity(R.layout.content_extended_title) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))

        val person: Person = intent.getParcelableExtra(EXTRA_PERSON)!!

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = person.name

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add<PersonInfoListFragment>(R.id.content,
                        args = PersonInfoListFragment.createArguments(person))
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val EXTRA_PERSON = "person"
    }
}