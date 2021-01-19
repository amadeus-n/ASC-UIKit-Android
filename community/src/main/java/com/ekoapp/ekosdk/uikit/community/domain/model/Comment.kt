package com.ekoapp.ekosdk.uikit.community.domain.model

data class Comment(
        val id: String,
        var editedAt: Long,
        var deleted: Boolean = false,
        var edited: Boolean = false,
        val user: User,
        var data: String,
        val reactionCount: Int,
        val childrenNumber: Int,
        val createdAt: Long,
        val replies: List<Comment>?, val parentId: String? = null
)