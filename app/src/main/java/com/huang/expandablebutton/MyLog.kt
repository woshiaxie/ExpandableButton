package com.huang.expandablebutton

import android.util.Log

open class MyLog{
    companion object {
        private const val TAG = "MyLog"
        fun logd(logInfo: String){
            Log.d(TAG,logInfo)
        }
        fun loge(logInfo: String){
            Log.e(TAG,logInfo)
        }
        fun loge(logInfo: String, throwable: Throwable){
            Log.e(TAG,logInfo,throwable)
        }
    }
}