package cafe.adriel.nmsalphabet.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.ramotion.foldingcell.FoldingCell;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.util.DbUtil;
import cafe.adriel.nmsalphabet.util.LanguageUtil;
import cafe.adriel.nmsalphabet.util.ThemeUtil;
import cafe.adriel.nmsalphabet.util.Util;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context context;
    private List<AlienWord> wordList;
    private String language;

    public HomeAdapter(Context context, List<AlienWord> wordList) {
        this.context = context;
        this.wordList = wordList;
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_home, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(language == null){
            language = LanguageUtil.getCurrentLanguage(context);
        }
        final AlienWord word = wordList.get(position);
        final AlienRace race = DbUtil.getRaceById(word.getRace().getObjectId());
        loadFlag(holder);
        holder.wordTranslationsList = null;
        holder.cardLayout.initialize(1000, context.getResources().getColor(R.color.gray), 2);
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
                holder.cardLayout.unfold(false);
                if(holder.wordTranslationsList == null){
                    loadTranslations(race, word, holder);
                }
            }
        });
    }

    private void loadFlag(ViewHolder holder){
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

    private void loadTranslations(AlienRace race, AlienWord word, final ViewHolder holder){
        DbUtil.getTranslations(race, word, language, new FindCallback<AlienWordTranslation>() {
            @Override
            public void done(List<AlienWordTranslation> objects, ParseException e) {
                holder.wordTranslationsList = objects;
                if(Util.isNotEmpty(holder.wordTranslationsList)){
                    if(holder.wordTranslationsList.size() >= 1){
                        holder.translation1View.setText(holder.wordTranslationsList.get(0).getTranslation());
                        addBadge(holder.translation1View, holder.wordTranslationsList.get(0).getUsersCount());
                    }
                    if(holder.wordTranslationsList.size() >= 2){
                        holder.translation2View.setText(holder.wordTranslationsList.get(1).getTranslation());
                        addBadge(holder.translation2View, holder.wordTranslationsList.get(1).getUsersCount());
                    }
                    if(holder.wordTranslationsList.size() >= 3){
                        holder.translation3View.setText(holder.wordTranslationsList.get(2).getTranslation());
                        addBadge(holder.translation3View, holder.wordTranslationsList.get(2).getUsersCount());
                    }
                    if(holder.wordTranslationsList.size() >= 4){
                        holder.translation4View.setText(holder.wordTranslationsList.get(3).getTranslation());
                        addBadge(holder.translation4View, holder.wordTranslationsList.get(3).getUsersCount());
                    }
                    if(holder.wordTranslationsList.size() >= 5){
                        holder.translation5View.setText(holder.wordTranslationsList.get(4).getTranslation());
                        addBadge(holder.translation5View, holder.wordTranslationsList.get(4).getUsersCount());
                    }
                }
            }
        });
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
        List<AlienWordTranslation> wordTranslationsList;

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
        @BindView(R.id.translation_1)
        TextView translation1View;
        @BindView(R.id.translation_2)
        TextView translation2View;
        @BindView(R.id.translation_3)
        TextView translation3View;
        @BindView(R.id.translation_4)
        TextView translation4View;
        @BindView(R.id.translation_5)
        TextView translation5View;
        @BindView(R.id.add_translation)
        TextView addTranslationView;
        @BindView(R.id.report_translation)
        TextView reportTranslationView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}