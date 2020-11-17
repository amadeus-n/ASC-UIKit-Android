package com.ekoapp.ekosdk.uikit.community.profile.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.base.EkoPickerFragment
import com.ekoapp.ekosdk.uikit.common.views.dialog.EkoBottomSheetDialogFragment
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.FragmentEkoEditUserProfileBinding
import com.ekoapp.ekosdk.uikit.community.profile.viewmodel.EkoEditUserProfileViewModel
import com.ekoapp.ekosdk.uikit.components.EkoToolBarClickListener
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import com.ekoapp.ekosdk.user.EkoUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_eko_category_list.*
import java.io.File


class EkoEditUserProfileFragment : EkoPickerFragment(), EkoToolBarClickListener {
    private val TAG = EkoEditUserProfileFragment::class.java.canonicalName
    private val mViewModel: EkoEditUserProfileViewModel by activityViewModels()
    lateinit var mBinding: FragmentEkoEditUserProfileBinding
    private var profileUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addViewModelListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_eko_edit_user_profile,
                container,
                false
            )
        mBinding.lifecycleOwner = viewLifecycleOwner
        mBinding.vm = mViewModel
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar()
        getUserDetails()
        mBinding.avatarView.setOnClickListener {
            showOptionTakePhoto()
        }

        observeProfileUpdate()
    }


    private fun observeProfileUpdate() {
        mViewModel.mediatorLiveData.observe(viewLifecycleOwner, Observer {
            mViewModel.checkProfileUpdate()
        })
        mViewModel.hasProfileUpdate.observe(viewLifecycleOwner, Observer {
            toolbar.setRightStringActive(it)
        })
    }


    override fun onImagePicked(data: Uri?) {
        profileUri = data
        setProfilePicture(profileUri)
        mViewModel.checkProfileUpdate()
    }

    private fun setProfilePicture(profileUri: Uri?) {
        mViewModel.updateProfileUri(profileUri)
        Glide.with(requireContext())
            .load(profileUri)
            .placeholder(R.drawable.ic_uikit_default_profile)
            .centerCrop()
            .into(mBinding.ivAvatar)
    }

    override fun onFilePicked(data: Uri?) {

    }

    override fun onPhotoClicked(file: File?) {
        profileUri = Uri.fromFile(file)
        setProfilePicture(profileUri)
        mViewModel.checkProfileUpdate()
    }


    private fun getUserDetails() {
        mViewModel.getUser()?.also {
            disposable.add(
                it.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it != null) {
                            mViewModel.user = it
                            setupUserData(it)
                        }
                    }, {
                        Log.d(TAG, it.message)
                    })
            )
        }
    }

    private fun setupUserData(user: EkoUser) {
        mBinding.etDisplayName.setText(user.getDisplayName())
        mBinding.etAbout.setText(user.getDescription())
        user.getAvatar()?.getUrl(EkoImage.Size.SMALL)?.apply {
            setProfilePicture(Uri.parse(this))
        }
    }

    private fun initToolBar() {
        toolbar.setLeftDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_uikit_arrow_back)
        )
        toolbar.setLeftString(getString(R.string.edit_profile))
        toolbar.setRightString(getString(R.string.save))
        toolbar.setRightStringActive(false)
        toolbar.setClickListener(this)
        (activity as AppCompatActivity).supportActionBar?.displayOptions =
            ActionBar.DISPLAY_SHOW_CUSTOM
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    override fun leftIconClick() {
        activity?.onBackPressed()
    }

    override fun rightIconClick() {
        if (profileUri == null) {
            updateUser()
        } else {
            uploadProfilePicture(profileUri!!)
        }

    }

    private fun addViewModelListener() {
        mViewModel.onEventReceived += { event ->
            when (event.type) {
                EventIdentifier.PROFILE_PICTURE_UPLOAD_FAILED -> {
                    handleErrorProfilePictureUpload()
                }
                EventIdentifier.PROFILE_PICTURE_UPLOAD_SUCCESS -> {
                    updateUser()
                }
                else -> {
                }
            }
        }
    }

    private fun handleErrorProfilePictureUpload() {
        Toast.makeText(
            requireContext(),
            getString(R.string.upload_failed_profile_picture),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun uploadProfilePicture(uri: Uri) {
        disposable.add(
            mViewModel.uploadProfilePicture(
                uri
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    mViewModel.updateImageUploadStatus(it)
                }.doOnError {
                    Log.d(TAG, it.message)
                    mViewModel.errorOnUpdate()
                }.subscribe()
        )
    }

    private fun showOptionTakePhoto() {
        val fragment =
            EkoBottomSheetDialogFragment.newInstance(R.menu.eko_upload_profile_picture)

        fragment.show(childFragmentManager, EkoBottomSheetDialogFragment.toString())
        fragment.setOnNavigationItemSelectedListener(object :
            EkoBottomSheetDialogFragment.OnNavigationItemSelectedListener {
            override fun onItemSelected(item: MenuItem) {
                handleUploadPhotoOption(item)
            }

        })
    }

    private fun handleUploadPhotoOption(item: MenuItem) {
        if (item.itemId == R.id.actionTakePicture) {
            takePicture()
        } else if (item.itemId == R.id.actionPickPicture) {
            pickImage()
        }
    }

    private fun updateUser() {
        disposable.add(
            mViewModel.updateUser().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    activity?.onBackPressed()
                }, {
                    Log.d(TAG, it.message)
                    mViewModel.errorOnUpdate()
                    context?.also {
                        Toast.makeText(
                            requireContext(),
                            getText(R.string.edit_profile_update_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        )
    }


    class Builder() {
        fun build(activity: AppCompatActivity): EkoEditUserProfileFragment {
            return EkoEditUserProfileFragment()
        }
    }

}