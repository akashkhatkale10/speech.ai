package com.speechai.speechai.di

import com.speechai.speechai.data.repository.LoginRepository
import com.speechai.speechai.data.repository.StorageRepository
import com.speechai.speechai.data.repository.UserRepository
import com.speechai.speechai.data.repository.impl.LoginRepositoryImpl
import com.speechai.speechai.data.repository.impl.StorageRepositoryImpl
import com.speechai.speechai.data.repository.impl.UserRepositoryImpl
import com.speechai.speechai.prefs.SharedPrefs
import com.speechai.speechai.prefs.SharedPrefsImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindLoginRepository(loginRepositoryImpl: LoginRepositoryImpl): LoginRepository

    @Binds
    abstract fun bindStorageRepository(storageRepositoryImpl: StorageRepositoryImpl): StorageRepository

    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindSharedPrefs(sharedPrefsImpl: SharedPrefsImpl): SharedPrefs
}