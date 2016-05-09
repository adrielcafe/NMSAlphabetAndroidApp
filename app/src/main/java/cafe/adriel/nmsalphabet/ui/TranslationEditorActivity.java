package cafe.adriel.nmsalphabet.ui;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.event.EditTranslationEvent;
import cafe.adriel.nmsalphabet.event.TranslationUpdatedEvent;
import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.util.DbUtil;
import cafe.adriel.nmsalphabet.util.LanguageUtil;
import cafe.adriel.nmsalphabet.util.ThemeUtil;
import cafe.adriel.nmsalphabet.util.Util;

public class TranslationEditorActivity extends BaseActivity {

    private AlienRace alienRace;
    private AlienWord alienWord;
    private AlienWordTranslation enTranslation;
    private AlienWordTranslation ptTranslation;
    private AlienWordTranslation deTranslation;

    @BindView(R.id.content_layout)
    FrameLayout contentLayout;
    @BindView(R.id.races)
    MaterialSpinner alienRacesView;
    @BindView(R.id.alien_word)
    EditText alienWordView;
    @BindView(R.id.english_translation)
    EditText enTranslationView;
    @BindView(R.id.portuguese_translation)
    EditText ptTranslationView;
    @BindView(R.id.german_translation)
    EditText deTranslationView;
    @BindView(R.id.fab)
    FloatingActionButton fabView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation_editor);
        ButterKnife.bind(this);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_close)
                .color(Color.WHITE)
                .sizeDp(16));
        setTitle(R.string.new_translation);
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_translation, menu);
        MenuItem saveMenuItem = menu.findItem(R.id.save);
        saveMenuItem.setIcon(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_check)
                .color(Color.WHITE)
                .sizeDp(20));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.save:
                saveTranslation();
                break;
        }
        return true;
    }

    @Subscribe(sticky = true)
    public void onEvent(EditTranslationEvent event) {
        EventBus.getDefault().removeStickyEvent(EditTranslationEvent.class);
        if (event.word != null) {
            alienWord = event.word;
            alienRace = DbUtil.getRaceById(alienWord.getRace().getObjectId());

            for (AlienWordTranslation translation : event.translations) {
                if (translation.getLanguage().equals(LanguageUtil.LANGUAGE_EN)) {
                    enTranslation = translation;
                } else if (translation.getLanguage().equals(LanguageUtil.LANGUAGE_PT)) {
                    ptTranslation = translation;
                } else if (translation.getLanguage().equals(LanguageUtil.LANGUAGE_DE)) {
                    deTranslation = translation;
                }
            }

            editMode();
        }
    }

    @Override
    protected void init() {
        adjustMarginAndPadding();
        initForm();
        initFab();
    }

    private void initForm(){
        List<String> races = DbUtil.getRacesName();
        races.add(0, getString(R.string.select_alien_race));

        alienRacesView.setBackground(ThemeUtil.getHeaderControlDrawable(this));
        alienRacesView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        alienRacesView.setTextColor(Color.WHITE);
        alienRacesView.setArrowColor(Color.WHITE);
        alienRacesView.setDropdownColor(ThemeUtil.getPrimaryDarkColor(this));
        alienRacesView.setItems(races);
        alienRacesView.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

            }
        });

        alienWordView.post(new Runnable() {
            @Override
            public void run() {
                alienRacesView.setHeight(alienWordView.getHeight());
            }
        });

        alienWordView.setBackground(ThemeUtil.getHeaderControlDrawable(this));
        enTranslationView.setBackground(ThemeUtil.getHeaderControlDrawable(this));
        ptTranslationView.setBackground(ThemeUtil.getHeaderControlDrawable(this));
        deTranslationView.setBackground(ThemeUtil.getHeaderControlDrawable(this));

        alienWordView.setFilters(new InputFilter[] { Util.getWordInputFilter() });
        enTranslationView.setFilters(new InputFilter[] { Util.getTranslationInputFilter() });
        ptTranslationView.setFilters(new InputFilter[] { Util.getTranslationInputFilter() });
        deTranslationView.setFilters(new InputFilter[] { Util.getTranslationInputFilter() });
    }

    private void initFab(){
        Drawable fabIcon = new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_camera)
                .color(Color.WHITE)
                .sizeDp(50);
        fabView.setImageDrawable(fabIcon);
        fabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void editMode(){
        setTitle(R.string.edit_translation);

        // TODO Disable spinner
//        alienRacesView.setEnabled(false);
        alienWordView.setEnabled(false);

        alienRacesView.setSelectedIndex(DbUtil.getRacePosition(alienRace.getObjectId()) + 1);
        alienWordView.setText(alienWord.getWord());
        if(enTranslation != null && Util.isNotEmpty(enTranslation.getTranslation())) {
            enTranslationView.setText(enTranslation.getTranslation());
        }
        if(ptTranslation != null && Util.isNotEmpty(ptTranslation.getTranslation())) {
            ptTranslationView.setText(ptTranslation.getTranslation());
        }
        if(deTranslation != null && Util.isNotEmpty(deTranslation.getTranslation())) {
            deTranslationView.setText(deTranslation.getTranslation());
        }
    }

    private void saveTranslation() {
        if (isValid() && Util.isConnected(this)) {
            final AlertDialog dialog = Util.showLoadingDialog(this);
            Util.hideSoftKeyboard(this);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    List<AlienWordTranslation> translations = new ArrayList<>();
                    String alienRaceStr = alienRacesView.getText().toString().toUpperCase();
                    String alienWordStr = alienWordView.getText().toString().toUpperCase();
                    String enTranslationStr = enTranslationView.getText().toString().toUpperCase();
                    String ptTranslationStr = ptTranslationView.getText().toString().toUpperCase();
                    String deTranslationStr = deTranslationView.getText().toString().toUpperCase();

                    try {
                        alienRace = DbUtil.getRaceByName(alienRaceStr);

                        if(alienWord == null) {
                            alienWord = DbUtil.getWord(alienRace, alienWordStr);
                            if (alienWord == null) {
                                alienWord = new AlienWord();
                                alienWord.setRace(alienRace);
                                alienWord.setWord(alienWordStr);
                                alienWord.save();
                            }
                        }
                        alienWord.addUser(App.getUser());
                        alienWord.save();

                        if (Util.isNotEmpty(enTranslationStr)) {
                            AlienWordTranslation translation = addTranslation(enTranslationStr, LanguageUtil.LANGUAGE_EN, alienWord, alienRace);
                            translations.add(translation);
                        }
                        if (Util.isNotEmpty(ptTranslationStr)) {
                            AlienWordTranslation translation = addTranslation(ptTranslationStr, LanguageUtil.LANGUAGE_PT, alienWord, alienRace);
                            translations.add(translation);
                        }
                        if (Util.isNotEmpty(deTranslationStr)) {
                            AlienWordTranslation translation = addTranslation(deTranslationStr, LanguageUtil.LANGUAGE_DE, alienWord, alienRace);
                            translations.add(translation);
                        }

                        EventBus.getDefault().postSticky(new TranslationUpdatedEvent(alienWord, translations));

                        dialog.dismiss();
                        finish();
                    } catch (final Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TranslationEditorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    private AlienWordTranslation addTranslation(final String translationStr, final String language, final AlienWord word, final AlienRace race){
        final AlienWordTranslation currentTranslation = DbUtil.getUserTranslation(App.getUser(), language, word, race);
        AlienWordTranslation translation = DbUtil.getTranslation(translationStr, language, word, race);
        if (translation == null) {
            try {
                translation = new AlienWordTranslation();
                translation.setTranslation(translationStr);
                translation.setLanguage(language);
                translation.setWord(word);
                translation.setRace(race);
                translation.save();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        if(translation != null) {
            try {
                translation.addUser(App.getUser());
                translation.save();
                if (currentTranslation != null && !currentTranslation.getTranslation().equals(translationStr)) {
                    try {
                        currentTranslation.removeUser(App.getUser());
                        currentTranslation.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return translation;
    }

    private boolean isValid(){
        if(alienRacesView.getText().toString().equals(getString(R.string.select_alien_race))){
            Toast.makeText(this, R.string.select_alien_race, Toast.LENGTH_SHORT).show();
            return false;
        } else if(Util.isEmpty(alienWordView.getText().toString())){
            Toast.makeText(this, R.string.type_alien_word, Toast.LENGTH_SHORT).show();
            return false;
        } else if(Util.isEmpty(enTranslationView.getText().toString())
                && Util.isEmpty(ptTranslationView.getText().toString())
                && Util.isEmpty(deTranslationView.getText().toString())){
            Toast.makeText(this, R.string.translate_at_least_in_one_language, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}