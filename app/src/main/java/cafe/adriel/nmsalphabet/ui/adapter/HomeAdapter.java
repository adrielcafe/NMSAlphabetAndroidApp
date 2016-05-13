package cafe.adriel.nmsalphabet.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.ramotion.foldingcell.FoldingCell;
import com.readystatesoftware.viewbadger.BadgeView;

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
import cafe.adriel.nmsalphabet.util.DbUtil;
import cafe.adriel.nmsalphabet.util.LanguageUtil;
import cafe.adriel.nmsalphabet.util.ThemeUtil;
import cafe.adriel.nmsalphabet.util.Util;
import mehdi.sakout.dynamicbox.DynamicBox;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context context;
    private String language;
    private List<AlienWord> words;
    private Map<String, DynamicBox> viewStates;
    private Map<String, List<AlienWordTranslation>> enWordTranslations;
    private Map<String, List<AlienWordTranslation>> ptWordTranslations;
    private Map<String, List<AlienWordTranslation>> deWordTranslations;

    public HomeAdapter(Context context, List<AlienWord> words) {
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
            viewStates.put(word.getObjectId(), createViewState(holder));
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
        initLanguage(holder, race, word);
    }

    private void initLanguage(final ViewHolder holder, final AlienRace race, final AlienWord word){
        String[] languages = context.getResources().getStringArray(R.array.language_entries);
        int languageIndex = 0;

        if(language == null){
            language = LanguageUtil.getCurrentLanguageCode(context);
        }
        for (int i = 0; i < languages.length; i++){
            if(languages[i].equals(LanguageUtil.languageCodeToLanguage(context, language))){
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

        updateLanguage(holder, LanguageUtil.languageCodeToLanguage(context, language));
    }

    private DynamicBox createViewState(ViewHolder holder){
        View loadingState = LayoutInflater.from(context).inflate(R.layout.state_translations_loading, null, false);
        View emptyState = LayoutInflater.from(context).inflate(R.layout.state_translations_empty, null, false);
        View noInternetState = LayoutInflater.from(context).inflate(R.layout.state_translations_no_internet, null, false);

        DynamicBox viewState = new DynamicBox(context, holder.translationsLayout);
        viewState.addCustomView(loadingState, Constant.STATE_LOADING);
        viewState.addCustomView(emptyState, Constant.STATE_EMPTY);
        viewState.addCustomView(noInternetState, Constant.STATE_NO_INTERNET);
        return viewState;
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
        switch (language){
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
            DbUtil.getTranslations(race, word, language, new FindCallback<AlienWordTranslation>() {
                @Override
                public void done(List<AlienWordTranslation> translations, ParseException e) {
                    if (Util.isNotEmpty(translations)) {
                        switch (language){
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

    private void addTranslations(ViewHolder holder, AlienWord word, List<AlienWordTranslation> translations){
        holder.translationsLayout.removeAllViews();
        for(AlienWordTranslation translation : translations){
            addTranslation(holder, translation);
        }
        setViewState(word, null);
    }

    private void addTranslation(final ViewHolder holder, final AlienWordTranslation translation){
        RelativeLayout translationLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.list_item_translation, null, false);
        TextView translationView = (TextView) translationLayout.findViewById(R.id.translation);
        final TextView likeView = (TextView) translationLayout.findViewById(R.id.like);
        final TextView dislikeView = (TextView) translationLayout.findViewById(R.id.dislike);
        final BadgeView likeBadgeView = addBadge(likeView, translation.getLikesCount());
        final BadgeView dislikeBadgeView = addBadge(dislikeView, translation.getDislikesCount());

        translationView.setText(translation.getTranslation());
        likeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeTranslation(translation, likeView, dislikeView, likeBadgeView, dislikeBadgeView);
            }
        });
        dislikeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislikeTranslation(translation, likeView, dislikeView, likeBadgeView, dislikeBadgeView);
            }
        });

        if(DbUtil.isTranslationLiked(translation)){
            likeView.setTextColor(Color.BLACK);
            dislikeView.setTextColor(context.getResources().getColor(R.color.gray));
        } else if(DbUtil.isTranslationDisliked(translation)){
            likeView.setTextColor(context.getResources().getColor(R.color.gray));
            dislikeView.setTextColor(Color.BLACK);
        } else {
            likeView.setTextColor(context.getResources().getColor(R.color.gray));
            dislikeView.setTextColor(context.getResources().getColor(R.color.gray));
        }

        holder.translationsLayout.addView(translationLayout);
    }

    private void likeTranslation(AlienWordTranslation translation, TextView likeView, TextView dislikeView, BadgeView likeBadgeView, BadgeView dislikeBadgeView){
        if(!DbUtil.isTranslationLiked(translation)) {
            int likesCount = Integer.parseInt(likeBadgeView.getText().toString()) + 1;
            int dislikesCount = Integer.parseInt(dislikeBadgeView.getText().toString()) - 1;

            if(likesCount < 0) {
                likesCount = 0;
            }
            if(dislikesCount < 0) {
                dislikesCount = 0;
            }

            likeBadgeView.setText(likesCount+"");
            dislikeBadgeView.setText(dislikesCount+"");
            likeView.setTextColor(Color.BLACK);
            dislikeView.setTextColor(context.getResources().getColor(R.color.gray));

            DbUtil.likeTranslation(translation);
        }
    }

    private void dislikeTranslation(AlienWordTranslation translation, TextView likeView, TextView dislikeView, BadgeView likeBadgeView, BadgeView dislikeBadgeView){
        if(!DbUtil.isTranslationDisliked(translation)) {
            int likesCount = Integer.parseInt(likeBadgeView.getText().toString()) - 1;
            int dislikesCount = Integer.parseInt(dislikeBadgeView.getText().toString()) + 1;

            if(likesCount < 0) {
                likesCount = 0;
            }
            if(dislikesCount < 0) {
                dislikesCount = 0;
            }

            likeBadgeView.setText(likesCount+"");
            dislikeBadgeView.setText(dislikesCount+"");
            likeView.setTextColor(context.getResources().getColor(R.color.gray));
            dislikeView.setTextColor(Color.BLACK);

            DbUtil.dislikeTranslation(translation);
        }
    }

    private BadgeView addBadge(View view, int count){
        BadgeView badge = new BadgeView(context, view);
        badge.setText(count+"");
        badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        badge.setBadgeBackgroundColor(ThemeUtil.getAccentColor(context));
        badge.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        badge.setTextSize(11);
        badge.show();
        return badge;
    }

    private void updateLanguage(ViewHolder holder, String language){
        final String english = context.getString(R.string.english);
        final String portuguese = context.getString(R.string.portuguese);
        final String german = context.getString(R.string.german);
        int flagResId = -1;
        if(language.equals(english)){
            this.language = LanguageUtil.LANGUAGE_EN;
            flagResId = R.drawable.flag_uk;
        } else if(language.equals(portuguese)){
            this.language = LanguageUtil.LANGUAGE_PT;
            flagResId = R.drawable.flag_brazil;
        } else if(language.equals(german)){
            this.language = LanguageUtil.LANGUAGE_DE;
            flagResId = R.drawable.flag_germany;
        }
        holder.languageView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(flagResId), null, holder.languageView.getCompoundDrawables()[2], null);
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

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}