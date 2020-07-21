package com.bks.recipe.models

import android.content.Context
import android.util.Log

/**
 * The BEST WAY to write a singleton with Kotlin and Java is by making an enum
 * with instance functions in it
 *
 * The Right Way to Implement a Serializable Singleton
 * https://stackoverflow.com/questions/70689/what-is-an-efficient-way-to-implement-a-singleton-pattern-in-java/71399#71399
 *
 * Joshua Bloch explained this approach in his Effective Java Reloaded talk at Google I/O 2008: link to video. Also see slides 30-32 of his presentation (effective_java_reloaded.pdf):
 *
 *
 * https://discuss.kotlinlang.org/t/var-str-string-is-mutable-or-immutable/3363
 * val is readOnly and does not mean immutable
 *
 * Consider also the simple way to create singleton with Kotlin and Java by making an enum with instance functions in it, as explained here
 */

enum class Singleton {
    INSTANCE;

    fun doSomething(context : Context) {
        Log.d("abk", "doSomething: test singleton")
    }
}