package com.medtroniclabs.opensource.ui.home

import android.content.Intent
import android.net.ConnectivityManager as AndroidConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.medtroniclabs.microcoaching.MicroCoachingSDK
import com.medtroniclabs.opensource.R
import com.medtroniclabs.opensource.custom.SecuredPreference
import com.medtroniclabs.opensource.databinding.FragmentHomeScreenBinding
import com.medtroniclabs.opensource.db.tables.MenuEntity
import com.medtroniclabs.opensource.formgeneration.definedproperties.DefinedParams
import com.medtroniclabs.opensource.formgeneration.formsupport.CommonUtils
import com.medtroniclabs.opensource.network.resource.ResourceState
import com.medtroniclabs.opensource.network.utils.ConnectivityManager
import com.medtroniclabs.opensource.ui.AdvancedSearchActivity
import com.medtroniclabs.opensource.ui.BaseActivity
import com.medtroniclabs.opensource.ui.BaseFragment
import com.medtroniclabs.opensource.ui.UIConstants
import com.medtroniclabs.opensource.ui.home.adapter.ActivitiesAdapter
import com.medtroniclabs.opensource.ui.landing.LandingViewModel
import com.medtroniclabs.opensource.ui.screening.GeneralDetailsActivity
import com.medtroniclabs.opensource.ui.coaching.CoachingAssistantActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeScreenFragment : BaseFragment(), MenuSelectionListener {

    lateinit var binding: FragmentHomeScreenBinding

    private val viewModel: LandingViewModel by activityViewModels()

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachObserver()
        setupCoachingCard()
    }

    private fun setupCoachingCard() {
        val sdk = MicroCoachingSDK.getInstance()
        val chwId = runCatching { SecuredPreference.getUserId().toString() }.getOrDefault("")
        sdk.onHomeScreenShown(chwId)

        binding.btnTodaysCoaching.visibility = View.VISIBLE
        binding.btnTodaysCoaching.setOnClickListener {
            val cards = sdk.morningCards.value
            if (cards.isEmpty()) {
                Toast.makeText(requireContext(), "আজকের পরামর্শ এখনও প্রস্তুত নয়", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            CoachingCardBottomSheet.show(parentFragmentManager, scenarioId = cards.first().scenarioId, autoSpeak = true)
        }

        binding.btnChatAssistant.visibility = View.VISIBLE
        binding.btnChatAssistant.setOnClickListener {
            if (sdk.modelManager.isModelPresent()) {
                CoachingAssistantActivity.launch(requireContext())
            } else {
                showModelDownloadPrompt(onDownloadStarted = { CoachingAssistantActivity.launch(requireContext()) })
            }
        }
    }

    private fun showModelDownloadPrompt(onDownloadStarted: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("AI মডেল প্রয়োজন")
            .setMessage("AI মডেল ডাউনলোড করতে হবে (~800 MB)। ডাউনলোড করবেন?")
            .setPositiveButton("হ্যাঁ") { _, _ ->
                if (isOnMeteredNetwork()) {
                    showMeteredNetworkWarning(onDownloadStarted)
                } else {
                    triggerDownloadAndProceed(onDownloadStarted)
                }
            }
            .setNegativeButton("না", null)
            .show()
    }

    private fun showMeteredNetworkWarning(onDownloadStarted: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("মোবাইল ডেটা")
            .setMessage("আপনি মোবাইল ডেটায় আছেন। ~800 MB ডেটা ব্যবহার হবে। চালিয়ে যাবেন?")
            .setPositiveButton("হ্যাঁ") { _, _ -> triggerDownloadAndProceed(onDownloadStarted) }
            .setNegativeButton("না", null)
            .show()
    }

    private fun triggerDownloadAndProceed(onDownloadStarted: () -> Unit) {
        MicroCoachingSDK.getInstance().modelManager.triggerDownload()
        onDownloadStarted()
    }

    private fun isOnMeteredNetwork(): Boolean {
        val cm = requireContext().getSystemService(AndroidConnectivityManager::class.java)
        val caps = cm?.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
    }

    private fun attachObserver() {
        viewModel.menuList.observe(viewLifecycleOwner) { resourceState ->
            when (resourceState.state) {
                ResourceState.LOADING -> showLoading()
                ResourceState.SUCCESS -> {
                    hideLoading()
                    resourceState.data?.let {
                        setAdapterViews(it)
                    }
                }
                ResourceState.ERROR -> {
                    hideLoading()
                }
            }
        }
    }

    private fun setAdapterViews(menuList: List<MenuEntity>) {
        if (CommonUtils.checkIsTablet(requireContext())) {
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.CENTER
            binding.rvActivitiesList.layoutManager = layoutManager
        } else {
            val layoutManager = GridLayoutManager(context, 2)
            binding.rvActivitiesList.layoutManager = layoutManager
        }

        binding.rvActivitiesList.adapter = ActivitiesAdapter(menuList, this,
            SecuredPreference.getIsTranslationEnabled())
    }

    override fun onMenuSelected(name: String) {
        when {
            connectivityManager.isNetworkAvailable() -> {
                handleMenuSelection(name)
            }
            name == UIConstants.screeningUniqueID -> {
                handleMenuSelection(name)
            }
            else -> {
                (activity as BaseActivity).showErrorDialogue(
                    getString(R.string.error),
                    getString(R.string.no_internet_error),
                    isNegativeButtonNeed = false
                ) {}
            }
        }
    }

    private fun handleMenuSelection(name: String) {
        when (name) {
            UIConstants.screeningUniqueID -> {
                startActivity(Intent(requireContext(), GeneralDetailsActivity::class.java))
            }
            else -> {
                val intent = Intent(requireContext(), AdvancedSearchActivity::class.java)
                intent.putExtra(DefinedParams.Origin, name)
                startActivity(intent)
            }
        }
    }
}