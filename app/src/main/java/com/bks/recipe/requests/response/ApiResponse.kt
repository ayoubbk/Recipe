package com.bks.recipe.requests.response

import com.bks.recipe.util.Constant.Companion.QUERY_EXHAUSTED
import retrofit2.Response

/**
 * Generic class for handling responses from Retrofit; Common class used by API responses.
 * @param <T> the type of the response object
</T>*/
@Suppress("unused") // T is used in extending classes
sealed class ApiResponse<T> {

    companion object {
        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(error.message ?: "unknown error\n check network connection")
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body is RecipeSearchResponse) {
                    if (!CheckRecipeApiKey.isRecipeApiKeyValid(body as RecipeSearchResponse)) {
                        val errorMsg = "Api key is invalid or expired."
                        return ApiErrorResponse<T>(errorMsg)
                    }
                    if ((body as RecipeSearchResponse).count == 0) {
                        // query is exhausted
                        return ApiErrorResponse<T>(QUERY_EXHAUSTED)
                    }
                }
                if (body is RecipeResponse) {
                    if (!CheckRecipeApiKey.isRecipeApiKeyValid(body as RecipeResponse)) {
                        val errorMsg = "Api key is invalid or expired."
                        return ApiErrorResponse<T>(errorMsg)
                    }
                }
                if (body == null || response.code() == 204) { // 204 is empty response
                    ApiEmptyResponse()
                }
                else {
                    ApiSuccessResponse(body)
                }

            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                ApiErrorResponse(errorMsg ?: "unknown error")
            }
        }
    }

}

/**
 * Generic success response from api
 * @param <T>
</T> */
data class ApiSuccessResponse<T>(val body :  T) : ApiResponse<T>()

/**
 * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T> : ApiResponse<T>()

data class ApiErrorResponse<T>(val errorMessage: String) : ApiResponse<T>()