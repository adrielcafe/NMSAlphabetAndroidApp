package cafe.adriel.nmsalphabet.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
    private Map<String, List<AlienWordTranslation>> wordTranslations;

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

        if(language == null){
            language = LanguageUtil.getCurrentLanguage(context);
        }
        if(wordTranslations == null){
            wordTranslations = new HashMap<>();
        }
        if(viewStates == null){
            viewStates = new HashMap<>();
        }
        if(!viewStates.containsKey(word.getObjectId())) {
            viewStates.put(word.getObjectId(), createViewState(holder));
        }

        initFlag(holder);

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
                if(!wordTranslations.containsKey(word.getObjectId())){
                    loadTranslations(holder, race, word);
                }
                holder.cardLayout.unfold(false);
            }
        });
    }

    private void initFlag(ViewHolder holder){
        int flagResId;
        switch (language){
            case LanguageUtil.LANGUAGE_PT:
                flagResId = R.drawable.flag_brazil;
                break;
            case LanguageUtil.LANGUAGE_DE:
                flagResId = R.drawable.flag_germany;
                break;
            default:
                flagResId = R.drawable.flag_uk;
                break;
        }
        Glide.with(context).load(flagResId).into(holder.countryFlagView);
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
        if(Util.isConnected(context)) {
            setViewState(word, Constant.STATE_LOADING);
            DbUtil.getTranslations(race, word, language, new FindCallback<AlienWordTranslation>() {
                @Override
                public void done(List<AlienWordTranslation> translations, ParseException e) {
                    if (Util.isNotEmpty(translations)) {
                        wordTranslations.put(word.getObjectId(), translations);
                        for(AlienWordTranslation translation : translations){
                            addTranslation(holder, translation);
                            addTranslation(holder, translation);
                        }
                        setViewState(word, null);
                    } else {
                        setViewState(word, Constant.STATE_EMPTY);
                    }
                }
            });
        } else {
            setViewState(word, Constant.STATE_NO_INTERNET);
        }
    }

    private void addTranslation(ViewHolder holder, AlienWordTranslation translation){
        RelativeLayout translationLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.list_item_translation, null, false);
        TextView translationView = (TextView) translationLayout.findViewById(R.id.translation);
        final TextView likeView = (TextView) translationLayout.findViewById(R.id.like);
        final TextView dislikeView = (TextView) translationLayout.findViewById(R.id.dislike);

        translationView.setText(translation.getTranslation());
        likeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeView.setTextColor(Color.BLACK);
                dislikeView.setTextColor(context.getResources().getColor(R.color.gray));
            }
        });
        dislikeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislikeView.setTextColor(Color.BLACK);
                likeView.setTextColor(context.getResources().getColor(R.color.gray));
            }
        });

        addBadge(likeView, 1);
        addBadge(dislikeView, 45);

        holder.translationsLayout.addView(translationLayout);
    }

    private void addBadge(View view, int count){
        BadgeView badge = new BadgeView(context, view);
        badge.setText(count+"");
        badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        badge.setBadgeBackgroundColor(ThemeUtil.getAccentColor(context));
        badge.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        badge.setTextSize(11);
        badge.show();
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
        @BindView(R.id.country_flag)
        ImageView countryFlagView;
        @BindView(R.id.translations_layout)
        LinearLayout translationsLayout;
//        @BindView(R.id.translation_1)
//        TextView translation1View;
//        @BindView(R.id.translation_2)
//        TextView translation2View;
//        @BindView(R.id.translation_3)
//        TextView translation3View;
//        @BindView(R.id.translation_4)
//        TextView translation4View;
//        @BindView(R.id.translation_5)
//        TextView translation5View;
        @BindView(R.id.add_translation)
        TextView addTranslationView;
        @BindView(R.id.see_all_translations)
        TextView seeAllTranslationsView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}