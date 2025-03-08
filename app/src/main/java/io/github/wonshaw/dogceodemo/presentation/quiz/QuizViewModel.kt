package io.github.wonshaw.dogceodemo.presentation.quiz

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.wonshaw.dogceodemo.domain.model.DogBreedQuiz
import io.github.wonshaw.dogceodemo.domain.usecase.GetDogBreedQuizUseCase
import io.github.wonshaw.dogceodemo.domain.usecase.UseCaseResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(private val useCase: GetDogBreedQuizUseCase) :
    ViewModel() {
    private val _uiState = MutableStateFlow(QuizViewUiState())
    val uiState: StateFlow<QuizViewUiState> = _uiState.asStateFlow()

    private var currentQuiz: DogBreedQuiz? = null

    init {
        loadNextQuiz()
    }

    fun loadNextQuiz() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showCorrectAnswer = null,
                    loading = true,
                    errorMsg = null,
                )
            }

            val resultData = useCase.execute()
            if (resultData is UseCaseResult.Success) {
                currentQuiz = resultData.data
                val breedOptions = resultData.data.options.map { dogBreed ->
                    QuizOptions(id = dogBreed.id, displayText = dogBreed.getDisplayName())
                }
                _uiState.update {
                    it.copy(
                        breedOptions = breedOptions,
                        imageBitmap = resultData.data.imageBitmap,
                        errorMsg = null,
                        loading = false,
                    )
                }
            } else if (resultData is UseCaseResult.Error) {
                _uiState.update {
                    it.copy(errorMsg = resultData.message, loading = false)
                }
            } else {
                _uiState.update {
                    it.copy(errorMsg = "unknown error", loading = false)
                }
            }
        }
    }

    fun selectOption(selectedId: String) {
        val currentQuiz = currentQuiz
        if (currentQuiz == null) {
            _uiState.update {
                it.copy(errorMsg = "no quiz")
            }
        } else {
            val isCorrect = useCase.checkAnswer(currentQuiz, selectedId)
            if (isCorrect) {
                _uiState.update {
                    it.copy(
                        showCorrectAnswer = currentQuiz.correctAnswer.getDisplayName()
                    )
                }
            } else {
                viewModelScope.launch {
                    _uiState.update { uiState ->
                        uiState.copy(
                            breedOptions = uiState.breedOptions.map { quizOption ->
                                if (quizOption.id == selectedId) {
                                    quizOption.copy(shaking = true)
                                } else {
                                    quizOption
                                }
                            }
                        )
                    }
                    delay(500)
                    _uiState.update { uiState ->
                        uiState.copy(
                            breedOptions = uiState.breedOptions.map { quizOption ->
                                if (quizOption.id == selectedId) {
                                    quizOption.copy(shaking = false)
                                } else {
                                    quizOption
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    fun closeAnswerDialog() {
        _uiState.update {
            it.copy(showCorrectAnswer = null)
        }
    }
}

data class QuizViewUiState(
    val loading: Boolean = false,
    val errorMsg: String? = null,
    val breedOptions: List<QuizOptions> = emptyList(),
    val imageBitmap: ImageBitmap? = null,
    val showCorrectAnswer: String? = null,
)