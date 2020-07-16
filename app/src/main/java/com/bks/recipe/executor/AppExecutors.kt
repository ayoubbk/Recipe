package com.bks.recipe.executor

import android.os.Handler
import android.os.Looper
import com.bks.recipe.persistence.RecipeDatabase
import java.lang.RuntimeException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors private constructor() : java.io.Serializable {

    init {
        //prevent from reflection API
        if(soleInstance != null) {
            throw RuntimeException("Use getInstance() method to get the single instance of this class.")
        }
    }

    private val diskIO = Executors.newSingleThreadExecutor()
    private val networkIO = Executors.newFixedThreadPool(3)
    private val mainThread = MainThreadExecutor()

    // Make singleton from Serialize and deserialize operations.
    // Overcome serialization issue
    private fun readResolve() : AppExecutors ? {
        return getInstance()
    }

    fun diskIO(): Executor {
        return diskIO
    }

    fun networkIO() : Executor {
        return networkIO
    }

    fun mainThread() : Executor {
        return mainThread
    }

    companion object {

        //the volatile keyword to make sure that the instance will be shared across different thread
        @Volatile
        private var soleInstance : AppExecutors ? = null

        //if there is no instance available...create new one
        fun getInstance() : AppExecutors {
            synchronized(AppExecutors::class.java) {
                return soleInstance?: soleInstance?: AppExecutors()
            } 
        }

        // or you can replace getInstance method by the following statements (getter of instance)
//        val instance : MyAppExecutor?
//            get() {
//                if(soleInstance == null ) {
//                    synchronized(MyAppExecutor::class.java) {
//                        if(soleInstance == null) soleInstance = MyAppExecutor()
//                    }
//                }
//                return soleInstance
//            }
    }


        private class MainThreadExecutor : Executor {
            private val mainThreadHandler = Handler(Looper.getMainLooper())
            override fun execute(command: Runnable) {
                mainThreadHandler.post(command)
            }
        }

}