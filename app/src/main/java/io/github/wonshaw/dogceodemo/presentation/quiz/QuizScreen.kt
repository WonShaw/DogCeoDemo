package io.github.wonshaw.dogceodemo.presentation.quiz

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.wonshaw.dogceodemo.R
import io.github.wonshaw.dogceodemo.presentation.compose.theme.DogCeoDemoTheme


private typealias OptionSelectedCallback = (String) -> Unit
private typealias Callback = () -> Unit

@Composable
fun QuizScreenWithViewModel(
    landscape: Boolean = false,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    QuizScreen(
        uiState = uiState.value,
        landscape = landscape,
        optionSelected = viewModel::selectOption,
        retry = viewModel::loadNextQuiz,
        nextQuiz = viewModel::loadNextQuiz,
        closeAnswerDialog = viewModel::closeAnswerDialog,
    )
}

@Composable
private fun QuizScreen(
    uiState: QuizViewUiState,
    landscape: Boolean = false,
    optionSelected: OptionSelectedCallback = {},
    retry: Callback = {},
    nextQuiz: Callback = {},
    closeAnswerDialog: Callback = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.imageBitmap != null) {
            QuizContent(uiState, landscape, optionSelected)
        }
        if (uiState.loading) {
            LoadingView()
        }
        if (uiState.errorMsg != null) {
            ErrorView(uiState.errorMsg, retry = retry)
        }
    }

    if (uiState.showCorrectAnswer != null) {
        AlertDialog(
            onDismissRequest = closeAnswerDialog,
            title = { Text(stringResource(R.string.correct_title)) },
            text = { Text(stringResource(R.string.yes_it_is_sth, uiState.showCorrectAnswer)) },
            confirmButton = {
                Button(onClick = nextQuiz) {
                    Text(stringResource(R.string.next))
                }
            },
            dismissButton = {
                Button(onClick = closeAnswerDialog) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }
}

@Composable
fun ErrorView(errorMsg: String, retry: Callback) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorMsg,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(MaterialTheme.shapes.small)
                .background(color = MaterialTheme.colorScheme.inverseOnSurface)
                .padding(20.dp),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = retry) {
            Text(stringResource(R.string.retry))
        }
    }

}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            .clickable(
                interactionSource = null,
                indication = null
            ) { },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
private fun QuizContent(
    uiState: QuizViewUiState,
    landscape: Boolean,
    optionSelected: OptionSelectedCallback = {}
) {
    if (landscape) {
        QuizContentLandscape(uiState, optionSelected)
    } else {
        QuizContentPortrait(uiState, optionSelected)
    }
}

@Composable
private fun QuizContentPortrait(
    uiState: QuizViewUiState,
    optionSelected: OptionSelectedCallback
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        QuestionSection(uiState.imageBitmap)
        OptionsSection(uiState.breedOptions, optionSelected)
    }
}

@Composable
private fun QuizContentLandscape(
    uiState: QuizViewUiState,
    optionSelected: OptionSelectedCallback
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        QuestionSection(uiState.imageBitmap, modifier = Modifier.weight(14F))
        OptionsSection(uiState.breedOptions, optionSelected, modifier = Modifier.weight(10F))
    }
}

@Composable
private fun QuestionSection(imageBitmap: ImageBitmap?, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            stringResource(R.string.what_breed_is_the_dog_in_the_picture),
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .clip(MaterialTheme.shapes.small)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.inverseOnSurface)
                .padding(20.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87F),
        )
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxHeight()
                    .aspectRatio(4 / 3F)
                    .clip(MaterialTheme.shapes.small)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Fit,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun OptionsSection(
    optionList: List<QuizOptions>,
    optionSelected: OptionSelectedCallback,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.select_one),
            modifier = Modifier.padding(start = 20.dp, bottom = 10.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
                .copy(alpha = 0.6F),
        )

        optionList.forEach { uiModel ->
            key(uiModel.id) {
                SingleOption(uiModel = uiModel, optionSelected = optionSelected)
            }
        }
    }
}

@Composable
private fun SingleOption(
    uiModel: QuizOptions,
    optionSelected: OptionSelectedCallback,
) {
    val shakeAnimation = remember { Animatable(0f) }
    LaunchedEffect(uiModel.shaking) {
        if (uiModel.shaking) {
            repeat(4) {
                shakeAnimation.animateTo(-4f, animationSpec = tween(40))
                shakeAnimation.animateTo(4f, animationSpec = tween(40))
            }
            shakeAnimation.animateTo(0f)
        }
    }
    OutlinedButton(
        onClick = {
            optionSelected(uiModel.id)
        },
        modifier = Modifier
            .padding(
                horizontal = 20.dp,
                vertical = 4.dp
            )
            .fillMaxWidth()
            .offset { IntOffset(x = shakeAnimation.value.dp.roundToPx(), 0) }
    ) {
        Text(uiModel.displayText)
    }
}


@Preview(showBackground = true, name = "portrait")
@Composable
fun QuizScreenPortraitPreview() {
    DogCeoDemoTheme {
        QuizScreen(
            uiState = QuizViewUiState(
                breedOptions = listOf(
                    QuizOptions(id = "1", displayText = "Breed 1"),
                    QuizOptions(id = "2", displayText = "Breed 2"),
                    QuizOptions(id = "3", displayText = "Breed 3"),
                    QuizOptions(id = "4", displayText = "Breed 4"),
                ),
                imageBitmap = ImageBitmap(100, 100),
            ),
        )
    }
}

@Preview(showBackground = true, name = "answer")
@Composable
fun QuizScreenAnswerPreview() {
    DogCeoDemoTheme {
        QuizScreen(
            uiState = QuizViewUiState(
                breedOptions = listOf(
                    QuizOptions(id = "1", displayText = "Breed 1"),
                    QuizOptions(id = "2", displayText = "Breed 2"),
                    QuizOptions(id = "3", displayText = "Breed 3"),
                    QuizOptions(id = "4", displayText = "Breed 4"),
                ),
                imageBitmap = ImageBitmap(100, 100),
                showCorrectAnswer = "Breed 10",
            ),
        )
    }
}

@Preview(showBackground = true, name = "loading")
@Composable
fun QuizScreenPreviewLoading() {
    DogCeoDemoTheme {
        QuizScreen(
            uiState = QuizViewUiState(
                loading = true,
            )
        )
    }
}

@Preview(showBackground = true, name = "error")
@Composable
fun QuizScreenErrorPreview() {
    DogCeoDemoTheme {
        QuizScreen(
            uiState = QuizViewUiState(
                breedOptions = listOf(
                    QuizOptions(id = "1", displayText = "Breed 1"),
                    QuizOptions(id = "2", displayText = "Breed 2"),
                    QuizOptions(id = "3", displayText = "Breed 3"),
                    QuizOptions(id = "4", displayText = "Breed 4"),
                ),
                imageBitmap = ImageBitmap(100, 100),
                errorMsg = "error message",
            ),
        )
    }
}

@Preview(showBackground = true, name = "landscape", widthDp = 640, heightDp = 320)
@Composable
fun QuizScreenLandscapePreview() {
    DogCeoDemoTheme {
        QuizContent(
            uiState = QuizViewUiState(
                breedOptions = listOf(
                    QuizOptions(id = "1", displayText = "Breed 1"),
                    QuizOptions(id = "2", displayText = "Breed 2"),
                    QuizOptions(id = "3", displayText = "Breed 3"),
                    QuizOptions(id = "4", displayText = "Breed 4"),
                ),
                imageBitmap = ImageBitmap(100, 100),
                errorMsg = "error message",
            ),
            landscape = true,
        )
    }
}