package org.dahatu.apps.linearviewtest.data

import android.view.View
import com.mooveit.library.Fakeit
import kotlinx.android.synthetic.main.layout_music.view.*
import org.dahatu.libs.linearview.Item

class Music(val id: Long) : Item {
    companion object {

        const val TYPE = 1

        fun create(id: Long) = Music(id).apply {
            chord = Fakeit.music().chord()
            chordType = Fakeit.music().chordTypes()
            instrument = Fakeit.music().instrument()
            key = Fakeit.music().key()
            keyType = Fakeit.music().keyTypes()
        }

        fun bind(music: Music, view: View, onUpdate: (music: Music) -> Unit) {
            view.chold.text = music.chord
            view.chord_type.text = music.chordType
            view.instrument.text = music.instrument
            view.key.text = music.key
            view.key_type.text = music.keyType
            view.setOnClickListener { onUpdate(music) }
        }
    }

    private lateinit var chord: String
    private lateinit var chordType: String
    private lateinit var instrument: String
    private lateinit var key: String
    private lateinit var keyType: String

    override fun type(): Int = TYPE
    override fun id(): Long = id

    fun update() {
        chord = Fakeit.music().chord()
        chordType = Fakeit.music().chordTypes()
        instrument = Fakeit.music().instrument()
        key = Fakeit.music().key()
        keyType = Fakeit.music().keyTypes()
    }

}