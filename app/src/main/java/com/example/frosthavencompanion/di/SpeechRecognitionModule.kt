package com.example.frosthavencompanion.di

import android.content.Context
import com.example.frosthavencompanion.data.speechRecognition.SpeechRecognitionHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped


@Module
@InstallIn(ActivityComponent::class)
object SpeechRecognitionModule {


    @Provides
    @ActivityScoped
    fun provideSpeechRecognitionHelper(@ActivityContext context: Context): SpeechRecognitionHelper {
        return SpeechRecognitionHelper(context)
    }

}