package com.example.kt6_3.di

import com.example.kt6_3.presentation.common.SessionManager
import com.example.kt6_3.presentation.login.LoginViewModel
import com.example.kt6_3.presentation.userdetail.UserDetailViewModel
import com.example.kt6_3.presentation.users.UsersListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    single { SessionManager(get()) }

    viewModel { LoginViewModel(get(), get()) }

    viewModel { UsersListViewModel(get()) }

    viewModel { parameters ->
        UserDetailViewModel(
            userRepository = get(),
            sessionManager = get(),
            userId = parameters.get()
        )
    }
}