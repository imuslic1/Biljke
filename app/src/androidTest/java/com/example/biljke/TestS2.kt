package com.example.biljke

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.biljke.NovaBiljkaActivity

@RunWith(AndroidJUnit4::class)
class TestS2 {

    @get:Rule
    var activityRule: ActivityScenarioRule<NovaBiljkaActivity> = ActivityScenarioRule(NovaBiljkaActivity::class.java)

    @Test
    fun testTakePictureAndDisplay() {
        // Initialize Intents and prepare stub
        Intents.init()

        // Create a bitmap we can use for our simulated camera image
        val icon: Bitmap = BitmapFactory.decodeResource(
            InstrumentationRegistry.getInstrumentation().targetContext.resources,
            R.drawable.test_img
        )

        // Build a result to return when a picture is taken
        val resultData = Intent()
        val bundle = Bundle()
        bundle.putParcelable("data", icon)
        resultData.putExtras(bundle)

        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)

        // Stub out the Intent. When an Intent with action MediaStore.ACTION_IMAGE_CAPTURE is seen,
        // return the ActivityResult we just created
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result)

        // Now that we have the stub in place, click on the button in our app
        onView(withId(R.id.uslikajBiljkuBtn)).perform(click())

        // We can also validate that an intent resolving to the "camera" activity has been sent out by our app
        intended(hasAction(MediaStore.ACTION_IMAGE_CAPTURE))

        // ... additional test steps and validation ...

        // Clean up
        Intents.release()
    }
}