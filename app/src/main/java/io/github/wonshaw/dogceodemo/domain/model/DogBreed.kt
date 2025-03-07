package io.github.wonshaw.dogceodemo.domain.model

data class DogBreed(
    val id: String,
    val breed: String,
    val subBreed: String? = null,
) {
    fun isSubBreed(): Boolean {
        return subBreed != null
    }

    fun getDisplayName(): String {
        return if (isSubBreed()) {
            if (postfixBreeds.contains(breed.lowercase()) || prefixSubBreeds.contains(subBreed!!.lowercase())) {
                "$subBreed $breed"
            } else {
                "$breed $subBreed"
            }
        } else {
            breed
        }
    }
}

private val prefixSubBreeds = setOf(
    "indian", "italian", "german", "french", "english", "scottish",
    "japanese", "swedish", "norwegian", "spanish", "irish", "dutch",
    "russian", "caucasian", "australian", "american", "welsh",

    "toy", "miniature", "standard", "medium", "giant"
)

private val postfixBreeds = setOf(
    "hound", "terrier", "retriever", "mastiff", "sheepdog", "bulldog",
    "pointer", "spaniel", "elkhound", "waterdog", "wolfhound", "ridgeback",
    "deerhound", "setter"
)