package com.example.biljke

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.test.espresso.intent.Checks
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher


object ErrorMatcher {
    fun withErrorText(expectedErrorText: String) = object : BoundedMatcher<View, Button>(Button::class.java) {
        override fun describeTo(description: Description?) {
            description?.appendText("with error text: $expectedErrorText")
        }

        override fun matchesSafely(button: Button?): Boolean {
            return expectedErrorText == button?.error.toString()
        }
    }
}