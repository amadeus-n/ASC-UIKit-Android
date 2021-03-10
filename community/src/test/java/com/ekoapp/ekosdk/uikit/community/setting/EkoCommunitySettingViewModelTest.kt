package com.ekoapp.ekosdk.uikit.community.setting

import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.EkoCommunityRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.reactivex.Completable
import org.junit.Assert.*
import org.junit.Test

class EkoCommunitySettingViewModelTest {

    @Test
    fun initTest() {
        val viewModel = EkoCommunitySettingViewModel()
        assertEquals(viewModel.communityId.get(), "")
        assertEquals(viewModel.membersCount.get(), "0")
        assertTrue(viewModel.isPublic.get())
        assertFalse(viewModel.isModerator.get())
    }

    @Test
    fun when_leaveCommunity_expect_success() {
        mockkStatic(EkoClient::class)
        val communityRepository: EkoCommunityRepository = mockk()
        every { EkoClient.newCommunityRepository() } returns communityRepository
        every { communityRepository.leaveCommunity(any()) } returns Completable.complete()

        val viewModel = EkoCommunitySettingViewModel()
        val res = viewModel.leaveCommunity().test()
        res.assertComplete()
    }

    @Test
    fun when_leaveCommunity_expect_failure() {
        mockkStatic(EkoClient::class)
        val communityRepository: EkoCommunityRepository = mockk()
        every { EkoClient.newCommunityRepository() } returns communityRepository
        every { communityRepository.leaveCommunity(any()) } returns Completable.error(Exception("test_error"))

        val viewModel = EkoCommunitySettingViewModel()
        val res = viewModel.leaveCommunity().test()
        res.assertErrorMessage("test_error")
    }

    @Test
    fun when_deleteCommunity_expect_success() {
        mockkStatic(EkoClient::class)
        val communityRepository: EkoCommunityRepository = mockk()
        every { EkoClient.newCommunityRepository() } returns communityRepository
        every { communityRepository.deleteCommunity(any()) } returns Completable.complete()

        val viewModel = EkoCommunitySettingViewModel()
        val res = viewModel.closeCommunity().test()
        res.assertComplete()
    }

    @Test
    fun when_deleteCommunity_expect_failure() {
        mockkStatic(EkoClient::class)
        val communityRepository: EkoCommunityRepository = mockk()
        every { EkoClient.newCommunityRepository() } returns communityRepository
        every { communityRepository.deleteCommunity(any()) } returns Completable.error(Exception("test_error"))

        val viewModel = EkoCommunitySettingViewModel()
        val res = viewModel.closeCommunity().test()
        res.assertErrorMessage("test_error")
    }
}