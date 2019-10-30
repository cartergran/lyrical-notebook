package com.example.mbtesting;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface DatabaseAPI {
    //In general, don't commit API keys to version control
    @Headers({
            "X-RapidAPI-Key: 009fe0d538mshb9d81b9a2bd5f24p1c549ejsn3fc2fb94bfd2"
    })
    @GET("findLyrics/")
    Call<JsonTypeResult> responseResult(@Query(value = "q", encoded = true) String search);
}
