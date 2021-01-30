package viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import com.raywenderlich.android.cocktails.common.repository.CocktailsRepository
import com.raywenderlich.android.cocktails.game.CocktailsGameFactory
import com.raywenderlich.android.cocktails.game.model.Game
import com.raywenderlich.android.cocktails.game.model.Question
import com.raywenderlich.android.cocktails.game.model.Score
import com.raywenderlich.android.cocktails.game.viewmodel.CocktailsGameViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CocktailsGameViewModelUnitTests {

    // This is a test rule. A test rule is a tool to change the way test run, sometimes adding additional checks or running code before and after tests.
    // android Architecture components use a background executor that is asynchronous  to do its magic.
    // InstantTaskExecutorRule is a rule that swaps out that executor and replaces with synchronous one.
    // This will make sure that when, you're using LiveData with ViewModel, it's all run synchronously in
    // the tests.

    @get: Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: CocktailsRepository
    private lateinit var factory: CocktailsGameFactory
    private lateinit var viewModel: CocktailsGameViewModel
    private lateinit var game: Game
    private lateinit var loadingObserver: Observer<Boolean>
    private lateinit var errorObserver: Observer<Boolean>
    private lateinit var scoreObserver: Observer<Score>
    private lateinit var questionObserver: Observer<Question>

    @Before
    fun setup() {
        // THe ViewModel being tested will require a cocktail repository to save the high score and
        // cocktailsGameFactory to build a game. These are dependencies , so you need to mock them.
        repository = mock()
        factory = mock()
        viewModel = CocktailsGameViewModel(repository, factory)

        // A game mock will be used to stub some of the methods.
        game = mock()

        // Tests will require mocked observers because the Activity will observe LiveData objects exposed
        // by the ViewModel. In the UI. Because there is no lifecycle in the tests we can you observer
        // forever.
        loadingObserver = mock()
        errorObserver = mock()
        scoreObserver = mock()
        questionObserver = mock()
        viewModel.getLoading().observeForever(loadingObserver)
        viewModel.getScore().observeForever(scoreObserver)
        viewModel.getQuestion().observeForever(questionObserver)
        viewModel.getError().observeForever(errorObserver)



    }

    @Test
    fun init_shouldBuildGame() {
        viewModel.initGame()

        // Verfiy: that certain behavior happened once.
        verify(factory).buildGame(any())
    }

    /**
     * Verify that initGame publishes the correct data. When the program posts a value to a LiveData,
     * The object calls onChanged with the value.
     */
    @Test
    fun init_shouldShowLoading() {
        // Verifies the init game publishes teh correct data.
        viewModel.initGame()

        // Verifies: certain behavior happened once.
        // onChanged: Observer, notifies when data is changed
        // eq: Matcher, argument is equal to given value.
        verify(loadingObserver).onChanged(eq(true))
        // You can also use "Assert.assertTrue(viewModel.getLoading().value!!)"
    }

    /**
     * Verify that initGame publishes the correct data. When the program post a value to a LiveData,
     * The object calls onChanged with the value.
     */
    @Test
    fun init_shouldHideError() {
        viewModel.initGame()

        // Verify: a certain behavior has occurred once.
        // onChanged: Observer, notifies when data is changed
        // eq Matcher, argument is equal to given value.
        verify(errorObserver).onChanged(eq(false))
    }


    /**
     * Used to stub the buildGame() method from the CocktailsGameFactory class.
     */
    private fun setUpFactoryWithSuccessGame(game: Game) {
        doAnswer {
            val callback: CocktailsGameFactory.Callback =
                    it.getArgument(0)
            callback.onSuccess(game)
        }.whenever(factory.buildGame(any()))
    }

    /**
     * Used to stube the buildGame() method from the CocktailsGameFactory class.
     */
    private fun setupFactoryWithError() {
        doAnswer {
            val callback: CocktailsGameFactory.Callback =
                    it.getArgument(0)
            callback.onError()
        }.whenever(factory).buildGame(any())
    }
}