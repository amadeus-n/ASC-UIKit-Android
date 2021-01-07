package com.ekoapp.ekosdk.uikit.community.members

import com.ekoapp.ekosdk.user.EkoUser
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import org.junit.Test

class EkoMembershipItemViewModelTest {

    @Test
    fun when_reportUser_expect_success() {

        val ekoUser: EkoUser = mockk()
        every { ekoUser.report().flag() } returns Completable.complete()
        val viewModel = EkoMembershipItemViewModel()
        val res = viewModel.reportUser(ekoUser).test()
        res.assertComplete()
    }

    @Test
    fun when_reportUser_expect_failure() {
        val ekoUser: EkoUser = mockk()
        every { ekoUser.report().flag() } returns Completable.error(Exception("test_error"))
        val viewModel = EkoMembershipItemViewModel()
        val res = viewModel.reportUser(ekoUser).test()
        res.assertErrorMessage("test_error")
    }
}