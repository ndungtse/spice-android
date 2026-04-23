package com.medtroniclabs.opensource.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.medtroniclabs.microcoaching.ai.voice.BanglaTtsHelper
import com.medtroniclabs.opensource.databinding.FragmentMorningCoachingCardBinding
class DemoMorningCoachingCardFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentMorningCoachingCardBinding? = null
    private val binding get() = _binding!!

    private val tts by lazy { BanglaTtsHelper(requireContext()) }

    private val cardTitle = "উচ্চ রক্তচাপ পরামর্শ"
    private val cardBody = "প্রতিদিন ওষুধ খান। লবণ কম খান। নিয়মিত ব্যায়াম করুন।"
    private val cardWarning = "⚠ মাথাব্যথা বা বুক ব্যথা হলে এখনই হাসপাতালে যান।"
    private val cardNextStep = "পরবর্তী: ১ সপ্তাহ পরে পুনরায় রক্তচাপ পরিমাপ করুন।"

    private val fullCardText get() = "$cardTitle\n$cardBody\n$cardWarning\n$cardNextStep"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMorningCoachingCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = cardTitle
        binding.tvBody.text = cardBody
        binding.tvWarning.text = cardWarning
        binding.tvNextStep.text = cardNextStep
        binding.btnSpeak.setOnClickListener { tts.speak(fullCardText) }
        binding.btnClose.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts.release()
        _binding = null
    }

    companion object {
        fun show(manager: FragmentManager) =
            DemoMorningCoachingCardFragment().show(manager, "demo_morning_card")
    }
}
