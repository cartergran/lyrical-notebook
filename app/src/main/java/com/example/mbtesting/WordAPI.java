package com.example.mbtesting;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface WordAPI {
    @Headers({
            "X-RapidAPI-Key: 009fe0d538mshb9d81b9a2bd5f24p1c549ejsn3fc2fb94bfd2"
    })
    @GET("words/{word}/rhymes")
    Call<JsonRhymes> responseRhymes(@Path("word") String word);
}
