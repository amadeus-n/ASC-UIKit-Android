package com.ekoapp.ekosdk.uikit.community.explore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekoapp.ekosdk.community.category.EkoCommunityCategory
import com.ekoapp.ekosdk.uikit.base.EkoBaseFragment
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.explore.adapter.EkoCategoryListAdapter
import com.ekoapp.ekosdk.uikit.community.explore.listener.IEkoCategoryItemClickListener
import com.ekoapp.ekosdk.uikit.community.explore.viewmodel.EkoCategoryListViewModel
import com.ekoapp.ekosdk.uikit.utils.EkoRecyclerViewItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_eko_category_list.*

const val ARG_DEFAULT_SELECTION = "default_selection"

abstract class EkoBaseCategoryListFragment internal constructor() : EkoBaseFragment(),
        IEkoCategoryItemClickListener {
    internal lateinit var mViewModel: EkoCategoryListViewModel

    private lateinit var adapter: EkoCategoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = getCategoryListAdapter()
    }

    abstract fun getCategoryListAdapter(): EkoCategoryListAdapter

    private fun setupToolBar() {
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.category)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(requireActivity()).get(EkoCategoryListViewModel::class.java)
        return inflater.inflate(R.layout.fragment_eko_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolBar()
        initView()
        getCategories()
    }

    private fun initView() {
        val itemDecorSpace =
                EkoRecyclerViewItemDecoration(resources.getDimensionPixelSize(R.dimen.eight))
        rvCategory.layoutManager = LinearLayoutManager(requireContext())
        rvCategory.adapter = adapter
        rvCategory.addItemDecoration(itemDecorSpace)
    }

    private fun getCategories() {
        disposable.add(mViewModel.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    run {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }

                }
                .subscribe { result ->
                    run {
                        adapter.submitList(result)
                    }
                })
    }

    override fun onCategorySelected(category: EkoCommunityCategory) {
        mViewModel.categoryItemClickListener?.onCategorySelected(category)
    }

    fun setCategoryItemClickListener(listener: IEkoCategoryItemClickListener) {
        mViewModel.categoryItemClickListener = listener
    }
}
