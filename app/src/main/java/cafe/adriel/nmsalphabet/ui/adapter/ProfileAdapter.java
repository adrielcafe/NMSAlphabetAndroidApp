package cafe.adriel.nmsalphabet.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.ramotion.foldingcell.FoldingCell;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.util.DbUtil;
import cafe.adriel.nmsalphabet.util.LanguageUtil;
import cafe.adriel.nmsalphabet.util.ThemeUtil;
import cafe.adriel.nmsalphabet.util.Util;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private Context context;
    private List<AlienWord> wordList;

    public ProfileAdapter(Context context, List<AlienWord> wordList) {
        this.context = context;
        this.wordList = wordList;
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_profile, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AlienWord word = wordList.get(position);
        final AlienRace race = DbUtil.getRaceById(word.getRace().getObjectId());
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
                holder.cardLayout.toggle(false);
                if(holder.wordTranslationsList == null){
                    loadTranslations(race, word, holder);
                }
            }
        });
    }

    private void loadTranslations(AlienRace race, AlienWord word, final ViewHolder holder){
        DbUtil.getUserTranslations(race, word, App.getUser(), new FindCallback<AlienWordTranslation>() {
            @Override
            public void done(List<AlienWordTranslation> objects, ParseException e) {
                holder.wordTranslationsList = objects;
                if(Util.isNotEmpty(holder.wordTranslationsList)){
                    for(AlienWordTranslation translation : holder.wordTranslationsList){
                        if(translation.getLanguage().equals(LanguageUtil.LANGUAGE_EN) && holder.englishTranslationView.getText().toString().isEmpty()){
                            holder.englishTranslationView.setText(translation.getTranslation());
                        } else if(translation.getLanguage().equals(LanguageUtil.LANGUAGE_PT) && holder.portugueseTranslationView.getText().toString().isEmpty()){
                            holder.portugueseTranslationView.setText(translation.getTranslation());
                        } else if(translation.getLanguage().equals(LanguageUtil.LANGUAGE_DE) && holder.germanTranslationView.getText().toString().isEmpty()){
                            holder.germanTranslationView.setText(translation.getTranslation());
                        }
                    }
                }
            }
        });
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
        @BindView(R.id.english_translation)
        TextView englishTranslationView;
        @BindView(R.id.portuguese_translation)
        TextView portugueseTranslationView;
        @BindView(R.id.german_translation)
        TextView germanTranslationView;
        @BindView(R.id.delete_translation)
        TextView removeTranslationView;
        @BindView(R.id.edit_translation)
        TextView editTranslationView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}