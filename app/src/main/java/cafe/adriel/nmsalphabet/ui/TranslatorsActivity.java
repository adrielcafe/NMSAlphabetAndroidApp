package cafe.adriel.nmsalphabet.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.util.LanguageUtil;
import cafe.adriel.nmsalphabet.util.Triple;
import cafe.adriel.nmsalphabet.util.Util;

public class TranslatorsActivity extends BaseActivity {

    @BindView(R.id.translators_layout)
    LinearLayout translatorsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translators);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.translators);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    protected void init() {
        adjustMarginAndPadding();
        Triple<String, String, String> lastTranslator = null;
        for(Triple<String, String, String> translator : Constant.TRANSLATORS){
            boolean showCountryFlag = true;
            if(lastTranslator != null && lastTranslator.getA().equals(translator.getA())){
                showCountryFlag = false;
            }
            String language = translator.getA();
            String name = translator.getB();
            String twitter = translator.getC();
            View translatorView = setupTranslator(language, name, twitter, showCountryFlag);
            translatorsLayout.addView(translatorView);
            lastTranslator = translator;
        }
    }

    private View setupTranslator(String language, String name, final String twitter, boolean showCountryFlag){
        View rootView = LayoutInflater.from(this).inflate(R.layout.list_item_translator, null, false);
        TranslatorViewHolder holder = new TranslatorViewHolder(rootView);
        holder.translatorNameView.setText(name);
        holder.translatorTwitterView.setText(twitter);

        if(showCountryFlag) {
            switch (language) {
                case LanguageUtil.LANGUAGE_EN:
                    holder.countryFlagView.setImageResource(R.drawable.flag_uk_big);
                    break;
                case LanguageUtil.LANGUAGE_PT:
                    holder.countryFlagView.setImageResource(R.drawable.flag_brazil_big);
                    break;
                case LanguageUtil.LANGUAGE_DE:
                    holder.countryFlagView.setImageResource(R.drawable.flag_germany_big);
                    break;
            }
        }

        if(Util.isNotEmpty(twitter)) {
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "http://twitter.com/" + twitter.replace("@", "");
                    Util.openUrl(TranslatorsActivity.this, url);
                }
            });
        } else {
            rootView.setBackground(null);
            holder.translatorTwitterView.setVisibility(View.GONE);
        }

        return rootView;
    }

    public static class TranslatorViewHolder {
        @BindView(R.id.country_flag)
        public ImageView countryFlagView;
        @BindView(R.id.translator_name)
        public TextView translatorNameView;
        @BindView(R.id.translator_twitter)
        public TextView translatorTwitterView;

        public TranslatorViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}