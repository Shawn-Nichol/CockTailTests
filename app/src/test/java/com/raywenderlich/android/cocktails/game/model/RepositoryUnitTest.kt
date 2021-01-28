package com.raywenderlich.android.cocktails.game.model

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.*
import com.raywenderlich.android.cocktails.common.network.CocktailsApi
import com.raywenderlich.android.cocktails.common.repository.CocktailsRepository
import com.raywenderlich.android.cocktails.common.repository.CocktailsRepositoryImpl
import org.junit.Before
import org.junit.Test

class RepositoryUnitTest {

    private lateinit var api: CocktailsApi
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private lateinit var repository: CocktailsRepository

    @Before
    fun setup() {
        // create Mock objects
        api = mock()
        sharedPreferences = mock()
        sharedPreferencesEditor = mock()
        // Stub, return sharedPreference editor
        whenever(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor)
        repository = CocktailsRepositoryImpl(api, sharedPreferences)

    }

    @Test
    fun saveScore_shouldSaveToSharedPreferences() {
        // Execute the save high score method
        val score = 100
        repository.saveHighScore(score)

        // check that the subsequent verifications are executed in the exact order
        inOrder(sharedPreferencesEditor) {
            // verify that the score is saved correctly.
            verify(sharedPreferencesEditor).putInt(any(), eq(score))
            verify(sharedPreferencesEditor).apply()
        }
    }

    @Test
    fun getScore_shouldGetFromSharedPreferences() {
        repository.getHighScore()

        verify(sharedPreferences).getInt(any(), any())
    }
}