package com.bks.recipe.util

import androidx.lifecycle.LiveData
import com.bks.recipe.requests.response.ApiResponse
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.IllegalArgumentException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class LiveDataCallAdapterFactory : CallAdapter.Factory() {

    /**
     *  This method performs a number of checks and then returns the Response type for Retrofit requests
     *  (@bodyType is the ResponseType. It can be RecipeResponse or RecipeSearchResponse )
     *  CHECK #1) returnType returns LiveData
     *  CHECK #2) Type LiveData<T> is of ApiResponse.class
     *  CHECK #3) Make sure the APiResponse is parameterized. AKA : ApiResponse<T> exists.
     *
     */
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {

        // Check #1
        // Make sure the CallAdapter is returning a type of LiveData
        if (getRawType(returnType) != LiveData::class.java) {
            return null
        }

        // Check #2
        // Type that LiveData is wrapping around
        val observableType = getParameterUpperBound(0,  returnType as ParameterizedType)

        // Check if it's of type ApiResponse
        val rawObservableType = getRawType(observableType)
        if(rawObservableType != ApiResponse::class.java) {
            throw IllegalArgumentException("Type must be a defined resource")
        }

        // Check #3
        // Check if ApiResponse is parameterized. AKA : Does ApiResponse<T> exist ? (must wrap around T)
        // FYI : T is either RecipeResponse or RecipeSearchResponse
        if(observableType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterized")
        }

        val bodyType = getParameterUpperBound(0, observableType as ParameterizedType)
        return LiveDataCallAdapter<Type>(bodyType)
    }
}