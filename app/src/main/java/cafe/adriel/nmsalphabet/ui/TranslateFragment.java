package cafe.adriel.nmsalphabet.ui;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.ui.util.TextViewClickMovement;
import cafe.adriel.nmsalphabet.util.DbUtil;
import cafe.adriel.nmsalphabet.util.LanguageUtil;
import cafe.adriel.nmsalphabet.util.ThemeUtil;
import cafe.adriel.nmsalphabet.util.TranslationUtil;
import cafe.adriel.nmsalphabet.util.Util;
import mehdi.sakout.dynamicbox.DynamicBox;

public class TranslateFragment extends BaseFragment {

    private AlienRace selectedRace;
    private String languageCode;
    private DynamicBox viewState;
    private List<AlienWordTranslation> translations;

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
    @BindView(R.id.legend)
    TextView legendView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_translate, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        init();
        return rootView;
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
            public void onLongClick(String text) { }
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
                    if (isValid()) {
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
                legendView.setVisibility(View.INVISIBLE);
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
                scanPicture();
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
        final String phrase = searchView.getText().toString().trim();
        translations = new ArrayList<>();
        Util.hideSoftKeyboard(getActivity());
        if(Util.isNotEmpty(phrase) && selectedRace != null) {
            viewState.showCustomView(Constant.STATE_LOADING);
            translatedPhraseView.setText("");
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    String[] words = phrase.split(" ");
                    for(String w : words){
                        AlienWord word = DbUtil.getWord(selectedRace, w);
                        if(word != null){
                            AlienWordTranslation translation = DbUtil.getBestTranslation(selectedRace, word, languageCode);
                            translations.add(translation);
                        }
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(AlienWordTranslation translation : translations){
                                String t = translation == null ? "<font color='#D32F2F'>�</font>" : "<a href='http://nms.ab'>" + translation.getTranslation() + "</a>";
                                translatedPhraseView.setText(String.format("%s %s ", translatedPhraseView.getText().toString(), t));
                            }
                            languageView.setVisibility(View.VISIBLE);
                            translationSeparatorView.setVisibility(View.VISIBLE);
                            legendView.setVisibility(View.VISIBLE);
                            translationLayout.setVisibility(View.VISIBLE);
                            translatedPhraseView.setText(Html.fromHtml(translatedPhraseView.getText().toString().trim()));

                            if(Util.isNotEmpty(translatedPhraseView.getText().toString())){
                                viewState.hideAll();
                            } else {
                                viewState.showCustomView(Constant.STATE_EMPTY);
                            }
                        }
                    });
                }
            });
        }
    }

    // TODO
    private void scanPicture(){
        Log.e("SCAN", "OPEN CAMERA");
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
        languageView.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(flagResId), null, languageView.getCompoundDrawables()[2], null);
    }

    private void showWordTranslationsDialog(String translation){
        for(AlienWordTranslation t : translations){
            if(t != null && t.getTranslation().equals(translation)){
                TranslationUtil.showTranslationsDialog(getContext(), t.getWord(), languageCode);
                break;
            }
        }
    }

    private boolean isValid(){
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