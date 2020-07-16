package com.bks.recipe.requests

import com.bks.recipe.util.Constant
import com.bks.recipe.util.Constant.Companion.READ_TIMEOUT
import com.bks.recipe.util.Constant.Companion.WRITE_TIMEOUT
import com.bks.recipe.util.LiveDataCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceGenerator {

    // Create logger
    var logger = HttpLoggingInterceptor().also {
        it.level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        // establish connection to server
        .connectTimeout(Constant.CONNECTION_TIMEOUT, TimeUnit.SECONDS)

        // time between each byte read from the server
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)

        // time between each byte sent to server
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

        .retryOnConnectionFailure(false)

        .addInterceptor(logger)

        .build()

    private val retrofitBuilder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(Constant.BASE_URL)
        .client(client)
        .addCallAdapterFactory(LiveDataCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())

    private val retrofit = retrofitBuilder.build()

    val recipeApi: RecipeApi = retrofit.create(RecipeApi::class.java)
}
