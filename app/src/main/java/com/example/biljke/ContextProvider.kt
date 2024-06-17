package com.example.biljke

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object ContextProvider {
    private var context: Context? = null

    fun initialize(context: Context) {
        this.context = context.applicationContext
    }

    fun getContext(): Context {
        return context ?: throw IllegalStateException("ContextProvider is not initialized")
    }
}