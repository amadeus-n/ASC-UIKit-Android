package com.ekoapp.ekosdk.uikit.community.members

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.ekoapp.ekosdk.uikit.community.R

/**
 * A simple [Fragment] subclass.
 * Use the [EkoModeratorsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EkoModeratorsFragment : Fragment() {
    private val mViewModel: EkoCommunityMembersViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_eko_moderators, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment EkoModeratorsFragment.
         */
        @JvmStatic
        fun newInstance() = EkoModeratorsFragment()
    }
}