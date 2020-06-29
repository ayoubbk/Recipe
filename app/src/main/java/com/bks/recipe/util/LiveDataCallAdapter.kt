package com.bks.recipe.util

import androidx.lifecycle.LiveData
import com.bks.recipe.requests.response.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

/**
 * Converting Call to LiveData
 * use of Generic Type 'R' here which going to define the response from Retrofit, because
 * we have multi different type of response : RecipeResponse, RecipeSearchResponse, etc... and we could
 * have have even more responses
 * we're wrapping the ApiResponse around our Retrofit response object which is R and we're wrapping that with LiveData
 */

class LiveDataCallAdapter<R>  constructor(private val responseType : Type) : CallAdapter<R, LiveData<ApiResponse<R>>> {


    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): LiveData<ApiResponse<R>> {

        return object : LiveData<ApiResponse<R>>() {
            override fun onActive() {
                super.onActive()
                val apiResponse = ApiResponse
                call.enqueue(object : Callback<R> {
                    override fun onResponse(call: Call<R>, response: Response<R>) {
                        postValue(apiResponse.create(response))
                    }

                    override fun onFailure(call: Call<R>, t: Throwable) {
                        postValue(apiResponse.create(t))
                    }
                })

            }
        }

    }


}