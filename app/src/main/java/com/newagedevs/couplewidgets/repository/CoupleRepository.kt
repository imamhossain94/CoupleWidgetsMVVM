package com.newagedevs.couplewidgets.repository

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.model.Decorator
import com.newagedevs.couplewidgets.model.Person
import com.newagedevs.couplewidgets.persistence.AppDatabase
import com.newagedevs.couplewidgets.persistence.CoupleDao
import com.newagedevs.couplewidgets.utils.VectorDrawableMasker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.SimpleDateFormat
import java.util.*

//class CoupleRepository : KoinComponent {
//
//    private val database: AppDatabase by inject()
//
//    fun getCoupleWithCountersFlow(): Flow<Couple> {
//        return database.coupleDao().getAllCouples().map {
//            it[0]
//        }
//    }
//
//}

class CoupleRepository(private val coupleDao: CoupleDao) {

    fun getCoupleWithFlow(): Flow<List<Couple>> {

        return coupleDao.getAllCouples().map {
            it
        }.flowOn(Dispatchers.IO)
    }

}


