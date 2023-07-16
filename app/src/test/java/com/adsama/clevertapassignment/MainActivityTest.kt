package com.adsama.clevertapassignment

import android.R
import android.os.Build
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode


@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class MainActivityTest {

    @Test
    @Config(sdk = [Build.VERSION_CODES.P])
    fun test_if_fragment_is_shown() {
        val activity: MainActivity =
            Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val fragmentManager: FragmentManager = activity.supportFragmentManager
        val fragment = HundFragment()
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.add(R.id.content, fragment, "hund_tag")
        transaction.commit()
        shadowOf(Looper.getMainLooper()).idle()
        val addedFragment: Fragment? = fragmentManager.findFragmentByTag("hund_tag")
        assertTrue(addedFragment?.isVisible ?: false)
    }
}