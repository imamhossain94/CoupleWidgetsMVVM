package com.newagedevs.couplewidgets.repository

import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.persistence.CoupleDao
import timber.log.Timber


class MainRepository constructor(
    private val coupleDao: CoupleDao
) : Repository {


    fun getCouple(): Couple? {
        val couple = coupleDao.getCouples()
        return if (couple.isEmpty()) {
            null
        } else {
            couple[0]
        }
    }

    fun setCouple(couple:Couple) {
        coupleDao.insertCouple(couple)
    }


    init {
        Timber.d("Injection MainRepository")
    }

}