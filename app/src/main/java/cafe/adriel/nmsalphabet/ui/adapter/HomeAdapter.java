package cafe.adriel.nmsalphabet.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.ramotion.foldingcell.FoldingCell;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.event.AddTranslationEvent;
import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.ui.MainActivity;
import cafe.adriel.nmsalphabet.ui.TranslationEditorActivity;
import cafe.adriel.nmsalphabet.util.DbUtil;
import cafe.adriel.nmsalphabet.util.LanguageUtil;
import cafe.adriel.nmsalphabet.util.ThemeUtil;
import cafe.adriel.nmsalphabet.util.TranslationUtil;
import cafe.adriel.nmsalphabet.util.Util;
import mehdi.sakout.dynamicbox.DynamicBox;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Activity context;
    private String languageCode;
    private List<AlienWord> words;
    private Map<String, DynamicBox> viewStates;
    private Map<String, List<AlienWordTranslation>> enWordTranslations;
    private Map<String, List<AlienWordTranslation>> ptWordTranslations;
    private Map<String, List<AlienWordTranslation>> deWordTranslations;

    public HomeAdapter(Activity context, List<AlienWord> words) {
        this.context = context;
        this.words = words;
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_home, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AlienWord word = words.get(position);
        final AlienRace race = DbUtil.getRaceById(word.getRace().getObjectId());

        if(enWordTranslations == null){
            enWordTranslations = new HashMap<>();
        }
        if(ptWordTranslations == null){
            ptWordTranslations = new HashMap<>();
        }
        if(deWordTranslations == null){
            deWordTranslations = new HashMap<>();
        }
        if(viewStates == null){
            viewStates = new HashMap<>();
        }
        if(!viewStates.containsKey(word.getObjectId())) {
            viewStates.put(word.getObjectId(), TranslationUtil.createViewState(context, holder.translationsLayout));
        }

        holder.cardLayout.initialize(1000, context.getResources().getColor(R.color.gray), 0);
        holder.cardLayout.fold(true);
        holder.alienRaceTitleView.setBackground(ThemeUtil.getWordRaceTitleDrawable(context));
        holder.alienWordTitleView.setText(word.getWord());
        holder.alienWordView.setText(word.getWord());
        if(race != null) {
            holder.alienRaceTitleView.setText(race.getName());
            holder.alienRaceView.setText(race.getName());
        }
        holder.titleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTranslations(holder, race, word);
                holder.cardLayout.unfold(false);
            }
        });
        holder.newTranslationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTranslation(word);
            }
        });
        holder.seeAllTranslationsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeAllTranslations(word);
            }
        });
        holder.shareTranslationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTranslation(race, word);
            }
        });

        initLanguage(holder, race, word);
    }

    private void initLanguage(final ViewHolder holder, final AlienRace race, final AlienWord word){
        String[] languages = context.getResources().getStringArray(R.array.language_entries);
        int languageIndex = 0;

        if(languageCode == null){
            languageCode = LanguageUtil.getCurrentLanguageCode(context);
        }
        for (int i = 0; i < languages.length; i++){
            if(languages[i].equals(LanguageUtil.languageCodeToLanguage(context, languageCode))){
                languageIndex = i;
            }
        }

        holder.languageView.setTextColor(Color.BLACK);
        holder.languageView.setArrowColor(Color.BLACK);
        holder.languageView.setBackground(null);
        holder.languageView.setPadding(10, 10, 10, 10);
        holder.languageView.setItems(languages);
        holder.languageView.setSelectedIndex(languageIndex);
        holder.languageView.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                updateLanguage(holder, item);
                loadTranslations(holder, race, word);
            }
        });

        updateLanguage(holder, LanguageUtil.languageCodeToLanguage(context, languageCode));
    }

    private DynamicBox getViewState(AlienWord word){
        if(viewStates.containsKey(word.getObjectId())){
            return viewStates.get(word.getObjectId());
        } else {
            return null;
        }
    }

    private void setViewState(AlienWord word, String state){
        DynamicBox viewState = getViewState(word);
        if(viewState != null){
            if(Util.isEmpty(state)){
                viewState.hideAll();
            } else {
                viewState.showCustomView(state);
            }
        }
    }

    private void loadTranslations(final ViewHolder holder, AlienRace race, final AlienWord word){
        switch (languageCode){
            case LanguageUtil.LANGUAGE_EN:
                if(enWordTranslations.containsKey(word.getObjectId())){
                    addTranslations(holder, word, enWordTranslations.get(word.getObjectId()));
                    return;
                }
                break;
            case LanguageUtil.LANGUAGE_PT:
                if(ptWordTranslations.containsKey(word.getObjectId())){
                    addTranslations(holder, word, ptWordTranslations.get(word.getObjectId()));
                    return;
                }
                break;
            case LanguageUtil.LANGUAGE_DE:
                if(deWordTranslations.containsKey(word.getObjectId())){
                    addTranslations(holder, word, deWordTranslations.get(word.getObjectId()));
                    return;
                }
                break;
        }
        if(Util.isConnected(context)) {
            setViewState(word, Constant.STATE_LOADING);
            DbUtil.getBestTranslations(race, word, languageCode, new FindCallback<AlienWordTranslation>() {
                @Override
                public void done(List<AlienWordTranslation> translations, ParseException e) {
                    if (Util.isNotEmpty(translations)) {
                        switch (languageCode){
                            case LanguageUtil.LANGUAGE_EN:
                                enWordTranslations.put(word.getObjectId(), translations);
                                break;
                            case LanguageUtil.LANGUAGE_PT:
                                ptWordTranslations.put(word.getObjectId(), translations);
                                break;
                            case LanguageUtil.LANGUAGE_DE:
                                deWordTranslations.put(word.getObjectId(), translations);
                                break;
                        }
                        addTranslations(holder, word, translations);
                    } else {
                        setViewState(word, Constant.STATE_EMPTY);
                    }
                }
            });
        } else {
            setViewState(word, Constant.STATE_NO_INTERNET);
        }
    }

    private void addTranslations(final ViewHolder holder, AlienWord word, List<AlienWordTranslation> translations){
        holder.translationsLayout.removeAllViews();
        for(AlienWordTranslation translation : translations){
            RelativeLayout translationLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.list_item_translation, null, false);
            TranslationAdapter.ViewHolder translationHolder = new TranslationAdapter.ViewHolder(translationLayout);
            TranslationUtil.setupTranslation(context, translationHolder, translation);
            holder.translationsLayout.addView(translationLayout);
        }
        setViewState(word, null);
    }

    private void updateLanguage(ViewHolder holder, String language){
        final String english = context.getString(R.string.english);
        final String portuguese = context.getString(R.string.portuguese);
        final String german = context.getString(R.string.german);
        int flagResId = -1;
        if(language.equals(english)){
            this.languageCode = LanguageUtil.LANGUAGE_EN;
            flagResId = R.drawable.flag_uk_small;
        } else if(language.equals(portuguese)){
            this.languageCode = LanguageUtil.LANGUAGE_PT;
            flagResId = R.drawable.flag_brazil_small;
        } else if(language.equals(german)){
            this.languageCode = LanguageUtil.LANGUAGE_DE;
            flagResId = R.drawable.flag_germany_small;
        }
        holder.languageView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(flagResId), null, holder.languageView.getCompoundDrawables()[2], null);
    }

    private void newTranslation(AlienWord word){
        if(App.isSignedIn()) {
            EventBus.getDefault().postSticky(new AddTranslationEvent(word));
            context.startActivity(new Intent(context, TranslationEditorActivity.class));
        } else {
            MainActivity.showSignInDialog(context);
        }
    }

    private void seeAllTranslations(AlienWord word){
        TranslationUtil.showTranslationsDialog(context, word, languageCode);
    }

    private void shareTranslation(AlienRace race, AlienWord word){
        if(hasTranslations(word)) {
            List<AlienWordTranslation> translations = getCurrentLanguageTranslations(word);
            if(Util.isNotEmpty(translations)) {
                StringBuilder shareText = new StringBuilder()
                        .append(context.getString(R.string.word) + ": " + word.getWord())
                        .append("\n")
                        .append(context.getString(R.string.race) + ": " + race.getName())
                        .append("\n")
                        .append(context.getString(R.string.best_translations) + ": ");
                Iterator<AlienWordTranslation> i = translations.iterator();
                while (i.hasNext()) {
                    shareText.append(i.next().getTranslation());
                    if (i.hasNext()) {
                        shareText.append(", ");
                    }
                }
                shareText.append("\n\n");
                shareText.append(String.format("Download %s: %s", context.getString(R.string.app_name), Util.getGooglePlayUrl(context)));
                Util.shareText(context, shareText.toString());
            }
        } else {
            Toast.makeText(context, R.string.no_translation_found, Toast.LENGTH_SHORT).show();
        }
    }

    private List<AlienWordTranslation> getCurrentLanguageTranslations(AlienWord word){
        switch (languageCode){
            case LanguageUtil.LANGUAGE_PT:
                return ptWordTranslations.get(word.getObjectId());
            case LanguageUtil.LANGUAGE_DE:
                return deWordTranslations.get(word.getObjectId());
            default:
                return enWordTranslations.get(word.getObjectId());
        }
    }

    private boolean hasTranslations(AlienWord word){
        return Util.isNotEmpty(getCurrentLanguageTranslations(word));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title_layout)
        RelativeLayout titleLayout;
        @BindView(R.id.card_layout)
        FoldingCell cardLayout;
        @BindView(R.id.alien_word_title)
        TextView alienWordTitleView;
        @BindView(R.id.alien_word)
        TextView alienWordView;
        @BindView(R.id.tag_title)
        TextView alienRaceTitleView;
        @BindView(R.id.tag)
        TextView alienRaceView;
        @BindView(R.id.language)
        MaterialSpinner languageView;
        @BindView(R.id.translations_layout)
        LinearLayout translationsLayout;
        @BindView(R.id.new_translation)
        TextView newTranslationView;
        @BindView(R.id.see_all_translations)
        TextView seeAllTranslationsView;
        @BindView(R.id.share_translation)
        TextView shareTranslationView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}