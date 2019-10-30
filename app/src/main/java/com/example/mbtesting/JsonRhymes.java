package com.example.mbtesting;

import com.google.gson.JsonObject;

public class JsonRhymes {
    private String word;
    private JsonObject rhymes;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public JsonObject getRhymes() {
        return rhymes;
    }

    public void setRhymes(JsonObject rhymes) {
        this.rhymes = rhymes;
    }
}
