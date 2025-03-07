package io.github.wonshaw.dogceodemo.di

import android.content.Context
import coil3.ImageLoader
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.wonshaw.dogceodemo.data.remote.DogCeoApi
import io.github.wonshaw.dogceodemo.domain.usecase.CoilResultToBitmapConverter
import io.github.wonshaw.dogceodemo.domain.usecase.ICoilResultToBitmapConverter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppHiltModule {
    @Provides
    @Singleton
    fun provideDogCeoApi(): DogCeoApi {
        val moshi = Moshi.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        return retrofit.create(DogCeoApi::class.java)
    }

    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context).build()
    }

    @Provides
    fun provideImageConverter(): ICoilResultToBitmapConverter {
        return CoilResultToBitmapConverter()
    }
}