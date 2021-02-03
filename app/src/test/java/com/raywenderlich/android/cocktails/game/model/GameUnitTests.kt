/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.cocktails.game.model

import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

class GameUnitTests {
    @Test
    fun whenGettingNextQuestion_shouldReturnIt() {
        val question1 = Question("CORRECT", "INCORRECT")
        val questions = listOf(question1)
        val game = Game(questions)

        val nextQuestion = game.nextQuestion()

        Assert.assertSame(question1, nextQuestion)
    }

    @Test
    fun whenGettingNextQuestion_withoutMoreQuestions_shouldReturnNull() {
        val question1 = Question("CORRECT", "INCORRECT")
        val questions = listOf(question1)
        val game = Game(questions)

        game.nextQuestion()
        val nextQuestion = game.nextQuestion()

        Assert.assertNull(nextQuestion)
    }

    @Test
    fun whenAnswering_shouldDelegateToQuestion() {
        // Create a mock of question.
        val question = mock<Question>()
        val game = Game(listOf(question))

        // Call the answer method with the mock as a parameter.
        game.answer(question, "OPTION")

        // Verify the method answer was called on teh Question mock.
        // Times 1 is used to confirm that answer was called exactly one time.
        // Argument matcher is used to check that the answer method was called with a String equal to OPTION
        verify(question, times(1)).answer(eq("OPTION"))
    }

    @Test
    fun whenAnsweringCorrectly_shouldIncrementCurrentScore() {
        // Creates a mock question object.
        val question = mock<Question>()
        // Stubs the question.answer method with anyString and always returns false.
        whenever(question.answer(anyString())).thenReturn(true)

        val score = mock<Score>()
        val game = Game(listOf(question), score)

        // call the answer() of the game.
        game.answer(question, "OPTION")

        // Check that the game score was incremented
        verify(score).increment()
    }

    @Test
    fun whenAnsweringIncorrectly_shouldNotIncrementScore() {
        // Creating a mock questions object
        val question = mock<Question>()
        // Stubs the question.answer method with anyString and always returns false.
        whenever(question.answer(anyString())).thenReturn(false)
        val score = mock<Score>()

        val game = Game(listOf(question))
        // calls the answer() of the game object
        game.answer(question, "OPTION")

        // Checks that the game score was not incremented.
        verify(score, never()).increment()
    }

    @Test
    fun whenAnsweringIncorrectly_increaseQuestionIncorrect() {
        // create a mock question
        val question = mock<Question>()
        // stubing
        whenever(question.answer(anyString())).thenReturn(false)

        val game = Game(listOf(question))
        game.answer(question, "OPTION")

        // Check that questionsAnsweredIncorrectly increases
        Assert.assertEquals(1, game.questionsAnsweredIncorrectly)
    }

    @Test
    fun whenAnsweringIncorrectly_threeTimesGameIsOver() {
        val question1 = mock<Question>()
        val question2 = mock<Question>()
        val question3 = mock<Question>()

        whenever(question1.answer(anyString())).thenReturn(false)
        whenever(question2.answer(anyString())).thenReturn(false)
        whenever(question3.answer(anyString())).thenReturn(false)

        val question = listOf(question1, question2, question3)
        val game = Game(question)
        game.answer(question1, "INCORRECT")
        game.answer(question2, "INCORRECT")
        game.answer(question3, "INCORRECT")

        Assert.assertTrue(game.isOver)
    }

    @Test
    fun whenAnsweringCorrectly_IncreaseSequentialScore() {
        val question = mock<Question>()

        whenever(question.answer(anyString())).thenReturn(true)

        val game = Game(listOf(question))
        game.answer(question, "CORRECT")

        Assert.assertEquals(1, game.questionsAnsweredCorrectlySequentially)
    }

    @Test
    fun whenAnsweringIncorrectly_resetSequentialScore() {
        val question = mock<Question>()
        whenever(question.answer(anyString())).thenReturn(false)

        val game = Game(listOf(question))
        game.questionsAnsweredCorrectlySequentially = 1
        game.answer(question, "INCORRECT")

        Assert.assertEquals(0, game.questionsAnsweredCorrectlySequentially)
    }

    @Test
    fun whenAnsweringCorrectly_threeTimesInARow() {
        val question1 = mock<Question>()
        val question2 = mock<Question>()
        val question3 = mock<Question>()
        val question4 = mock<Question>()
        val score = mock<Score>()

        // Stub
        whenever(question1.answer(anyString())).thenReturn(true)
        whenever(question2.answer(anyString())).thenReturn(true)
        whenever(question3.answer(anyString())).thenReturn(true)
        whenever(question4.answer(anyString())).thenReturn(true)

        val question = listOf(question1, question2, question3, question4)
        val game = Game(question, score)
        game.answer(question1, "CORRECT")
        game.answer(question2, "CORRECT")
        game.answer(question3, "CORRECT")
        game.answer(question4, "CORRECT")

        verify(score, times(1 + 1 + 1 + 2)).increment()
    }
}