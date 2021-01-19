package com.ekoapp.ekosdk.uikit.common.views.bottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.uikit.R
import com.ekoapp.ekosdk.uikit.databinding.LayoutBottomSheetItemBinding
import com.ekoapp.ekosdk.uikit.model.EkoMenuItem

class EkoBottomSheetAdapter(
        private val list: List<EkoMenuItem>,
        private val listener: IEkoMenuItemClickListener?
) :
        RecyclerView.Adapter<EkoBottomSheetAdapter.BottomSheetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: LayoutBottomSheetItemBinding =
                DataBindingUtil.inflate(inflater, R.layout.layout_bottom_sheet_item, parent, false)
        return BottomSheetViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class BottomSheetViewHolder(
            private val binding: LayoutBottomSheetItemBinding,
            private val listener: IEkoMenuItemClickListener?
    ) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EkoMenuItem?) {
            if (item != null) {
                binding.menuItem = item
                binding.listener = listener
                binding.alertColor = item.title == itemView.context.getString(R.string.remove_user)
            }
        }
    }
}