package com.bks.recipe.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bks.recipe.persistence.Converter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.NotNull

@Entity(tableName = "recipes")
@TypeConverters(Converter::class)
data class Recipe(
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "recipe_id")
    val recipe_id : String = "",
    @ColumnInfo(name = "title")
    val title: String = "",
    @ColumnInfo(name = "publisher")
    val publisher: String = "",
    @ColumnInfo(name = "image_url")
    @SerializedName(value = "image_url")
    val imageUrl: String = "",
    @ColumnInfo(name = "social_rank")
    @SerializedName(value = "social_rank")
    val socialRank: Float = 0f,
    @ColumnInfo(name = "ingredients")
    val ingredients: List<String> = emptyList<String>(),
    @ColumnInfo(name = "timestamp")
    var timestamp: Long = 0
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
        parcel.writeString(recipe_id)
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