package com.adventistportal.chat.presentation.di

import com.adventistportal.chat.presentation.chat_detail.ChatDetailViewModel
import com.adventistportal.chat.presentation.chat_list.ChatListViewModel
import com.adventistportal.chat.presentation.chat_list_detail.ChatListDetailViewModel
import com.adventistportal.chat.presentation.create_chat.CreateChatViewModel
import com.adventistportal.chat.presentation.manage_chat.ManageChatViewModel
import com.adventistportal.chat.presentation.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val chatPresentationModule = module {
    viewModelOf(::ChatListViewModel)
    viewModelOf(::ChatListDetailViewModel)
    viewModelOf(::CreateChatViewModel)
    viewModelOf(::ChatDetailViewModel)
    viewModelOf(::ManageChatViewModel)
    viewModelOf(::ProfileViewModel)
}