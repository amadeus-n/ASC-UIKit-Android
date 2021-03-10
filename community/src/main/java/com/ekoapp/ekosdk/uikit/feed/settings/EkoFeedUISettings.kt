package com.ekoapp.ekosdk.uikit.feed.settings

object EkoFeedUISettings {

    var postShareClickListener: IPostShareClickListener = object : IPostShareClickListener {}

    var postSharingSettings = EkoPostSharingSettings()

    private var postViewHolders: MutableMap<String, EkoIPostViewHolder> =
        EkoDefaultPostViewHolders.getDefaultMap()

    fun registerPostViewHolders(viewHolders: List<EkoIPostViewHolder>) {
        viewHolders.forEach { viewHolder ->
            postViewHolders[viewHolder.getDataType()] = viewHolder
        }
    }

    internal fun getViewHolder(dataType: String): EkoIPostViewHolder {
        return postViewHolders[dataType] ?: EkoDefaultPostViewHolders.unknownViewHolder
    }

    internal fun getViewHolder(viewType: Int): EkoIPostViewHolder {
        for (viewHolder in postViewHolders.values.toList()) {
            if (viewType == viewHolder.getDataType().hashCode()) {
                return viewHolder
            }
        }
        return EkoDefaultPostViewHolders.unknownViewHolder
    }
}