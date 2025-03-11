# DogCeo Demo

<img src="https://github.com/WonShaw/DogCeoDemo/blob/9096469d844b013ba4586aa58671ed9eb12703d8/arts/portrait.jpeg" width="200" />
<img src="https://github.com/WonShaw/DogCeoDemo/blob/9096469d844b013ba4586aa58671ed9eb12703d8/arts/landscape.jpeg" height="200" />

This is an Android demo app that utilizes the [Dog CEO API](https://dog.ceo/dog-api/documentation/) as the backend.  
The app includes a **Quiz Page**, where a random dog image is displayed along with four possible breed options. Your task is to guess the correct breed.

## Architecture

This demo follows the Android recommended [architecture](https://developer.android.com/topic/architecture).  
The code is structured as follows:

- **`presentation`** - UI Layer
- **`domain`** - Domain Layer
- **`data`** - Data Layer
- **`di`** - Dependency Injection
- **`test`** - Unit & Instrumented Testing

## Tech Stack

- **UI Layer**: Compose, MVVM
- **Asynchronous Programming**: Kotlin Coroutines
- **Image Loading**: Coil
- **Networking**: Retrofit + OkHttp
- **Dependency Injection**: Hilt
- **JSON Serialization**: Moshi
- **Testing**: JUnit, MockK, Turbine, Truth

---

Feel free to explore the code
