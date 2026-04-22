package com.medtroniclabs.opensource.ui.coaching

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.medtroniclabs.microcoaching.ui.chat.CoachingChatFragment
import com.medtroniclabs.opensource.R

class CoachingAssistantActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coaching_assistant)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.coaching_assistant_container,
                    CoachingChatFragment.newInstance(systemContext = "chw_assistant"),
                )
                .commit()
        }
    }

    companion object {
        fun launch(context: Context) =
            context.startActivity(Intent(context, CoachingAssistantActivity::class.java))
    }
}
