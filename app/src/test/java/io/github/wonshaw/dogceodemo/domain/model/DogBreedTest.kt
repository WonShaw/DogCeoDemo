package io.github.wonshaw.dogceodemo.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DogBreedTest {
    @Test
    fun `getDisplayName should return breed name when no subBreed`() {
        val dog = DogBreed(id = "", breed = "breed")
        assertThat(dog.getDisplayName()).isEqualTo("breed")
    }

    @Test
    fun `getDisplayName should return subBreed first when represents a breed category`() {
        assertThat(
            DogBreed(
                id = "",
                breed = "bulldog",
                subBreed = "french"
            ).getDisplayName()
        ).isEqualTo("french bulldog")
        assertThat(
            DogBreed(
                id = "",
                breed = "retriever",
                subBreed = "golden"
            ).getDisplayName()
        ).isEqualTo("golden retriever")
    }

    @Test
    fun `getDisplayName should return breed first when breed is in postfix list`() {
        val dog = DogBreed(id = "", breed = "retriever", subBreed = "golden")
        assertThat(dog.getDisplayName()).isEqualTo("golden retriever")
    }

    @Test
    fun `getDisplayName should return subBreed first when subBreed represents a region`() {
        assertThat(
            DogBreed(
                id = "",
                breed = "sheepdog",
                subBreed = "english"
            ).getDisplayName()
        ).isEqualTo("english sheepdog")
        assertThat(
            DogBreed(
                id = "",
                breed = "spitz",
                subBreed = "japanese"
            ).getDisplayName()
        ).isEqualTo("japanese spitz")
    }

    @Test
    fun `getDisplayName should return subBreed first when subBreed represents a size`() {
        assertThat(
            DogBreed(
                id = "",
                breed = "poodle",
                subBreed = "toy"
            ).getDisplayName()
        ).isEqualTo("toy poodle")
        assertThat(
            DogBreed(
                id = "",
                breed = "poodle",
                subBreed = "standard"
            ).getDisplayName()
        ).isEqualTo("standard poodle")
    }

    @Test
    fun `getDisplayName should return breed first when subBreed is not in prefix or breed is not in postfix`() {
        val dog = DogBreed(id = "", breed = "unknownbreed", subBreed = "unknownsub")
        assertThat(dog.getDisplayName()).isEqualTo("unknownbreed unknownsub")
    }

    @Test
    fun `getDisplayName should handle casing correctly`() {
        val dog = DogBreed(id = "", breed = "Retriever", subBreed = "Golden")
        assertThat(dog.getDisplayName()).isEqualTo("Golden Retriever")
    }
}