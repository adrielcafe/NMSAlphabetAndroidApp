package cafe.adriel.nmsalphabet.ui;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import butterknife.Bind;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.R;

public class TranslateFragment extends BaseFragment {


    @Bind(R.id.phrase_layout)
    RelativeLayout phraseLayout;
    @Bind(R.id.phrase)
    EditText phraseView;
    @Bind(R.id.phrase_clear)
    TextView phraseClearView;
    @Bind(R.id.races)
    MaterialSpinner racesView;
    @Bind(R.id.fab)
    FloatingActionButton fabView;
    @Bind(R.id.translation_header)
    LinearLayout translationHeaderView;
    @Bind(R.id.country_flag)
    ImageView countryFlagView;
    @Bind(R.id.translation_separator)
    View translationSeparatorView;
    @Bind(R.id.translated_phrase)
    EditText translatedPhraseView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_translate, container, false);
        ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    @Override
    protected void init(){
        initFab();
        racesView.setBackgroundResource(R.drawable.home_control);
        racesView.setTextColor(Color.WHITE);
        racesView.setArrowColor(Color.WHITE);
        racesView.setDropdownColor(getResources().getColor(R.color.colorPrimaryDark));
        racesView.setItems(getString(R.string.all_alien_races), "Korvax");
        racesView.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

            }
        });

        phraseView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (phraseView.getText().length() > 0) {
                        translatePhrase(phraseView.getText().toString());
                    }
                }
                return false;
            }
        });
        phraseView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                phraseClearView.setVisibility(s.length() == 0 ? View.INVISIBLE : View.VISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        phraseView.post(new Runnable() {
            @Override
            public void run() {
                racesView.setHeight(phraseLayout.getHeight());
            }
        });
        phraseClearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phraseView.setText("");
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

            }
        });
    }

    private void translatePhrase(String phrase){
        translationHeaderView.setVisibility(View.VISIBLE);
        translationSeparatorView.setVisibility(View.VISIBLE);
        translatedPhraseView.setVisibility(View.VISIBLE);
        translatedPhraseView.setText("HELLO FRIEND");
    }
}