package com.ekoapp.ekosdk.uikit.community.ui.viewModel

import androidx.paging.PagedList
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.EkoUserRepository
import com.ekoapp.ekosdk.uikit.community.data.SelectMemberItem
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import com.ekoapp.ekosdk.user.EkoUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.reactivex.Flowable
import org.junit.Assert.*
import org.junit.Test

class EkoSelectMembersViewModelTest {

    @Test
    fun initTest() {
        val viewModel = EkoSelectMembersViewModel()
        assertEquals(viewModel.searchString.get(), "")
        assertEquals(viewModel.selectedMembersList.size, 0)
        assertEquals(viewModel.selectedMemberSet.size, 0)
        assertEquals(viewModel.memberMap.size, 0)
        assertEquals(viewModel.searchMemberMap.size, 0)
        assertFalse(viewModel.isSearchUser.get())
    }

    @Test
    fun when_getAllUsers_expect_Flowablex_PagedListEkoUser() {
        val mockList: PagedList<EkoUser> = mockk()
        every { mockList.size } returns 5
        mockkStatic(EkoClient::class)
        val userRepository: EkoUserRepository = mockk()
        every { EkoClient.newUserRepository() } returns userRepository
        every { userRepository.searchUserByDisplayName(any()).build().query() } returns Flowable.just(mockList)

        val viewModel = EkoSelectMembersViewModel()
        val res = viewModel.getAllUsers().blockingFirst()
        assertEquals(res.size, 5)
    }

    @Test
    fun when_searchUser_expect_Flowablex_PagedListEkoUser() {
        val mockList: PagedList<EkoUser> = mockk()
        every { mockList.size } returns 5
        mockkStatic(EkoClient::class)
        val userRepository: EkoUserRepository = mockk()
        every { EkoClient.newUserRepository() } returns userRepository
        every { userRepository.searchUserByDisplayName(any()).build().query() } returns Flowable.just(mockList)

        val viewModel = EkoSelectMembersViewModel()
        viewModel.searchString.set("Sum")
        val res = viewModel.searchUser().blockingFirst()
        assertEquals(res.size, 5)
    }

    @Test
    fun prepareSelectedMembersListTest() {
        val member = SelectMemberItem("1", "testUrl", "testName", "", true)
        val viewModel = EkoSelectMembersViewModel()

        viewModel.prepareSelectedMembersList(member, true)
        assertEquals(viewModel.selectedMembersList.size, 1)

        viewModel.prepareSelectedMembersList(member, false)
        assertEquals(viewModel.selectedMembersList.size, 0)
    }

    @Test
    fun when_searchString_change_expect_event_SEARCH_STRING_CHANGED() {
        var searchStringChanged = false
        val viewModel = EkoSelectMembersViewModel()
        viewModel.onEventReceived += {event->
            when(event.type) {
                EventIdentifier.SEARCH_STRING_CHANGED -> searchStringChanged = true
                else -> {}
            }
        }
        viewModel.setPropertyChangeCallback()
        viewModel.searchString.set("123")
        assertTrue(searchStringChanged)
    }
}