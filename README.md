# Popular Movies
This repository contains the code for the second and third projects of the [Android Developer Nanodegree](https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801) from Udacity.

The application requires an API key which you can get from [The Movie Database Website](https://www.themoviedb.org).

Then, add your API key in the file gradle.properties as follows: api_key="your_api_key"

## Features
- Sort the list of movies by popular, top rated or favorites
- Favorites are defined by the user, and the data is persisted locally (so it can be accessed offline)
- Movie details shows:
  - Backdrop image
  - Release date
  - User rating
  - Plot synopsis
  - List of trailers 
  - List of reviews
- Click on a trailer opens the video in Youtube app, if installed, or in browser 
- Support for orientation change

## [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/)
- Room
- LiveData
- ViewModel
- Repository (Although not part of the Android Architecture Components, it was added as a best practice)

## Other libraries and patterns
- [Retrofit](https://github.com/square/retrofit)
- [Gson](https://github.com/google/gson)
- [RecyclerView](https://developer.android.com/reference/android/support/v7/widget/RecyclerView)
- [Butter Knife](https://github.com/JakeWharton/butterknife)
- [Picasso](https://github.com/square/picasso)
