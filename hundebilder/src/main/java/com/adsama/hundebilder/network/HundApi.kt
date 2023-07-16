package com.adsama.hundebilder.network

object HundApi {

    val hundService: HundService by lazy {
        retrofitClient().create(HundService::class.java)
    }

}