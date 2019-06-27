package org.dahatu.libs.linearview

interface Item {
    fun type(): Int
    fun id(): Long = 0
}