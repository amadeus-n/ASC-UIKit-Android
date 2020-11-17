package com.ekoapp.ekosdk.uikit.community.ui.view

import android.Manifest
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.ekoapp.ekosdk.file.upload.EkoImageUpload
import com.ekoapp.ekosdk.uikit.common.toCircularShape
import com.ekoapp.ekosdk.uikit.common.views.ColorPaletteUtil
import com.ekoapp.ekosdk.uikit.common.views.ColorShade
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.detailpage.EkoCommunityPageActivity
import com.ekoapp.ekosdk.uikit.community.data.SelectCategoryItem
import com.ekoapp.ekosdk.uikit.community.databinding.FragmentEkoCreateCommunityBinding
import com.ekoapp.ekosdk.uikit.community.explore.activity.EkoCategorySelectionActivity
import com.ekoapp.ekosdk.uikit.community.ui.viewModel.EkoCreateCommunityViewModel
import com.ekoapp.ekosdk.uikit.contract.EkoPickImageContract
import com.ekoapp.ekosdk.uikit.utils.AlertDialogUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_eko_create_community.*

abstract class EkoCommunityCreateBaseFragment: Fragment() {

    private val TAG = EkoCommunityCreateBaseFragment::class.java.canonicalName
    var disposable = CompositeDisposable()
    private var imageUri: Uri? = null
    val mViewModel: EkoCreateCommunityViewModel by activityViewModels()
    private lateinit var mBinding: FragmentEkoCreateCommunityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_eko_create_community, container, false
        )
        mBinding.viewModel = mViewModel
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.category.setOnClickListener {
            launchCategorySelection(mViewModel.category.get()!!)
        }

        mBinding.categoryArrow.setOnClickListener {
            launchCategorySelection(mViewModel.category.get()!!)
        }
        addInitialStateChangeListener()
        setUpBackPress()
        setAvatar()
        uploadImageAndCreateCommunity()
    }

    private fun uploadImageAndCreateCommunity() {
        mBinding.btnCreateCommunity.setOnClickListener {
            mViewModel.createIdList()
            uploadImage(false)
        }
    }

    private fun addInitialStateChangeListener() {
        mViewModel.setPropertyChangeCallback()
    }

    fun getBindingVariable(): FragmentEkoCreateCommunityBinding = mBinding

    private fun pickImage() {
        val pickImagePermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    val pickImage = registerForActivityResult(EkoPickImageContract()) { data ->
                        imageUri = data
                        mViewModel.initialStateChanged.value = true
                        Glide.with(this)
                            .load(data)
                            .circleCrop()
                            .dontAnimate()
                            .into(ccAvatar)
                    }
                    pickImage.launch(getString(com.ekoapp.ekosdk.uikit.R.string.choose_image))
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        pickImagePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun launchCategorySelection(preSelectedCategory: SelectCategoryItem) {
        selectCategoryContract.launch(preSelectedCategory)
    }

    private fun setAvatar() {

        mBinding.icCamera.toCircularShape(
            ColorPaletteUtil.getColor(
                ContextCompat.getColor(requireContext(), R.color.upstraColorBase), ColorShade.SHADE4
            ), 2F
        )

        mBinding.lAvatar.setOnClickListener {
            pickImage()
        }

        ccName.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val DRAWABLE_LEFT = 0
                val DRAWABLE_TOP = 1
                val DRAWABLE_RIGHT = 2
                val DRAWABLE_BOTTOM = 3

                if (event?.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (ccName.right - ccName.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                        mViewModel.communityName.set("")
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun createCommunity() {
        disposable.add(mViewModel.createCommunity().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                val detailIntent = EkoCommunityPageActivity
                    .newIntent(requireContext(), it.getCommunityId(), true)
                startActivity(detailIntent)
                requireActivity().finish()
            }
            .doOnError {
                Log.e(TAG, "createCommunity: ${it.localizedMessage}")
            }.subscribe()
        )
    }

    fun onLeftIconClick() {
        if (mViewModel.initialStateChanged.value == true) {
            showDialog()
        } else {
            requireActivity().finish()
        }
    }

    private fun setUpBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mViewModel.initialStateChanged.value == true) {
                    showDialog()
                } else {
                    requireActivity().finish()
                }
            }
        })
    }

    private fun showDialog() {
        AlertDialogUtil.showDialog(requireContext(),
            getString(R.string.cc_leave),
            getString(R.string.cc_dialog_msg),
            getString(R.string.leave),
            getString(R.string.cancel),
            DialogInterface.OnClickListener { dialog, which ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    requireActivity().finish()
                } else {
                    dialog.cancel()
                }
            })
    }

    fun uploadImage(isEditCommunity: Boolean) {
        if (isEditCommunity) {
            mViewModel.initialStateChanged.value = false
        }else {
            mBinding.btnCreateCommunity.isEnabled = false
        }

        if (imageUri != null) {
            disposable.add(mViewModel.uploadProfilePicture(imageUri!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { uploadStatus ->
                    when (uploadStatus) {
                        is EkoImageUpload.COMPLETE -> {
                            mViewModel.ekoImage = uploadStatus.image
                            if (isEditCommunity) {
                                editCommunity()
                            }else {
                                createCommunity()
                            }
                        }
                        is EkoImageUpload.ERROR, EkoImageUpload.CANCELLED -> {
                            btnCreateCommunity.isEnabled = true
                            mViewModel.initialStateChanged.value = true
                            Toast.makeText(
                                requireContext(),
                                "Image upload Error",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        else -> {
                        }
                    }
                }.doOnError {
                    Log.e(TAG, "uploadImageAndCreateCommunity: ${it.localizedMessage}")
                    mBinding.btnCreateCommunity.isEnabled = true
                    mViewModel.initialStateChanged.value = true
                }.subscribe()
            )
        } else {
            if (isEditCommunity) {
                editCommunity()
            }else {
                createCommunity()
            }
        }
    }

        private fun editCommunity() {
        mViewModel.editCommunity().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                requireActivity().finish()
            }.doOnError {
                Log.e(TAG, "save: ${it.localizedMessage}")
            }.subscribe()
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    private var selectCategoryContract = registerForActivityResult(
        EkoCategorySelectionActivity.EkoCategorySelectionActivityContract()
    ) {
        it?.let {
            mViewModel.setCategory(it)
        }


    }
}