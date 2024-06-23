package com.example.biljke

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description

object BitmapMatcher {
    fun withBitmap(expectedBitmap: Bitmap) = object : BoundedMatcher<View, ImageView>(ImageView::class.java) {
        override fun describeTo(description: Description?) {
            description?.appendText("with bitmap: $expectedBitmap")
        }

        override fun matchesSafely(imageView: ImageView?): Boolean {
            val bitmap = (imageView?.drawable as BitmapDrawable).bitmap
            return bitmap.sameAs(expectedBitmap)
        }
    }
}