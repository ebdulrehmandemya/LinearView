package org.dahatu.libs.linearview

internal class LoadMore : Item {

    companion object {
        const val LOAD_MORE_TYPE_ID = Int.MIN_VALUE
        fun create() = LoadMore()
    }

    override fun type(): Int = LOAD_MORE_TYPE_ID

}