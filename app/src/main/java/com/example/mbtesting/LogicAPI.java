package com.example.mbtesting;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LogicAPI {

    final static int unicode = 0x1F64B;

    //TODO: Separation of Concerns. Maybe implement Listener<SongLyric> for NoteViewModel
    public static void searchSongLyrics(final NoteViewModel noteViewModel, String search, final int numOfResults) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://audd.p.rapidapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DatabaseAPI dataBaseAPI = retrofit.create(DatabaseAPI.class);

        Call<JsonTypeResult> call = dataBaseAPI.responseResult(search);

        call.enqueue(new Callback<JsonTypeResult>() {
            @Override
            public void onResponse(Call<JsonTypeResult> call, Response<JsonTypeResult> response) {
                if (!response.isSuccessful()) {
                    Integer content = response.code();
                    Note error = new Note("Code:", content.toString(), 1, "");
                    noteViewModel.insert(error);
                    return;
                }
                JsonTypeResult jsonTypeResult = response.body();
                Result[] result = jsonTypeResult.getResult();

                noteViewModel.deleteInspoNotes();
                for (int x = 0; x < result.length; x++) {
                    //Breaks for loop when desired number of notes has been hit
                    if (x == numOfResults) {
                        break;
                    }
                    Note note = new Note(getEmojiByUnicode(unicode) + result[x].getTitle() + " by " + result[x].getArtist(), result[x].getLyrics(), 0, result[x].getMedia());
                    noteViewModel.insert(note);
                }
            }

            @Override
            public void onFailure(Call<JsonTypeResult> call, Throwable t) {
                Note error = new Note("onFailure:", t.getMessage(), 1, "");
                noteViewModel.insert(error);
            }
        });
    }

    public static void searchRhymeWords(String word, final ExpandableTextView expandableTextView) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://wordsapiv1.p.rapidapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WordAPI wordAPI = retrofit.create(WordAPI.class);

        Call<JsonRhymes> call = wordAPI.responseRhymes(word);

        call.enqueue(new Callback<JsonRhymes>() {
            @Override
            public void onResponse(Call<JsonRhymes> call, Response<JsonRhymes> response) {
                if (!response.isSuccessful()) {
                    System.out.println(response.code());
                }
                JsonRhymes rhymes = response.body();
                JsonObject jsonObject = rhymes.getRhymes();
                JsonArray jsonArray = jsonObject.get("all").getAsJsonArray();
                StringBuilder sb = new StringBuilder();
                for (int n = 0; n < jsonArray.size(); n++){
                    sb.append(jsonArray.get(n).getAsString() + '\n');
                }
                expandableTextView.setText(sb);
            }

            @Override
            public void onFailure(Call<JsonRhymes> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    public static String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}
