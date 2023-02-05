package com.newagedevs.couplewidgets.repository

import com.newagedevs.couplewidgets.persistence.HeartSymbolDao
import com.newagedevs.couplewidgets.persistence.ImageShapeDao
import timber.log.Timber


class MainRepository constructor(
    private val imageShapeDao: ImageShapeDao,
    private val heartSymbolDao: HeartSymbolDao
) : Repository {

    init {
        Timber.d("Injection MainRepository")
    }


}