package com.dicoding.greenquest

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.greenquest.data.Repository
import com.dicoding.greenquest.di.Injection
import com.dicoding.greenquest.ui.customizeprofile.CustomizeProfileViewModel
import com.dicoding.greenquest.ui.home.HomeViewModel
import com.dicoding.greenquest.ui.leaderboard.LeaderboardViewModel
import com.dicoding.greenquest.ui.login.LoginViewModel
import com.dicoding.greenquest.ui.main.MainViewModel
import com.dicoding.greenquest.ui.materi.MateriViewModel
import com.dicoding.greenquest.ui.mission.MissionViewModel
import com.dicoding.greenquest.ui.profile.ProfilesViewModel
import com.dicoding.greenquest.ui.quiz.QuizViewModel
import com.dicoding.greenquest.ui.register.SignupViewModel
import com.dicoding.greenquest.ui.scan.ScanViewModel

class ViewModelFactory(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ScanViewModel::class.java) -> {
                ScanViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfilesViewModel::class.java) -> {
                ProfilesViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LeaderboardViewModel::class.java) -> {
                LeaderboardViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MateriViewModel::class.java) -> {
                MateriViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MissionViewModel::class.java) -> {
                MissionViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CustomizeProfileViewModel::class.java) -> {
                CustomizeProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(QuizViewModel::class.java) -> {
                QuizViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}