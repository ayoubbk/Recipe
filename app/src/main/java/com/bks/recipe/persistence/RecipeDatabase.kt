package com.bks.recipe.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bks.recipe.models.Recipe

/**
 * The Room Database that contains Recipe table
 *
 * Note that exportSchema should be true in production databases.
 */

@Database(entities = [Recipe::class], version = 0, exportSchema = false)
abstract class RecipeDatabase : RoomDatabase() {

    abstract fun getRecipeDao() : RecipeDao

    companion object {

        private const val DATABASE_NAME = "recipe_db"

        //Fixing Double-Checked Locking using Volatile
        @Volatile
        private var instance : RecipeDatabase ? = null

        /**
         * double-checked locking system and the code is somehow similar
         * to the Lazy() function in kotlin and that's why it's called lazy initialization
         */
        fun getInstance(context : Context) : RecipeDatabase {
            return instance?: synchronized(this ) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }
        }

        private fun buildDatabase(context: Context): RecipeDatabase {
            return Room.databaseBuilder(context, RecipeDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    /**
     * this is equivalent to getInstance() method written in different way
     * @param context
     */
//    fun getInstance2(context: Context): RecipeDatabase? {
//        if (instance == null) {
//            synchronized(RecipeDatabase::class.java) {
                    // Double-checked Locking system and the code is somehow similar to the Lazy() function in kotlin and that's why it's called lazy initialization
//                if (instance == null)
//                    instance =
//                        Room.databaseBuilder(context, RecipeDatabase::class.java, DATABASE_NAME)
//                            .fallbackToDestructiveMigration()
//                            .build()
//            }
//        }
//        return instance
//    }

}