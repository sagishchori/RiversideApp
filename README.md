**I used the following 3rd party libraries:**

1. **Retrofit** - to handle all network calls. Basically you dont need a cannon to kill a musquito so a simple network module with only GET can be implemented.
2. **LiveData** - to use ViewModels and live updates on data changes. There can be a use of databinding as well with the XML but since it is a simple app I did not think that would make any difference.
3. **RecyclerView** - as requested in the assignment.
4. **Glide** - for image loading and caching management. The is also Coil that supports Kotlin and utilizes the extention property but I think it is still new to the market.
5. **Room** - for caching network results and also to save more data e.g. user's favoorites. It is a wrapper for SQLite and it is more developer friendly than using sqlite directly.
6. **Hilt** - for dependency injection, remove a lot of boilerplate code and to benefit from the compile-time correctness, runtime performance and scalability.


Minimum Android - 24
