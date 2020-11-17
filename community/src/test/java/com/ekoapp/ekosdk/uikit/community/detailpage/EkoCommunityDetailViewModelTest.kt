package com.ekoapp.ekosdk.uikit.community.detailpage

import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.EkoCommunityRepository
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.community.category.EkoCommunityCategory
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.reactivex.Completable
import io.reactivex.Flowable
import org.junit.Assert.*
import org.junit.Test
import java.lang.Exception

class EkoCommunityDetailViewModelTest {

    @Test
    fun initTest() {
        val viewModel = EkoCommunityDetailViewModel()
        assertEquals(viewModel.communityID, "")
        assertEquals(viewModel.avatarUrl.get(), "")
        assertEquals(viewModel.category.get(), "")
        assertEquals(viewModel.posts.get(), "0")
        assertEquals(viewModel.members.get(), "0")
        assertEquals(viewModel.description.get(), "")
        assertTrue(viewModel.isPublic.get())
        assertTrue(viewModel.isMember.get())
        assertFalse(viewModel.isOfficial.get())
        assertFalse(viewModel.isCreator.get())
        assertFalse(viewModel.isModerator.get())
    }

    @Test
    fun when_GetCommunityDetails_expect_flowable_EkoCommunity() {
        val ekoCommunity: EkoCommunity = mockk()
        mockkStatic(EkoClient::class)
        val communityRepository: EkoCommunityRepository = mockk()
        every { EkoClient.newCommunityRepository() } returns communityRepository
        every { communityRepository.getCommunity(any()) } returns Flowable.just(ekoCommunity)
        every { ekoCommunity.getChannelId() } returns "testId"
        val viewModel = EkoCommunityDetailViewModel()
        val res = viewModel.getCommunityDetail().blockingFirst()
        assertEquals(res.getChannelId(), "testId")
    }

    @Test
    fun setCommunityTest() {
        val displayName = "DisplayName"
        val postCount = 1000
        val membersCount = 10
        val description = "Description"
        val avatar: EkoImage = mockk()
        mockkStatic(EkoClient::class)
        every { EkoClient.getUserId() } returns "testUser"
        val ekoCommunityCategory: EkoCommunityCategory = mockk()
        every { ekoCommunityCategory.getName() } returns "test"
        val mockList = ArrayList<EkoCommunityCategory>()
        mockList.add(ekoCommunityCategory)
        val ekoCommunity: EkoCommunity = mockk()
        every { ekoCommunity.getDisplayName() } returns displayName
        every { ekoCommunity.getPostCount() } returns postCount
        every { ekoCommunity.getMemberCount() } returns membersCount
        every { ekoCommunity.getDescription() } returns description
        every { ekoCommunity.isPublic() } returns true
        every { ekoCommunity.isJoined() } returns true
        every { ekoCommunity.isOfficial() } returns true
        every { ekoCommunity.getDescription() } returns description
        every { ekoCommunity.getUserId() } returns "testUser"
        every { avatar.getUrl(any()) } returns "testUrl"
        every { ekoCommunity.getAvatar() } returns avatar
        every { ekoCommunity.getCategories() } returns mockList

        val viewModel = EkoCommunityDetailViewModel()
        viewModel.setCommunity(ekoCommunity)
        assertEquals(viewModel.name.get(), displayName)
        assertEquals(viewModel.category.get(), "test")
        assertEquals(viewModel.posts.get(), "1K")
        assertEquals(viewModel.members.get(), "10")
        assertEquals(viewModel.description.get(), description)
        assertEquals(viewModel.avatarUrl.get(), "testUrl")
        assertTrue(viewModel.isPublic.get())
        assertTrue(viewModel.isMember.get())
        assertTrue(viewModel.isOfficial.get())
        assertTrue(viewModel.isCreator.get())
    }

    @Test
    fun when_join_community_expect_success() {
        mockkStatic(EkoClient::class)
        val communityRepository: EkoCommunityRepository = mockk()
        every { EkoClient.newCommunityRepository() } returns communityRepository
        every { communityRepository.joinCommunity(any()) } returns Completable.complete()
        val viewModel = EkoCommunityDetailViewModel()
        val res = viewModel.joinCommunity().test()
        res.assertComplete()
    }

    @Test
    fun when_join_community_expect_failure() {
        mockkStatic(EkoClient::class)
        val communityRepository: EkoCommunityRepository = mockk()
        every { EkoClient.newCommunityRepository() } returns communityRepository
        every { communityRepository.joinCommunity(any()) } returns Completable.error(Exception("test_error"))
        val viewModel = EkoCommunityDetailViewModel()
        val res = viewModel.joinCommunity().test()
        res.assertErrorMessage("test_error")
    }

    @Test
    fun onPrimaryButtonClickTest() {
        var editProfile = false
        val viewModel = EkoCommunityDetailViewModel()
        viewModel.isCreator.set(true)
        viewModel.isModerator.set(true)
        viewModel.onEventReceived += {event->
            when(event.type) {
                EventIdentifier.EDIT_PROFILE -> editProfile = true
                else -> {}
            }
        }
        viewModel.onPrimaryButtonClick()
        assertTrue(editProfile)
    }
}