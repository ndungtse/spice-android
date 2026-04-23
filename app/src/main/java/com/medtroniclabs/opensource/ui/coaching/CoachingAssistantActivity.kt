package com.medtroniclabs.opensource.ui.coaching

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.medtroniclabs.microcoaching.ui.chat.CoachingChatFragment
import com.medtroniclabs.opensource.R
import com.medtroniclabs.opensource.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoachingAssistantActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentView = layoutInflater.inflate(R.layout.activity_coaching_assistant, null)
        setMainContentView(
            view = contentView,
            isToolbarVisible = true,
            title = getString(R.string.chw_assistant),
            homeAndBackVisibility = Pair(false, true),
        )
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
