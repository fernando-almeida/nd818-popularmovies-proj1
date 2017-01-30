# nd818-popularmovies-proj1
Popular movies (Project 1)

Makes use of TheMovieDB (https://www.themoviedb.org/) API (version 3)


Currently not using
    All DB classes in package ".db"
    Only the sort criteria preference is being used currently

Features list:
    List movie posters sorted by two criteria: top rated and most popular
    Display movie details when poster in the previous list is clicked

Technical aspects:
    Minor support for offline support using HttpResponseCache (max. 10MB storage)
    Automatic pagination of movie list data in recycler view upon scroll with visibility threshold
    Maximum local cache for date to avoid memory exhaustion (currently hard-coded)
    Picasso default image caching (~ 50MB)

Dependencies
    Picasso - Image download/cache manager
    butterknife - View injection/lookup

Configuration
    Replace API_KEY in api/TmdbApiClient with a suitable value and rebuild before using