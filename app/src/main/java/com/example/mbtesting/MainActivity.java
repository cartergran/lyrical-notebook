package com.example.mbtesting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final int ADD_NOTE_REQUEST = 1;
    public final int EDIT_NOTE_REQUEST = 2;
    public final int LYRIC_VIDEO_REQUEST = 3;
    public final int SEARCH_MUSIC_REQUEST = 4;
    ArrayList<String> toCopy =  new ArrayList<>();

    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class)
                        .putExtra(YoutubeLyricActivity.EXTRA_SAVE, toCopy);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });

        FloatingActionButton buttonSearch = findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(intent, SEARCH_MUSIC_REQUEST);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                adapter.submitList(notes);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, (note.getPriority() == 0) ? YoutubeLyricActivity.class : AddEditNoteActivity.class)
                        .putExtra(AddEditNoteActivity.EXTRA_ID, note.getId())
                        .putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle())
                        .putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription())
                        .putExtra(AddEditNoteActivity.EXTRA_PRIORITY, note.getPriority())
                        .putExtra(AddEditNoteActivity.EXTRA_MEDIA, note.getMedia())
                        .putExtra(YoutubeLyricActivity.EXTRA_SAVE, toCopy);
                startActivityForResult(intent, (note.getPriority() == 0) ? LYRIC_VIDEO_REQUEST : EDIT_NOTE_REQUEST);
            }
        });
    }

    //TODO: Hard-coded strings --> refactor out into string resource
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK){
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);

            Note note = new Note(title, description, priority, "");
            noteViewModel.insert(note);

            if (data.getIntExtra(AddEditNoteActivity.EXTRA_ACTIVITY, 1) == AddEditNoteActivity.NOTE_UPDATED){
                Toast.makeText(this, "Your Note Was Saved", Toast.LENGTH_SHORT).show();
            } else { Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show(); }
        } else if(requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);

            //Due to a line in AddEditNoteActivity.class, this will never be called. For my OCD
            if (id == -1){
                Toast.makeText(this, "Note can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);

            Note note = new Note(title, description, priority, "");
            note.setId(id);
            noteViewModel.update(note);

            if (data.getIntExtra(AddEditNoteActivity.EXTRA_ACTIVITY, 1) == AddEditNoteActivity.NOTE_UPDATED){
                Toast.makeText(this, "Your Note Was Updated", Toast.LENGTH_SHORT).show();
            } else { Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show(); }
        } else if (requestCode == LYRIC_VIDEO_REQUEST && resultCode == RESULT_OK) {
            Toast.makeText(this, "Keep Searching", Toast.LENGTH_SHORT).show();
            toCopy = data.getStringArrayListExtra(YoutubeLyricActivity.EXTRA_SAVE);
        } else if (requestCode == SEARCH_MUSIC_REQUEST && resultCode == RESULT_OK) {
            Toast.makeText(this, data.getIntExtra(SearchActivity.EXTRA_VALUE, 0) + " Results", Toast.LENGTH_SHORT).show();
        } else if (requestCode == SEARCH_MUSIC_REQUEST) {
            //do nothing :)
        } else {
            Toast.makeText(this, "Note Not Saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_notes:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setCancelable(true)
                        .setTitle("Warning")
                        .setMessage("Are you sure you want to delete all notes?");

                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        noteViewModel.deleteAllNotes();
                        Toast.makeText(MainActivity.this, "All Notes Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
                return true;
            case R.id.delete_inspo_notes:
                noteViewModel.deleteInspoNotes();
                return true;
            case R.id.delete_inspo_cart:
                toCopy = new ArrayList<String>();
                Toast.makeText(MainActivity.this, getString(R.string.empty_cart), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


