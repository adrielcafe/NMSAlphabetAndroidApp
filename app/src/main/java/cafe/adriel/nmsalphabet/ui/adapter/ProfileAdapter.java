package cafe.adriel.nmsalphabet.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.ramotion.foldingcell.FoldingCell;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.event.EditTranslationEvent;
import cafe.adriel.nmsalphabet.event.UpdateStateEvent;
import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.ui.TranslationEditorActivity;
import cafe.adriel.nmsalphabet.util.DbUtil;
import cafe.adriel.nmsalphabet.util.LanguageUtil;
import cafe.adriel.nmsalphabet.util.ThemeUtil;
import cafe.adriel.nmsalphabet.util.Util;
import mehdi.sakout.dynamicbox.DynamicBox;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private Context context;
    private List<AlienWord> words;
    private Map<String, DynamicBox> viewStates;
    private Map<String, List<AlienWordTranslation>> wordTranslations;

    public ProfileAdapter(Context context, List<AlienWord> words) {
        this.context = context;
        this.words = words;
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_profile, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AlienWord word = words.get(position);
        final AlienRace race = DbUtil.getRaceById(word.getRace().getObjectId());

        if(wordTranslations == null){
            wordTranslations = new HashMap<>();
        } else if(wordTranslations.containsKey(word.getObjectId())){
            setTranslations(holder, word);
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
                if(!wordTranslations.containsKey(word.getObjectId())){
                    loadTranslations(holder, race, word);
                }
                holder.cardLayout.unfold(false);
            }
        });
        holder.removeTranslationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTranslation(word);
            }
        });
        holder.editTranslationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTranslation(word);
            }
        });
        holder.shareTranslationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTranslation(race, word);
            }
        });
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
            DbUtil.getUserTranslations(App.getUser(), race, word, new FindCallback<AlienWordTranslation>() {
                @Override
                public void done(List<AlienWordTranslation> translations, ParseException e) {
                    if (Util.isNotEmpty(translations)) {
                        wordTranslations.put(word.getObjectId(), translations);
                        setTranslations(holder, word);
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

    private void setTranslations(ViewHolder holder, AlienWord word){
        if(wordTranslations.containsKey(word.getObjectId())) {
            List<AlienWordTranslation> translations = wordTranslations.get(word.getObjectId());
            for (AlienWordTranslation translation : translations) {
                if (translation.getLanguage().equals(LanguageUtil.LANGUAGE_EN) && holder.enTranslationView.getText().toString().isEmpty()) {
                    holder.enTranslationView.setText(translation.getTranslation());
                } else if (translation.getLanguage().equals(LanguageUtil.LANGUAGE_PT) && holder.ptTranslationView.getText().toString().isEmpty()) {
                    holder.ptTranslationView.setText(translation.getTranslation());
                } else if (translation.getLanguage().equals(LanguageUtil.LANGUAGE_DE) && holder.deTranslationView.getText().toString().isEmpty()) {
                    holder.deTranslationView.setText(translation.getTranslation());
                }
            }
        }
    }

    private void editTranslation(AlienWord word){
        EventBus.getDefault().postSticky(new EditTranslationEvent(word, wordTranslations.get(word.getObjectId())));
        context.startActivity(new Intent(context, TranslationEditorActivity.class));
    }

    private void removeTranslation(final AlienWord word){
        new AlertDialog.Builder(context)
                .setTitle(R.string.delete_translation)
                .setMessage(R.string.translation_will_be_deleted_permanently)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTranslation(word);
                    }
                })
                .show();
    }

    private void deleteTranslation(final AlienWord word){
        final AlertDialog dialog = Util.showLoadingDialog(context);
        word.removeUser(App.getUser());
        word.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    for(AlienWordTranslation translation : wordTranslations.get(word.getObjectId())){
                        translation.removeUser(App.getUser());
                        translation.saveInBackground();
                    }
                    removeWord(word);
                }
                dialog.dismiss();
            }
        });
    }

    private void removeWord(AlienWord word) {
        int position = getWordPosition(word);
        if(position >= 0){
            words.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeRemoved(position, words.size());
            if(Util.isEmpty(words)){
                EventBus.getDefault().postSticky(new UpdateStateEvent());
            }
        }
    }

    public void updateWordAndTranslations(AlienWord word, List<AlienWordTranslation> translations) {
        int position = getWordPosition(word);
        if(position >= 0) {
            if(translations == null){
                translations = new ArrayList<>();
            }
            wordTranslations.put(word.getObjectId(), translations);
            words.set(position, word);
            notifyItemChanged(position);
            notifyItemRangeChanged(position, words.size());
        }
    }

    public int getWordPosition(AlienWord word){
        for(int i = 0; i < words.size(); i++){
            if(words.get(i).getObjectId().equals(word.getObjectId())){
                return i;
            }
        }
        return -1;
    }

    private void shareTranslation(AlienRace race, AlienWord word){
        AlienWordTranslation translation = null;
        for(AlienWordTranslation t : wordTranslations.get(word.getObjectId())){
            if(t.getLanguage().equals(LanguageUtil.getCurrentLanguageCode(context))){
                translation = t;
                break;
            }
        }
        if(translation != null) {
            StringBuilder shareText = new StringBuilder()
                    .append(context.getString(R.string.word) + ": " + word.getWord())
                    .append("\n")
                    .append(context.getString(R.string.race) + ": " + race.getName())
                    .append("\n")
                    .append(context.getString(R.string.my_translation) + ": " + translation.getTranslation())
                    .append("\n\n");
            shareText.append(String.format("Download %s: %s", context.getString(R.string.app_name), Util.getGooglePlayUrl(context)));
            Util.shareText((Activity) context, shareText.toString());
        }
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
        @BindView(R.id.translations_layout)
        LinearLayout translationsLayout;
        @BindView(R.id.english_translation)
        TextView enTranslationView;
        @BindView(R.id.portuguese_translation)
        TextView ptTranslationView;
        @BindView(R.id.german_translation)
        TextView deTranslationView;
        @BindView(R.id.delete_translation)
        TextView removeTranslationView;
        @BindView(R.id.edit_translation)
        TextView editTranslationView;
        @BindView(R.id.share_translation)
        TextView shareTranslationView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}