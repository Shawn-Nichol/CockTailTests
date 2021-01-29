package com.raywenderlich.android.cocktails.game.model

import com.nhaarman.mockitokotlin2.*
import com.raywenderlich.android.cocktails.common.network.Cocktail
import com.raywenderlich.android.cocktails.common.repository.CocktailsRepository
import com.raywenderlich.android.cocktails.common.repository.RepositoryCallback
import com.raywenderlich.android.cocktails.game.CocktailsGameFactory
import com.raywenderlich.android.cocktails.game.CocktailsGameFactoryImpl
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CocktailsGameFactoryUnitTests {

    private val cocktails = listOf(
            Cocktail("1", "Drink 1", "image 1"),
            Cocktail("2", "Drink 2", "image 2"),
            Cocktail("3", "Drink 3", "image 3"),
            Cocktail("4", "Drink 4", "image 4")

    )

    private lateinit var repository: CocktailsRepository
    private lateinit var factory: CocktailsGameFactory

    @Before
    fun setup() {
        repository = mock()
        factory = CocktailsGameFactoryImpl(repository)

    }

    @Test
    fun buildGame_shouldGetCocktailsFromRepo() {
        factory.buildGame(mock())

        verify(repository).getAlcoholic(any())
    }

    @Test
    fun buildGame_shouldCallOnSuccess() {
        // Mock object
        val callback = mock<CocktailsGameFactory.Callback>()
        setupRepositoryWithCocktails(repository)

        //
        factory.buildGame(callback)

        // Verifies that the game is created.
        verify(callback).onSuccess(any())
    }

    @Test
    fun buildGame_shouldCallOnError() {
        val callback = mock<CocktailsGameFactory.Callback>()
        setUpRepositoryWithError(repository)

        factory.buildGame(callback)
        // Verify that the game wasn't created.
        verify(callback).onError()
    }

    @Test
    fun buildGame_shouldGetHighScoreFromRepo() {
        setupRepositoryWithCocktails(repository)
        factory.buildGame(mock())

        verify(repository).getHighScore()
    }

    @Test
    fun buildGame_shouldBuildGameWithHighScore() {
        setupRepositoryWithCocktails(repository)
        val highScore = 100

        // Stubbing repository high score
        whenever(repository.getHighScore()).thenReturn(highScore)

        factory.buildGame(object : CocktailsGameFactory.Callback {
            override fun onSuccess(game: Game) {
                Assert.assertEquals(highScore, game.score.highest)
            }

            override fun onError() {
                Assert.fail()
            }
        })
    }

    @Test
    fun buildGame_shouldBuildGameWithQuestions() {
        setupRepositoryWithCocktails(repository)

        factory.buildGame(object : CocktailsGameFactory.Callback {
            override fun onSuccess(game: Game) {
                cocktails.forEach {
                    assertQuestion(game.nextQuestion(),
                            it.strDrink,
                            it.strDrinkThumb)
                }
            }

            override fun onError() {
                Assert.fail()
            }
        })
    }

    private fun setupRepositoryWithCocktails(repository: CocktailsRepository) {
        // doAnswer is used to stub the repository.getAlcoholic() with success and return a list.
        doAnswer {
            // Mock list created from repository
            val callback: RepositoryCallback<List<Cocktail>, String> = it.getArgument(0)
            // Mock method that we are spying on.
            callback.onSuccess(cocktails)
        }.whenever(repository).getAlcoholic(any())
    }

    private fun setUpRepositoryWithError(repository: CocktailsRepository) {
        // doAnswer is used to stub the repository.getAlcoholic() with success and return a list.
        doAnswer {
            // Mock list createdd form repository
            val callback: RepositoryCallback<List<Cocktail>, String> = it.getArgument(0)
            // Mock method that we are spying on.
            callback.onError("Error")
        }.whenever(repository).getAlcoholic(any())
    }

    private fun assertQuestion(
            question: Question?,
            correctOption: String,
            imageUrl: String?) {

        Assert.assertNotNull(question)
        Assert.assertEquals(imageUrl, question?.imageUrl)
        Assert.assertEquals(correctOption, question?.correctOption)
        Assert.assertNotEquals(correctOption, question?.incorrectOption)
    }
}