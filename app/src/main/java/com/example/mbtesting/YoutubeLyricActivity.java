package com.example.mbtesting;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;

public class YoutubeLyricActivity extends YouTubeBaseActivity {

    final static String API_KEY = "AIzaSyCfAD617gEUchQtOpBcAK14epyj49zr68Q";
    final static String EXTRA_SAVE = "com.example.mbtesting.EXTRA_SAVE";
    final static int KEY_LENGTH = 11;
    int count = 0;
    static Layout tvLayout;
    static boolean needSpace = false;
    static String lastTitle = "Magic Title";

    private TextView textViewTitle;
    private TextView textViewArtist;
    private TextView textViewLyrics;
    private TextView textViewSaved;
    private ScrollView scrollViewLyrics;
    private ScrollView scrollViewSaved;

    private int songTitleLength;
    private String songTitleTrim;
    int toSaveStart;
    int toSaveEnd;
    String toSave;
    String toSaveStanza;
    ArrayList<String> saved = new ArrayList<>();
    ArrayList<Integer> lines = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_lyric);

        Intent intent = getIntent();
        saved = intent.getStringArrayListExtra(EXTRA_SAVE);

        final BackgroundColorSpan bcsNormal = new BackgroundColorSpan(getColor(R.color.colorNote));

        scrollViewLyrics = findViewById(R.id.scroll_view_lyrics);
        scrollViewSaved = findViewById(R.id.scroll_view_saved);
        textViewTitle = findViewById(R.id.text_view_title);
        textViewArtist = findViewById(R.id.text_view_artist);
        textViewLyrics = findViewById(R.id.text_view_lyrics);
        textViewSaved = findViewById(R.id.text_view_saved);
        textViewLyrics.setMovementMethod(LinkMovementMethod.getInstance());
        populate(textViewSaved, saved);
        YouTubePlayerView youtubePlayerView = findViewById(R.id.player);

        String needle = "by";
        String toSplit = intent.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
        textViewTitle.setText(toSplit.substring(2, toSplit.indexOf(needle)));
        textViewArtist.setText(toSplit.substring(toSplit.indexOf(needle)));
        songTitleTrim = textViewTitle.getText().toString().trim();
        songTitleLength = textViewTitle.getText().length() + 2;

        String lyrics = intent.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
        final Spannable spanText = Spannable.Factory.getInstance().newSpannable(lyrics);
        spanText.setSpan(bcsNormal, 0, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewLyrics.setText(spanText);

        final GestureDetector myLongHoldListener = new GestureDetector(this, new LongHoldListener());

        ViewTreeObserver vto = textViewLyrics.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tvLayout = textViewLyrics.getLayout();
            }
        });

        final BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_lyrics);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_copy:
                        if (toSave == null) {
                            makeOffsetToast(getString(R.string.need_highlight), bottomNavigationView.getHeight());
                        } else {
                            if (toSaveStart == toSaveEnd) {
                                if (toSave.contains(getString(R.string.title_tag))) {
                                    int titleIndex = toSave.indexOf(getString(R.string.title_tag));
                                    if (count == 0) {
                                        count++;
                                        toSave = lastTitle.equals(songTitleTrim) ? toSave = toSave.substring(songTitleLength) : toSave;
                                        saved.add((textViewSaved.getText().length() == 0) ? toSave.trim() + '\n' : '\n' + toSave.trim() + '\n');
                                        textViewSaved.append((textViewSaved.getText().length() == 0) ? toSave.trim() + '\n' : '\n' + toSave.trim() + '\n');
                                        makeOffsetToast(getString(R.string.copied), bottomNavigationView.getHeight());

                                        //Occurs during the second call of "copy" to the same line or copying a new line in the same song. Sets static toSave without title.
                                    } else if (toSave.substring(titleIndex + 1, titleIndex + songTitleTrim.length() + 1).equals(songTitleTrim)) {
                                        toSave = toSave.substring(songTitleLength);
                                        saved.add((needSpace) ? '\n' + toSave.trim() + '\n' : toSave.trim() + '\n');
                                        textViewSaved.append((needSpace) ? '\n' + toSave.trim() + '\n' : toSave.trim() + '\n');
                                        needSpace = false;
                                        makeOffsetToast(getString(R.string.copied), bottomNavigationView.getHeight());
                                    }

                                    //Occurs only during the third+ call of "copy" to the same line.
                                } else {
                                    saved.add(toSave.trim() + '\n');
                                    textViewSaved.append(toSave.trim() + '\n');
                                    makeOffsetToast(getString(R.string.copied), bottomNavigationView.getHeight());
                                }
                            } else {
                                if (toSaveStanza.contains(getString(R.string.title_tag))) {
                                    int titleIndex = toSaveStanza.indexOf(getString(R.string.title_tag));
                                    if (count == 0) {
                                        count++;
                                        toSaveStanza = lastTitle.equals(songTitleTrim) ? toSaveStanza = toSaveStanza.substring(songTitleLength) : toSaveStanza;
                                        saved.add((textViewSaved.getText().length() == 0) ? toSaveStanza.trim() + '\n' : '\n' + toSaveStanza.trim() + '\n');
                                        textViewSaved.append((textViewSaved.getText().length() == 0) ? toSaveStanza.trim() + '\n' : '\n' + toSaveStanza.trim() + '\n');
                                        needSpace = true;
                                        makeOffsetToast(getString(R.string.copied), bottomNavigationView.getHeight());
                                    } else if (toSaveStanza.substring(titleIndex + 1, titleIndex + songTitleTrim.length() + 1).equals(songTitleTrim)) {
                                        toSaveStanza = toSaveStanza.substring(songTitleLength);
                                        saved.add('\n' + toSaveStanza.trim() + '\n');
                                        textViewSaved.append('\n' + toSaveStanza.trim() + '\n');
                                        needSpace = true;
                                        makeOffsetToast(getString(R.string.copied), bottomNavigationView.getHeight());
                                    }
                                } else {
                                    saved.add('\n' + toSaveStanza.trim() + '\n');
                                    textViewSaved.append('\n' + toSaveStanza.trim() + '\n');
                                    needSpace = true;
                                    makeOffsetToast(getString(R.string.copied), bottomNavigationView.getHeight());
                                }
                            }
                        }
                        lastTitle = songTitleTrim;
                        return false;

                    case R.id.navigation_saved:
                        scrollViewLyrics.setVisibility(View.GONE);
                        scrollViewSaved.setVisibility(View.VISIBLE);
                        break;

                    case R.id.navigation_lyrics:
                        scrollViewLyrics.setVisibility(View.VISIBLE);
                        scrollViewSaved.setVisibility(View.GONE);
                        break;
                }
                return true;
            }
        });

        textViewLyrics.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                myLongHoldListener.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Layout layout = ((TextView) view).getLayout();
                    int y = (int) event.getY();
                    if (layout != null) {
                        int line = layout.getLineForVertical(y);
                        try {
                            int startA = tvLayout.getLineStart(0);
                            int endA = tvLayout.getLineEnd(line - 1);
                            spanText.setSpan(new BackgroundColorSpan(getColor(R.color.colorNote)), startA, endA, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            int start = tvLayout.getLineStart(line);
                            int end = tvLayout.getLineEnd(line);
                            toSave = getString(R.string.title_tag) + songTitleTrim + getString(R.string.title_tag) + "\n" + textViewLyrics.getText().subSequence(start, end).toString().trim();
                            toSaveStart = line;
                            toSaveEnd = line;
                            spanText.setSpan(new BackgroundColorSpan(getColor(R.color.colorRed)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            int startB = tvLayout.getLineStart(line + 1);
                            int endB = tvLayout.getLineEnd(layout.getLineCount() - 1);
                            spanText.setSpan(new BackgroundColorSpan(getColor(R.color.colorNote)), startB, endB, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            textViewLyrics.setText(spanText);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (LongHoldListener.isLongPress) {
                    lines = new ArrayList<Integer>();
                    textViewLyrics.getParent().requestDisallowInterceptTouchEvent(true);
                    int historySize = event.getHistorySize();
                    Layout layout = ((TextView) view).getLayout();
                    if (layout != null) {
                        for (int i = 0; i < historySize; i++) {
                            int y = (int) event.getHistoricalY(i);
                            int line = layout.getLineForVertical(y);
                            lines.add(line);
                            try {
                                int start = tvLayout.getLineStart(lines.get(0));
                                int end = tvLayout.getLineEnd(lines.get(lines.size() - 1));

                                spanText.setSpan(new BackgroundColorSpan(getColor(R.color.colorRed)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                toSaveEnd = line;
                                int startIndex = tvLayout.getLineStart(toSaveStart);
                                int endIndex = tvLayout.getLineEnd(toSaveEnd);
                                toSaveStanza = getString(R.string.title_tag) + songTitleTrim + getString(R.string.title_tag) + '\n' + spanText.subSequence(startIndex, endIndex).toString().trim();

                                textViewLyrics.setText(spanText);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //do nothing
                }
                return false;
            }
        });

        final String VIDEO_KEY = mediaParser(intent.getStringExtra(AddEditNoteActivity.EXTRA_MEDIA));
        if (VIDEO_KEY.equals("")) {
            youtubePlayerView.setVisibility(View.GONE);
        }

        youtubePlayerView.initialize(API_KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.cueVideo(VIDEO_KEY);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                        System.out.println(youTubeInitializationResult);
                    }
                });
    }

    public String mediaParser(String media) {
        if (media.contains(getString(R.string.video_key))) {
            int findKey = media.indexOf(getString(R.string.video_key));
            int start = findKey + getString(R.string.video_key).length();
            int end = start + KEY_LENGTH;
            return media.substring(start, end);
        }
        return "";
    }

    public void populate(TextView textView, ArrayList<String> arrayList) {
        for (String s : arrayList) {
            textView.append(s);
        }
    }

    public void makeOffsetToast(String message, int yOffset){
        Toast toast = Toast.makeText(YoutubeLyricActivity.this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, yOffset);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent().putStringArrayListExtra(EXTRA_SAVE, saved);
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }
}

