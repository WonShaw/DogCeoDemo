package io.github.wonshaw.dogceodemo.presentation.quiz

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import io.github.wonshaw.dogceodemo.presentation.compose.theme.DogCeoDemoTheme
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class QuizScreenTest {
    @get:Rule
    val rule = createComposeRule()

    private val imageBitmap: ImageBitmap = ImageBitmap(1, 1, ImageBitmapConfig.Argb8888)
    private val option1 = QuizOptions(id = "affenpinscher", displayText = "affenpinscher")
    private val option2 = QuizOptions(id = "african", displayText = "african")
    private val option3 = QuizOptions(id = "australian_kelpie", displayText = "australian kelpie")
    private val option4 =
        QuizOptions(id = "australian_shepherd", displayText = "australian shepherd")
    private val breedOptions = listOf(option1, option2, option3, option4)

    @Test
    fun show_loading_when_loading_without_quiz() {
        rule.setContent {
            DogCeoDemoTheme {
                QuizScreen(
                    uiState = QuizViewUiState(
                        loading = true,
                        errorMsg = null,
                        breedOptions = emptyList(),
                        imageBitmap = null,
                        showCorrectAnswer = null,
                    )
                )
            }
        }

        loadingViewExists()
        quizContentNotExists()
        answerDialogNotExists()
    }


    @Test
    fun show_error_msg_when_error_without_quiz() {
        val errorMsg = "qwertyu"
        rule.setContent {
            DogCeoDemoTheme {
                QuizScreen(
                    uiState = QuizViewUiState(
                        loading = false,
                        errorMsg = errorMsg,
                        breedOptions = emptyList(),
                        imageBitmap = null,
                        showCorrectAnswer = null,
                    )
                )
            }
        }

        rule.onNode(hasText(errorMsg)).assertExists()
        loadingViewNotExists()
        quizContentNotExists()
        answerDialogNotExists()
    }

    @Test
    fun show_quiz_content_when_quiz_exists() {
        rule.setContent {
            DogCeoDemoTheme {
                QuizScreen(
                    uiState = QuizViewUiState(
                        loading = false,
                        errorMsg = null,
                        breedOptions = breedOptions,
                        imageBitmap = imageBitmap,
                        showCorrectAnswer = null,
                    )
                )
            }
        }

        quizContentExists()
        loadingViewNotExists()
        answerDialogNotExists()
    }

    @Test
    fun show_answer_dialog_when_user_answers_correctly() {
        val correctAnswer = "asdfghji"
        rule.setContent {
            DogCeoDemoTheme {
                QuizScreen(
                    uiState = QuizViewUiState(
                        loading = false,
                        errorMsg = null,
                        breedOptions = breedOptions,
                        imageBitmap = imageBitmap,
                        showCorrectAnswer = correctAnswer,
                    )
                )
            }
        }

        rule.onNode(hasText(correctAnswer, true)).assertExists()
        answerDialogExists()
        quizContentExists()
        loadingViewNotExists()
    }

    @Test
    fun show_loading_when_user_choose_next_quiz() {
        rule.setContent {
            DogCeoDemoTheme {
                QuizScreen(
                    uiState = QuizViewUiState(
                        loading = true,
                        errorMsg = null,
                        breedOptions = breedOptions,
                        imageBitmap = imageBitmap,
                        showCorrectAnswer = null,
                    )
                )
            }
        }

        loadingViewExists()
        quizContentExists()
        answerDialogNotExists()
    }

    @Test
    fun show_error_when_loading_next_quiz_fails() {
        val errorMsg = "qwertyu"
        rule.setContent {
            DogCeoDemoTheme {
                QuizScreen(
                    uiState = QuizViewUiState(
                        loading = false,
                        errorMsg = errorMsg,
                        breedOptions = breedOptions,
                        imageBitmap = imageBitmap,
                        showCorrectAnswer = null,
                    )
                )
            }
        }

        rule.onNode(hasText(errorMsg)).assertExists()
        loadingViewNotExists()
        quizContentExists()
        answerDialogNotExists()
    }

    @Test
    fun click_answer_dialog_dismiss_calls_close_answer_dialog() {
        val closeAnswerDialog = mockk<()->Unit>(relaxed = true)
        rule.setContent {
            DogCeoDemoTheme {
                QuizScreen(
                    uiState = QuizViewUiState(
                        loading = false,
                        errorMsg = null,
                        breedOptions = breedOptions,
                        imageBitmap = imageBitmap,
                        showCorrectAnswer = "asdfghji",
                    ),
                    closeAnswerDialog = closeAnswerDialog,
                )
            }
        }

        rule.onNode(hasTestTag(TEST_TAG_ANSWER_DIALOG_DISMISS)).performClick()
        verify(exactly = 1) { closeAnswerDialog() }
    }


    @Test
    fun click_answer_dialog_next_calls_next_quiz() {
        val nextQuiz = mockk<()->Unit>(relaxed = true)
        rule.setContent {
            DogCeoDemoTheme {
                QuizScreen(
                    uiState = QuizViewUiState(
                        loading = false,
                        errorMsg = null,
                        breedOptions = breedOptions,
                        imageBitmap = imageBitmap,
                        showCorrectAnswer = "asdfghji",
                    ),
                    nextQuiz = nextQuiz
                )
            }
        }

        rule.onNode(hasTestTag(TEST_TAG_ANSWER_DIALOG_CONFIRM)).performClick()
        verify(exactly = 1) { nextQuiz() }
    }

    @Test
    fun click_retry_calls_retry() {
        val retry = mockk<()->Unit>(relaxed = true)
        rule.setContent {
            DogCeoDemoTheme {
                QuizScreen(
                    uiState = QuizViewUiState(
                        errorMsg = "error msg",
                    ),
                    retry = retry
                )
            }
        }

        rule.onNode(hasTestTag(TEST_TAG_RETRY_BUTTON)).performClick()
        verify(exactly = 1) { retry() }
    }

    @Test
    fun click_option_calls_option_selected() {
        val optionSelected = mockk<(String)->Unit>(relaxed = true)
        rule.setContent {
            DogCeoDemoTheme {
                QuizScreen(
                    uiState = QuizViewUiState(
                        loading = false,
                        errorMsg = null,
                        breedOptions = breedOptions,
                        imageBitmap = imageBitmap,
                    ),
                    optionSelected = optionSelected
                )
            }
        }

        rule.onNode(hasText(option4.displayText)).performClick()

        verify(exactly = 1) { optionSelected(option4.id) }
    }

    private fun quizContentExists() {
        rule.onNode(hasTestTag(TEST_TAG_QUIZ_CONTENT)).assertExists()
    }

    private fun quizContentNotExists() {
        rule.onNode(hasTestTag(TEST_TAG_QUIZ_CONTENT)).assertDoesNotExist()
    }

    private fun loadingViewExists() {
        rule.onNode(hasTestTag(TEST_TAG_LOADING_VIEW)).assertExists()
    }

    private fun loadingViewNotExists() {
        rule.onNode(hasTestTag(TEST_TAG_LOADING_VIEW)).assertDoesNotExist()
    }

    private fun answerDialogExists() {
        rule.onNode(hasTestTag(TEST_TAG_ANSWER_DIALOG)).assertExists()
    }

    private fun answerDialogNotExists() {
        rule.onNode(hasTestTag(TEST_TAG_ANSWER_DIALOG)).assertDoesNotExist()
    }
}