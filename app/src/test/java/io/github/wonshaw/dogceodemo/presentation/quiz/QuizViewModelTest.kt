package io.github.wonshaw.dogceodemo.presentation.quiz

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.github.wonshaw.dogceodemo.domain.model.DogBreed
import io.github.wonshaw.dogceodemo.domain.model.DogBreedQuiz
import io.github.wonshaw.dogceodemo.domain.usecase.GetDogBreedQuizUseCase
import io.github.wonshaw.dogceodemo.domain.usecase.UseCaseResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class QuizViewModelTest {
    private lateinit var useCase: GetDogBreedQuizUseCase
    private val option1 = DogBreed(id = "affenpinscher", breed = "affenpinscher")
    private val option2 = DogBreed(id = "african", breed = "african")
    private val correctAnswer =
        DogBreed(id = "australian_kelpie", breed = "australian", subBreed = "kelpie")
    private val option4 =
        DogBreed(id = "australian_shepherd", breed = "australian", subBreed = "shepherd")
    private val quizMock = DogBreedQuiz(
        mockk(),
        listOf(
            option1,
            option2,
            correctAnswer,
            option4,
        ),
        correctAnswer,
    )
    private val expectedOptions = listOf(
        option1,
        option2,
        correctAnswer,
        option4,
    ).map { QuizOptions(it.id, it.getDisplayName()) }

    @Before
    fun setup() {
        useCase = mockk()
    }

    @Test
    fun `loadNextQuiz should update uiState with new quiz when successful`() = runTest {
        coEvery { useCase.execute() } returns UseCaseResult.Success(quizMock)

        val viewModel = QuizViewModel(useCase)

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertThat(state.imageBitmap).isNotNull()
            assertThat(state.loading).isFalse()
            assertThat(state.errorMsg).isNull()
            assertThat(state.breedOptions).containsExactlyElementsIn(expectedOptions)
        }
    }

    @Test
    fun `loadNextQuiz should update uiState with error message when failed`() = runTest {
        val errorMessage = "Network Error"
        coEvery { useCase.execute() } returns UseCaseResult.Error(errorMessage)

        val viewModel = QuizViewModel(useCase)

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertThat(state.imageBitmap).isNull()
            assertThat(state.loading).isFalse()
            assertThat(state.errorMsg).isEqualTo(errorMessage)
            assertThat(state.breedOptions).isEmpty()
        }
    }

    @Test
    fun `selectOption should update uiState when correct answer is chosen`() = runTest {
        coEvery { useCase.execute() } returns UseCaseResult.Success(quizMock)
        coEvery { useCase.checkAnswer(quizMock, correctAnswer.id) } returns true
        coEvery { useCase.checkAnswer(quizMock, not(correctAnswer.id)) } returns false

        val viewModel = QuizViewModel(useCase)
        viewModel.uiState.test {
            expectMostRecentItem()
        }
        viewModel.selectOption(correctAnswer.id)

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertThat(state.imageBitmap).isNotNull()
            assertThat(state.loading).isFalse()
            assertThat(state.errorMsg).isNull()
            assertThat(state.breedOptions).containsExactlyElementsIn(expectedOptions)
            assertThat(state.showCorrectAnswer).isEqualTo(correctAnswer.getDisplayName())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `selectOption should update uiState when wrong answer is chosen`() = runTest {
        coEvery { useCase.execute() } returns UseCaseResult.Success(quizMock)
        coEvery { useCase.checkAnswer(quizMock, correctAnswer.id) } returns true
        coEvery { useCase.checkAnswer(quizMock, not(correctAnswer.id)) } returns false

        val viewModel = QuizViewModel(useCase)
        viewModel.uiState.test {
            expectMostRecentItem()
        }
        viewModel.selectOption(option1.id)

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertThat(state.imageBitmap).isNotNull()
            assertThat(state.loading).isFalse()
            assertThat(state.errorMsg).isNull()
            assertThat(state.breedOptions).containsExactlyElementsIn(expectedOptions.map {
                if (it.id == option1.id) it.copy(
                    shaking = true
                ) else it
            })
            assertThat(state.showCorrectAnswer).isNull()
        }
        advanceTimeBy(1000)
        viewModel.uiState.test {
            awaitItem()
            val state = awaitItem()
            assertThat(state.imageBitmap).isNotNull()
            assertThat(state.loading).isFalse()
            assertThat(state.errorMsg).isNull()
            assertThat(state.breedOptions).containsExactlyElementsIn(expectedOptions)
            assertThat(state.showCorrectAnswer).isNull()
        }
    }

    @Test
    fun `closeAnswerDialog should clear showCorrectAnswer`() = runTest {
        coEvery { useCase.execute() } returns UseCaseResult.Success(quizMock)
        coEvery { useCase.checkAnswer(quizMock, correctAnswer.id) } returns true
        coEvery { useCase.checkAnswer(quizMock, not(correctAnswer.id)) } returns false

        val viewModel = QuizViewModel(useCase)
        viewModel.uiState.test {
            expectMostRecentItem()
        }
        viewModel.selectOption(correctAnswer.id)
        viewModel.closeAnswerDialog()

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertThat(state.imageBitmap).isNotNull()
            assertThat(state.loading).isFalse()
            assertThat(state.errorMsg).isNull()
            assertThat(state.breedOptions).containsExactlyElementsIn(expectedOptions)
            assertThat(state.showCorrectAnswer).isNull()
        }
    }
}