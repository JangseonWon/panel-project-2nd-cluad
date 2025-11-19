package com.gcgenome.lims.inserts

abstract class Insert {
    fun chk(predicate : (Insert) -> Boolean) : Boolean = predicate(this)

    open fun identifier() : String = ""
    abstract fun weight() : Int
    abstract fun text(something : Any?) : String
    abstract fun services() : Set<String>

    abstract fun position() : Position

    enum class Position{
        NEGATIVE, PRESUFFIX, POSTSUFFIX
    }
}