package com.example.mbtesting;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

//Note entity on which we want to make database operations with Data Access Object(DAO)

@Entity(tableName = "note_table")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private String description;

    private int priority;

    private String media;

    public Note(String title, String description, int priority, String media) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.media = media;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public String getMedia() {
        return media;
    }
}
