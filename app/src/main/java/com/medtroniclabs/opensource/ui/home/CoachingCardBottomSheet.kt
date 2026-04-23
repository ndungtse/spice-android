package com.medtroniclabs.opensource.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.medtroniclabs.microcoaching.ui.coaching.CoachingCardFragment

/**
 * Thin BottomSheetDialogFragment that hosts [CoachingCardFragment] from the SDK.
 * Use [show] to display the morning coaching card for a given scenario.
 */
class CoachingCardBottomSheet : BottomSheetDialogFragment() {

    private var containerId = View.NO_ID

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FrameLayout(requireContext()).also {
            it.id = View.generateViewId()
            containerId = it.id
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scenarioId = arguments?.getString(ARG_SCENARIO_ID) ?: return
        val autoSpeak = arguments?.getBoolean(ARG_AUTO_SPEAK, false) ?: false
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(containerId, CoachingCardFragment.newInstance(scenarioId, autoSpeak))
                .commit()
        }
    }

    companion object {
        private const val ARG_SCENARIO_ID = "scenario_id"
        private const val ARG_AUTO_SPEAK = "auto_speak"

        fun show(manager: FragmentManager, scenarioId: String, autoSpeak: Boolean = false) {
            CoachingCardBottomSheet().apply {
                arguments = bundleOf(ARG_SCENARIO_ID to scenarioId, ARG_AUTO_SPEAK to autoSpeak)
            }.show(manager, "coaching_card")
        }
    }
}
