package com.ekoapp.ekosdk.uikit.chat.messages.viewModel

import android.net.Uri
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.EkoChannelRepository
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.EkoMessageRepository
import com.ekoapp.ekosdk.message.EkoMessage
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Flowable
import org.junit.Assert
import org.junit.Test

class EkoMessageListViewModelTest {

    @Test
    fun when_joinChannel_Expect_Success() {
        mockkStatic(EkoClient::class)
        val channelRepository: EkoChannelRepository = mockk()
        every { EkoClient.newChannelRepository() } returns channelRepository
        every { channelRepository.joinChannel(any()).ignoreElement() } returns Completable.complete()

        val viewModel = EkoMessageListViewModel()
        val res = viewModel.joinChannel().test()
        res.assertComplete()
    }

    @Test
    fun when_joinChannel_Expect_Failure() {
        mockkStatic(EkoClient::class)
        val channelRepository: EkoChannelRepository = mockk()
        every { EkoClient.newChannelRepository() } returns channelRepository
        every { channelRepository.joinChannel(any()).ignoreElement() } returns Completable.error(Exception("error_message"))

        val viewModel = EkoMessageListViewModel()
        val res = viewModel.joinChannel().test()
        res.assertErrorMessage("error_message")
    }

    @Test
    fun startReadingTest() {
        mockkStatic(EkoClient::class)
        val channelRepository: EkoChannelRepository = mockk()
        every { EkoClient.newChannelRepository() } returns channelRepository
        every { channelRepository.membership(any()).startReading() } returns Unit
        val viewModel = EkoMessageListViewModel()
        viewModel.startReading()
        verify(exactly = 1) { channelRepository.membership(any()).startReading() }

    }

    @Test
    fun stopReadingTest() {
        mockkStatic(EkoClient::class)
        val channelRepository: EkoChannelRepository = mockk()
        every { EkoClient.newChannelRepository() } returns channelRepository
        every { channelRepository.membership(any()).stopReading() } returns Unit
        val viewModel = EkoMessageListViewModel()
        viewModel.stopReading()
        verify(exactly = 1) { channelRepository.membership(any()).stopReading() }

    }

    @Test
    fun when_getAllMessages_expect_pagedListOfEkoMessage() {
        mockkStatic(EkoClient::class)
        val messageRepository: EkoMessageRepository = mockk()
        every { EkoClient.newMessageRepository() } returns messageRepository
        val mockList: PagedList<EkoMessage> = mockk()
        every { mockList.size } returns 5
        every {
            messageRepository.getMessageCollection(any()).parentId(any()).build().query()
        } returns Flowable.just(mockList)

        val viewModel = EkoMessageListViewModel()
        val res = viewModel.getAllMessages().blockingFirst()
        Assert.assertEquals(res.size, 5)
    }

    @Test
    fun when_sendMessage_expect_success() {
        var msgSuccess = false
        val viewModel = EkoMessageListViewModel()
        viewModel.text.set("Test text")
        Assert.assertEquals(viewModel.text.get(), "Test text")

        mockkStatic(EkoClient::class)
        val messageRepository: EkoMessageRepository = mockk()
        every { EkoClient.newMessageRepository() } returns messageRepository
        every {
            messageRepository.createMessage(any()).with().text(any()).build().send()
        } returns Completable.complete()

        viewModel.onEventReceived += { event ->
            when (event.type) {
                EventIdentifier.MSG_SEND_SUCCESS -> msgSuccess = true
                else -> {
                }
            }
        }

        viewModel.sendMessage()
        Assert.assertTrue(msgSuccess)
        Assert.assertEquals(viewModel.text.get(), "")

    }

    @Test
    fun when_sendMessage_expect_failure() {
        var msgError = false
        val viewModel = EkoMessageListViewModel()
        viewModel.text.set("Test text")
        Assert.assertEquals(viewModel.text.get(), "Test text")

        mockkStatic(EkoClient::class)
        val messageRepository: EkoMessageRepository = mockk()
        every { EkoClient.newMessageRepository() } returns messageRepository
        every {
            messageRepository.createMessage(any()).with().text(any()).build().send()
        } returns Completable.error(Exception("test_exception"))

        viewModel.onEventReceived += { event ->
            when (event.type) {
                EventIdentifier.MSG_SEND_ERROR -> msgError = true
                else -> {
                }
            }
        }

        viewModel.sendMessage()
        Assert.assertTrue(msgError)
        Assert.assertEquals(viewModel.text.get(), "")
    }

    @Test
    fun when_sendImageMessage_expect_success() {
        val viewModel = EkoMessageListViewModel()
        mockkStatic(EkoClient::class)
        val messageRepository: EkoMessageRepository = mockk()
        every { EkoClient.newMessageRepository() } returns messageRepository
        every { messageRepository.createMessage(any()).with().image(any()).build().send() } returns Completable.complete()
        val imageUri: Uri = mockk()
        val res = viewModel.sendImageMessage(imageUri).test()
        res.assertComplete()
    }

    @Test
    fun when_sendImageMessage_expect_failure() {
        val viewModel = EkoMessageListViewModel()
        mockkStatic(EkoClient::class)
        val messageRepository: EkoMessageRepository = mockk()
        every { EkoClient.newMessageRepository() } returns messageRepository
        every { messageRepository.createMessage(any()).with().image(any()).build().send() } returns Completable.error(Exception("test_exception"))
        val imageUri: Uri = mockk()
        val res = viewModel.sendImageMessage(imageUri).test()
        res.assertErrorMessage("test_exception")

    }

    @Test
    fun composeBarClickListenerTest() {
        val viewModel = EkoMessageListViewModel()
        var leftIconClick = false
        var centerIconClick = false
        var rightIconClick = false

        viewModel.onEventReceived += {event->
            when(event.type) {
                EventIdentifier.CAMERA_CLICKED -> leftIconClick = true
                EventIdentifier.PICK_IMAGE -> centerIconClick = true
                EventIdentifier.PICK_FILE -> rightIconClick = true
                else  -> {}
            }
        }

        viewModel.composeBarClickListener.onCameraClicked()
        Assert.assertTrue(leftIconClick)

        viewModel.composeBarClickListener.onAlbumClicked()
        Assert.assertTrue(centerIconClick)

    }

    @Test
    fun onRVScrollStateChangedTest() {
        val viewModel = EkoMessageListViewModel()
        val recyclerView: RecyclerView = mockk()

        viewModel.isRVScrolling = true
        every { recyclerView.computeVerticalScrollRange() } returns 10
        every { recyclerView.height } returns 5
        initialScrollStateTest(recyclerView, viewModel)

        testWhenScrollable(recyclerView, viewModel)

        every { recyclerView.computeVerticalScrollRange() } returns 5
        every { recyclerView.height } returns 10

        testWhenNotScrollable(recyclerView, viewModel)
    }

    private fun initialScrollStateTest(recyclerView: RecyclerView, viewModel: EkoMessageListViewModel) {
        viewModel.onRVScrollStateChanged(recyclerView, RecyclerView.SCROLL_STATE_IDLE)
        Assert.assertFalse(viewModel.isRVScrolling)
    }

    private fun testWhenScrollable(recyclerView: RecyclerView, viewModel: EkoMessageListViewModel) {
        viewModel.isRVScrolling = false
        viewModel.onRVScrollStateChanged(recyclerView, RecyclerView.SCROLL_STATE_DRAGGING)
        Assert.assertTrue(viewModel.isRVScrolling)

        viewModel.isRVScrolling = false
        viewModel.onRVScrollStateChanged(recyclerView, RecyclerView.SCROLL_STATE_SETTLING)
        Assert.assertTrue(viewModel.isRVScrolling)

        viewModel.isRVScrolling = false
        viewModel.onRVScrollStateChanged(recyclerView, RecyclerView.SCROLL_STATE_IDLE)
        Assert.assertFalse(viewModel.isRVScrolling)
    }

    private fun testWhenNotScrollable(recyclerView: RecyclerView, viewModel: EkoMessageListViewModel) {
        viewModel.isRVScrolling = true
        viewModel.onRVScrollStateChanged(recyclerView, RecyclerView.SCROLL_STATE_IDLE)
        Assert.assertFalse(viewModel.isRVScrolling)

        viewModel.isRVScrolling = true
        viewModel.onRVScrollStateChanged(recyclerView, RecyclerView.SCROLL_STATE_SETTLING)
        Assert.assertFalse(viewModel.isRVScrolling)

        viewModel.isRVScrolling = true
        viewModel.onRVScrollStateChanged(recyclerView, RecyclerView.SCROLL_STATE_DRAGGING)
        Assert.assertFalse(viewModel.isRVScrolling)
    }
}