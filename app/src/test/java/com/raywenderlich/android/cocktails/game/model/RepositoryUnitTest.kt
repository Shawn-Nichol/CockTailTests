package com.raywenderlich.android.cocktails.game.model

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.*
import com.raywenderlich.android.cocktails.common.network.CocktailsApi
import com.raywenderlich.android.cocktails.common.repository.CocktailsRepository
import com.raywenderlich.android.cocktails.common.repository.CocktailsRepositoryImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * RunWith: annotation is to instruct that youar egoing to write tests using Mockito. Now you can
 * annotate using @Mock every property that you'll later use as mocks. Notice that in the setup
 */
@RunWith(MockitoJUnitRunner::class)
class RepositoryUnitTest {

    @Mock
    private lateinit var api: CocktailsApi
    @Mock
    private lateinit var sharedPreferences: SharedPreferences
    @Mock
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private lateinit var repository: CocktailsRepository

    @Before
    fun setup() {
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

    @Test
    fun saveScore_shouldNotSaveToSharedPReferencesIfLower() {
        val previouslySavedHighScore = 100
        val newHighScore = 10
        // Spy will le ou call he methods of real objects, while also tracking every interaction
        val spyRepository = spy(repository)
        // When setting up spies you need doReturn/whenever/method to stub a method.
        doReturn(previouslySavedHighScore)
                .whenever(spyRepository)
                .getHighScore()

        spyRepository.saveHighScore(newHighScore)

        verify(sharedPreferencesEditor, never())
                .putInt(any(), eq(newHighScore))
    }
}