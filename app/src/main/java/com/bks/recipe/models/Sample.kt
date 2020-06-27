package com.bks.recipe.models

/**
 * Try to experiment : overcome serialization issue by readResolve
 */

object Sample : java.io.Serializable {

    fun readResolve(): Any? = Sample

    // constructor are not allowed fo object
    // to initialize something we can use init {} bloc in singleton class
    // but what if you need to pass some argument for initialization just like parametrized constructor ?
    init {

    }


}