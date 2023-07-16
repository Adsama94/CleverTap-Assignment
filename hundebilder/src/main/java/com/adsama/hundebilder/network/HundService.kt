package com.adsama.hundebilder.network

import com.adsama.hundebilder.model.HundResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface HundService {

    @GET("image/random")
    fun getImageAsync(
    ): Deferred<Response<HundResponse>>

    @GET("image/random/{count}")
    fun getImagesAsync(@Path("count") count: Int): Deferred<Response<HundResponse>>

}