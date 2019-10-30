package com.example.mbtesting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.libRG.CustomTextView;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.List;

public class AddEditNoteActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.example.mbtesting.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.mbtesting.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.mbtesting.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY = "com.example.mbtesting.EXTRA_PRIORITY";
    public static final String EXTRA_ACTIVITY = "com.example.mbtesting.EXTRA_ACTIVITY";
    public static final String EXTRA_MEDIA = "com.example.mbtesting.EXTRA_MEDIA";

    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;
    public static final int NOTE_UPDATED = 23;

    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextSearch;
    private NumberPicker numberPickerPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Intent intent = getIntent();

        ((TextView)findViewById(R.id.saved_lyrics).findViewById(R.id.title)).setText(getString(R.string.saved_lyrics));
        ((TextView)findViewById(R.id.rhymed_words).findViewById(R.id.title)).setText(getString(R.string.words_that_rhyme));
        ExpandableTextView expTextViewSaved = findViewById(R.id.saved_lyrics).findViewById(R.id.expand_text_view);
        expTextViewSaved.setText(catenateString(intent.getStringArrayListExtra(YoutubeLyricActivity.EXTRA_SAVE)));

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextSearch = findViewById(R.id.edit_text_search);
        numberPickerPriority = findViewById(R.id.number_picker_priority);

        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);
        numberPickerPriority.setValue(5);
        numberPickerPriority.setSelectedTextColor(getColor(R.color.colorRed));
        numberPickerPriority.setTextColor(getColor(R.color.colorWhite));
        numberPickerPriority.setDividerColor(getColor(R.color.colorWhite));
        numberPickerPriority.setWrapSelectorWheel(false);

        editTextSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String word = editTextSearch.getText().toString();
                    ExpandableTextView expTextViewRhymes = findViewById(R.id.rhymed_words).findViewById(R.id.expand_text_view);
                    LogicAPI.searchRhymeWords(word, expTextViewRhymes);
                    hideKeyboard(AddEditNoteActivity.this);
                }
                return true;
            }
        });

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        if (intent.hasExtra(EXTRA_ID)) {
            setTitle(getString(R.string.edit_note));
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            numberPickerPriority.setValue(intent.getIntExtra(EXTRA_PRIORITY, 1));
            final int id = intent.getIntExtra(EXTRA_ID, -1);

            FloatingActionButton buttonSearch = findViewById(R.id.button_search);
            buttonSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = editTextTitle.getText().toString();
                    String description = editTextDescription.getText().toString();
                    int priority = numberPickerPriority.getValue();

                    Intent intent = new Intent(AddEditNoteActivity.this, SearchActivity.class)
                            .putExtra(EXTRA_TITLE, title)
                            .putExtra(EXTRA_DESCRIPTION, description)
                            .putExtra(EXTRA_PRIORITY, priority)
                            .putExtra(EXTRA_ID, id);
                    startActivityForResult(intent, EDIT_NOTE_REQUEST);
                }
            });
        } else {
            setTitle(getString(R.string.add_note));
            FloatingActionButton buttonSearch = findViewById(R.id.button_search);
            buttonSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = editTextTitle.getText().toString();
                    String description = editTextDescription.getText().toString();
                    int priority = numberPickerPriority.getValue();

                    if (title.trim().isEmpty() || description.trim().isEmpty()) {
                        Toast.makeText(AddEditNoteActivity.this, "Please insert a title and description", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(AddEditNoteActivity.this, SearchActivity.class)
                            .putExtra(EXTRA_TITLE, title)
                            .putExtra(EXTRA_DESCRIPTION, description)
                            .putExtra(EXTRA_PRIORITY, priority);
                    startActivityForResult(intent, ADD_NOTE_REQUEST);
                }
            });
        }
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        int priority = numberPickerPriority.getValue();

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent()
                .putExtra(EXTRA_TITLE, title)
                .putExtra(EXTRA_DESCRIPTION, description)
                .putExtra(EXTRA_PRIORITY, priority);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);

        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().getIntExtra(EXTRA_PRIORITY, -1) != 0) {
            getMenuInflater().inflate(R.menu.add_note_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            Intent intent = data;
            data.putExtra(EXTRA_ACTIVITY, NOTE_UPDATED);
            setResult(RESULT_OK, intent);
            finish();
        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
            Intent intent = data;
            data.putExtra(EXTRA_ACTIVITY, NOTE_UPDATED);
            setResult(RESULT_OK, intent);
            finish();
        } else if (requestCode == RESULT_OK) {
            // do nothing.
        } else {
        }
    }

    public String catenateString(ArrayList<String> list){
        StringBuilder sb = new StringBuilder();
        for (String s : list){
            sb.append(s);
        }
        return sb.toString();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
