package com.example.mbtesting;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.drm.DrmStore;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.libRG.CustomTextView;
import com.shawnlin.numberpicker.NumberPicker;

public class SearchActivity extends AppCompatActivity {

    public final static String EXTRA_VALUE = "com.example.mbtesting.EXTRA_VALUE";

    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final CustomTextView customTextView = findViewById(R.id.text_view_searches);
        final EditText editText = findViewById(R.id.edit_text_search);
        final NumberPicker numberPicker = findViewById(R.id.number_picker_searches);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(9);
        numberPicker.setValue(0);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setTextColor(getColor(R.color.colorWhite));
        numberPicker.setSelectedTextColor(getColor(R.color.colorWhite));
        numberPicker.setDividerColor(getColor(R.color.colorWhite));
        numberPicker.setOrientation(LinearLayout.HORIZONTAL);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        //TODO: Look into OnEditorActionListener instead (to cover more instances of KeyEvents)
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                int yOffset = getSupportActionBar().getHeight();
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String search = editText.getText().toString();
                    if (search.isEmpty()) {
                        makeOffsetToast("Please Enter Text", yOffset);
                        return true;
                    }
                    if (customTextView.isChecked()) {
                        LogicAPI.searchSongLyrics(noteViewModel, search, numberPicker.getValue());

                        Intent intent = getIntent();
                        intent.putExtra(EXTRA_VALUE, numberPicker.getValue());
                        setResult(RESULT_OK, intent);
                        finish();
                        return true;
                    } else {
                        makeOffsetToast("Please Confirm Results", yOffset);
                        return true;
                    }
                } else {
                    return false;
                }
            }
        });
    }

    public void makeOffsetToast(String message, int yOffset){
        Toast toast = Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, yOffset);
        toast.show();
    }
}
