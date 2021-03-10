package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.comment.EkoCommentReference
import com.ekoapp.ekosdk.comment.option.EkoCommentSortOption
import com.ekoapp.ekosdk.comment.query.EkoCommentLoader
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject


private const val COMMENT_PREVIEW_SIZE = 3
private const val TYPICAL_REPLY_PAGE_SIZE = 5

class EkoCommentReplyLoader(comment: EkoComment) {

    private var loader: EkoCommentLoader

    init {
        val postId = (comment.getReference() as? EkoCommentReference.Post)?.getPostId() ?: ""
        loader = EkoClient.newCommentRepository()
            .getCommentCollection()
            .post(postId)
            .parentId(comment.getCommentId())
            .sortBy(EkoCommentSortOption.LAST_CREATED)
            .includeDeleted(true)
            .build()
            .loader()
    }

    private val commentsSubject = PublishSubject.create<List<EkoComment>>()
    private val showLoadMoreButtonSubject = PublishSubject.create<Boolean>()
    private var loadedComments: List<EkoComment> = emptyList()
    private var publishingComments: List<EkoComment> = emptyList()
    private var isLoading: Boolean = false
    private var publishingSize = COMMENT_PREVIEW_SIZE
    private var selfLoad: Boolean = false

    fun showLoadMoreButton(): Flowable<Boolean> {
        return showLoadMoreButtonSubject.toFlowable(BackpressureStrategy.BUFFER)
            .doOnSubscribe {
                showLoadMoreButtonSubject.onNext(shouldShowLoadMoreButton())
            }
    }

    fun getComments(): Flowable<List<EkoComment>> {
        return Flowable.combineLatest(
            loader.getResult(),
            commentsSubject.toFlowable(BackpressureStrategy.BUFFER).startWith(mutableListOf<EkoComment>()),
            BiFunction { loadedResult, publishingResult ->
                if(publishingResult.size > publishingComments.size) {
                    publishingComments = publishingResult
                } else {
                    loadedComments = loadedResult
                    publishingComments = loadedComments.take(publishingSize)
                }
                showLoadMoreButtonSubject.onNext(shouldShowLoadMoreButton())
                publishingComments
            }
        )
    }

    fun load(): Completable {
        if(!selfLoad && !shouldShowLoadMoreButton()) {
            showLoadMoreButtonSubject.onNext(shouldShowLoadMoreButton())
            return Completable.complete()
        }
        selfLoad = false
        isLoading = true
        showLoadMoreButtonSubject.onNext(shouldShowLoadMoreButton())
        val targetSize = publishingSize + TYPICAL_REPLY_PAGE_SIZE
        if (loadedComments.size >= targetSize) {
            publishingSize = targetSize
            isLoading = false
            commentsSubject.onNext(loadedComments.take(publishingSize))
            return Completable.complete()
        } else {
            return loader.load().concatWith(Completable.defer {
                val keepLoading = loader.hasMore() && loadedComments.size < targetSize
                if(keepLoading) {
                    selfLoad = true
                    load()
                } else {
                    publishingSize = targetSize
                    isLoading = false
                    commentsSubject.onNext(loadedComments.take(publishingSize))
                    Completable.complete()
                }
            }).doFinally {
                isLoading = false
                showLoadMoreButtonSubject.onNext(shouldShowLoadMoreButton())
            }
        }
    }

    private fun shouldShowLoadMoreButton() : Boolean {
        return !isLoading && (loadedComments.size > publishingComments.size || loader.hasMore())
    }

}