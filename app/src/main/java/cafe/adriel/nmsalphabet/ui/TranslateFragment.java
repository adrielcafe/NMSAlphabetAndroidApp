package cafe.adriel.nmsalphabet.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.event.ImageCroppedEvent;
import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.ui.util.TextViewClickMovement;
import cafe.adriel.nmsalphabet.util.AnalyticsUtil;
import cafe.adriel.nmsalphabet.util.DbUtil;
import cafe.adriel.nmsalphabet.util.LanguageUtil;
import cafe.adriel.nmsalphabet.util.ThemeUtil;
import cafe.adriel.nmsalphabet.util.TranslationUtil;
import cafe.adriel.nmsalphabet.util.Util;
import mehdi.sakout.dynamicbox.DynamicBox;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class TranslateFragment extends BaseFragment {

    private static final int REQUEST_TAKE_PICTURE = 0;

    private String languageCode;
    private AlienRace selectedRace;
    private List<AlienWordTranslation> translations;
    private DynamicBox viewState;

    @BindView(R.id.search_layout)
    RelativeLayout searchLayout;
    @BindView(R.id.search)
    EditText searchView;
    @BindView(R.id.search_clear)
    TextView searchClearView;
    @BindView(R.id.races)
    MaterialSpinner racesView;
    @BindView(R.id.fab)
    FloatingActionButton fabView;
    @BindView(R.id.language)
    MaterialSpinner languageView;
    @BindView(R.id.translation_layout)
    ScrollView translationLayout;
    @BindView(R.id.translation_separator)
    View translationSeparatorView;
    @BindView(R.id.translated_phrase)
    TextView translatedPhraseView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_translate, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLanguage(languageCode);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                cropPicture(imageFile);
            }
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                e.printStackTrace();
            }
        });
    }

    @Subscribe(sticky = true)
    public void onEvent(final ImageCroppedEvent event) {
        EventBus.getDefault().removeStickyEvent(ImageCroppedEvent.class);
        final AlertDialog dialog = Util.showLoadingDialog(getContext());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final String text = TranslationUtil.extractTextFromImage(getContext(), event.image);
                if(Util.isNotEmpty(text)){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            searchView.setText(text);
                            translatePhrase();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), getString(R.string.no_word_found_image), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void init(){
        viewState = TranslationUtil.createViewState(getContext(), translationLayout);
        translatedPhraseView.setMovementMethod(new TextViewClickMovement(getContext(), new TextViewClickMovement.OnTextViewClickMovementListener() {
            @Override
            public void onLinkClicked(String linkText, TextViewClickMovement.LinkType linkType) {
                showWordTranslationsDialog(linkText);
            }
            @Override
            public void onLongClick(String text) {
                AlienWordTranslation translation = getTranslation(text);
                if(translation != null){
                    Toast.makeText(getContext(), translation.getWord().getWord(), Toast.LENGTH_SHORT).show();
                }
            }
        }));
        initControls();
        initFab();
        initLanguage();
    }

    private void initControls(){
        List<String> races = DbUtil.getRacesName();
        races.add(0, getString(R.string.select_alien_race));

        racesView.setBackground(ThemeUtil.getHeaderControlDrawable(getContext()));
        racesView.setBackgroundColor(ThemeUtil.getPrimaryDarkColor(getContext()));
        racesView.setTextColor(Color.WHITE);
        racesView.setArrowColor(Color.WHITE);
        racesView.setItems(races);
        racesView.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                selectedRace = DbUtil.getRaceByName(item);
                translatePhrase();
            }
        });

        searchLayout.setBackground(ThemeUtil.getHeaderControlDrawable(getContext()));
        searchView.setFilters(new InputFilter[] { Util.getTranslationInputFilter() });
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (isPhraseValid()) {
                        translatePhrase();
                    }
                }
                return false;
            }
        });
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                searchClearView.setVisibility(s.length() == 0 ? View.INVISIBLE : View.VISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        searchView.post(new Runnable() {
            @Override
            public void run() {
                racesView.setHeight(searchLayout.getHeight());
            }
        });
        searchClearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                languageView.setVisibility(View.INVISIBLE);
                translationSeparatorView.setVisibility(View.INVISIBLE);
                translationLayout.setVisibility(View.INVISIBLE);
                translatedPhraseView.setText("");
                searchView.setText("");
            }
        });
    }

    private void initFab(){
        Drawable fabIcon = new IconicsDrawable(getContext())
                .icon(MaterialDesignIconic.Icon.gmi_camera)
                .color(Color.WHITE)
                .sizeDp(50);
        fabView.setImageDrawable(fabIcon);
        fabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    private void initLanguage(){
        String[] languages = getContext().getResources().getStringArray(R.array.language_entries);
        int languageIndex = 0;

        if(languageCode == null){
            languageCode = LanguageUtil.getCurrentLanguageCode(getContext());
        }
        for (int i = 0; i < languages.length; i++){
            if(languages[i].equals(LanguageUtil.languageCodeToLanguage(getContext(), languageCode))){
                languageIndex = i;
            }
        }

        languageView.setTextColor(Color.BLACK);
        languageView.setArrowColor(Color.BLACK);
        languageView.setBackground(null);
        languageView.setPadding(20, 20, 20, 20);
        languageView.setItems(languages);
        languageView.setSelectedIndex(languageIndex);
        languageView.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                updateLanguage(item);
                translatePhrase();
            }
        });

        updateLanguage(LanguageUtil.languageCodeToLanguage(getContext(), languageCode));
    }

    private void translatePhrase(){
        final String phrase = Util.removeSpecialCharacters(searchView.getText().toString().trim());
        Util.hideSoftKeyboard(getActivity());
        if(Util.isNotEmpty(phrase) && selectedRace != null) {
            translations = new ArrayList<>();
            translatedPhraseView.setText("");
            viewState.showCustomView(Constant.STATE_LOADING);
            AnalyticsUtil.translateEvent(selectedRace, phrase);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    final String[] words = phrase.split(" ");
                    final Map<String, AlienWordTranslation> translatedWords = DbUtil
                            .translateWords(Arrays.asList(words), selectedRace, languageCode);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateTranslatedPhrase(words, translatedWords);
                        }
                    });
                }
            });
        }
    }

    private void takePicture(){
        EasyImage.openChooserWithGallery(this, getString(R.string.select_an_image), REQUEST_TAKE_PICTURE);
    }

    private void cropPicture(File imageFile){
        Intent i = new Intent(getContext(), CropImageActivity.class);
        i.putExtra(Constant.EXTRA_IMAGE_PATH, imageFile.getPath());
        startActivity(i);
    }

    private void updateLanguage(String language){
        final String english = getContext().getString(R.string.english);
        final String portuguese = getContext().getString(R.string.portuguese);
        final String german = getContext().getString(R.string.german);
        int flagResId = -1;
        if(language.equals(english)){
            languageCode = LanguageUtil.LANGUAGE_EN;
            flagResId = R.drawable.flag_uk_small;
        } else if(language.equals(portuguese)){
            languageCode = LanguageUtil.LANGUAGE_PT;
            flagResId = R.drawable.flag_brazil_small;
        } else if(language.equals(german)){
            languageCode = LanguageUtil.LANGUAGE_DE;
            flagResId = R.drawable.flag_germany_small;
        }
        try {
            languageView.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(flagResId), null, languageView.getCompoundDrawables()[2], null);
        } catch (Exception e){ }
    }

    private void updateTranslatedPhrase(String[] words, Map<String, AlienWordTranslation> translatedWords){
        final StringBuilder translatedPhrase = new StringBuilder();
        if(translatedWords != null){
            for(String word : words){
                if(translatedWords.containsKey(word)) {
                    AlienWordTranslation translation = translatedWords.get(word);
                    translations.add(translation);
                    translatedPhrase.append("<a href='http://nms.ab'>" + translation.getTranslation() + "</a>");
                } else {
                    translatedPhrase.append("<font color='#D32F2F'>" + word + "</font>");
                }
                translatedPhrase.append(" ");
            }
        }

        String translatedPhraseStr = translatedPhrase.toString().trim();
        languageView.setVisibility(View.VISIBLE);
        translationSeparatorView.setVisibility(View.VISIBLE);
        translationLayout.setVisibility(View.VISIBLE);
        translatedPhraseView.setText(Html.fromHtml(translatedPhraseStr));
        if(Util.isNotEmpty(translatedPhraseStr)) {
            viewState.hideAll();
        } else {
            viewState.showCustomView(Constant.STATE_EMPTY);
        }
    }

    private void showWordTranslationsDialog(String translation){
        AlienWordTranslation t = getTranslation(translation);
        if(t != null) {
            TranslationUtil.showTranslationsDialog(getContext(), t.getWord(), languageCode);
        }
    }

    private AlienWordTranslation getTranslation(String translation){
        if(translations != null) {
            for (AlienWordTranslation t : translations) {
                if (t != null && t.getTranslation().equals(translation)) {
                    return t;
                }
            }
        }
        return null;
    }

    private boolean isPhraseValid(){
        if(racesView.getText().toString().equals(getString(R.string.select_alien_race))){
            Toast.makeText(getContext(), R.string.select_alien_race, Toast.LENGTH_SHORT).show();
            return false;
        } else if(Util.isEmpty(searchView.getText().toString().trim())){
            Toast.makeText(getContext(), R.string.type_alien_phrase, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}