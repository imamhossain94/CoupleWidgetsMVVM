package com.newagedevs.couplewidgets.repository

import com.newagedevs.couplewidgets.persistence.CoupleDao
import timber.log.Timber


class MainRepository constructor(
    private val coupleDao: CoupleDao
) : Repository {

    init {
        Timber.d("Injection MainRepository")
    }


}