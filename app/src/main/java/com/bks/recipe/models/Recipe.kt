package com.bks.recipe.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.util.*

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "recipe_id")
    val id : String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "publisher")
    val publisher: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    @ColumnInfo(name = "social_rank")
    val socialRank: Float,
    @ColumnInfo(name = "ingredients")
    val ingredients: ArrayList<String>,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readFloat(),
        parcel.createStringArrayList(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(publisher)
        parcel.writeString(imageUrl)
        parcel.writeFloat(socialRank)
        parcel.writeStringList(ingredients)
        parcel.writeLong(timestamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}