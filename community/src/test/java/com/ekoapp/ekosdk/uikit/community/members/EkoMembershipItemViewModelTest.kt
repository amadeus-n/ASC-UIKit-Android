package com.ekoapp.ekosdk.uikit.community.members

import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.EkoUserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.reactivex.Completable
import org.junit.Test

class EkoMembershipItemViewModelTest {

    @Test
    fun when_reportUser_expect_success() {
        mockkStatic(EkoClient::class)
        val userRepository: EkoUserRepository = mockk()
        every { EkoClient.newUserRepository() } returns userRepository
        every { userRepository.report(any()).flag() } returns Completable.complete()
        val viewModel = EkoMembershipItemViewModel()
        val res = viewModel.reportUser("123").test()
        res.assertComplete()
    }

    @Test
    fun when_reportUser_expect_failure() {
        mockkStatic(EkoClient::class)
        val userRepository: EkoUserRepository = mockk()
        every { EkoClient.newUserRepository() } returns userRepository
        every { userRepository.report(any()).flag() } returns Completable.error(Exception("test_error"))
        val viewModel = EkoMembershipItemViewModel()
        val res = viewModel.reportUser("123").test()
        res.assertErrorMessage("test_error")
    }
}