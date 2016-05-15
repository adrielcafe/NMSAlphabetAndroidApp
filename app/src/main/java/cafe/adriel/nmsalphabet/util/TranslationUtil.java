package cafe.adriel.nmsalphabet.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;
import java.util.List;

import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.ui.adapter.TranslationAdapter;
import cafe.adriel.nmsalphabet.ui.util.EndlessRecyclerOnScrollListener;

public class TranslationUtil {

    public static void showTranslationsDialog(Context context, final AlienWord word, final String languageCode){
        final List<AlienWordTranslation> translations = new ArrayList<>();
        final AlienRace race = DbUtil.getRaceById(word.getRace().getObjectId());
        final TranslationAdapter adapter = new TranslationAdapter(context, translations);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        EndlessRecyclerOnScrollListener infiniteScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                updateTranslations(adapter, race, word, translations, languageCode, currentPage);
            }
        };

        View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_translations, null, false);
        RecyclerView translationsView = (RecyclerView) rootView.findViewById(R.id.translations);
        translationsView.addOnScrollListener(infiniteScrollListener);
        translationsView.setHasFixedSize(true);
        translationsView.setLayoutManager(layoutManager);
        translationsView.setAdapter(adapter);

        int flagResId;
        switch (languageCode){
            case LanguageUtil.LANGUAGE_PT:
                flagResId = R.drawable.flag_brazil_big;
                break;
            case LanguageUtil.LANGUAGE_DE:
                flagResId = R.drawable.flag_germany_big;
                break;
            default:
                flagResId = R.drawable.flag_uk_big;
        }

        updateTranslations(adapter, race, word, translations, languageCode, 0);

        new AlertDialog.Builder(context)
                .setIcon(flagResId)
                .setTitle(word.getWord())
                .setView(rootView)
                .setNegativeButton(R.string.close, null)
                .show();
    }

    private static void updateTranslations(final TranslationAdapter adapter, AlienRace race, AlienWord word, final List<AlienWordTranslation> translations, String languageCode, int page){
        DbUtil.getTranslations(race, word, languageCode, page, new FindCallback<AlienWordTranslation>() {
            @Override
            public void done(List<AlienWordTranslation> result, ParseException e) {
                if (Util.isNotEmpty(result)) {
                    translations.addAll(result);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public static void setupTranslation(final Context context, final TranslationAdapter.ViewHolder holder, final AlienWordTranslation translation){
        final BadgeView likeBadgeView = addBadge(context, holder.likeView, translation.getLikesCount());
        final BadgeView dislikeBadgeView = addBadge(context, holder.dislikeView, translation.getDislikesCount());

        holder.translationView.setText(translation.getTranslation());
        holder.likeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableLikeFor1Second(holder.likeView, holder.dislikeView);
                likeTranslation(context, translation, holder.likeView, holder.dislikeView, likeBadgeView, dislikeBadgeView);
            }
        });
        holder.dislikeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableLikeFor1Second(holder.likeView, holder.dislikeView);
                dislikeTranslation(context, translation, holder.likeView, holder.dislikeView, likeBadgeView, dislikeBadgeView);
            }
        });

        if(DbUtil.isTranslationLiked(translation)){
            holder.likeView.setTextColor(Color.BLACK);
            holder.dislikeView.setTextColor(context.getResources().getColor(R.color.gray));
        } else if(DbUtil.isTranslationDisliked(translation)){
            holder.likeView.setTextColor(context.getResources().getColor(R.color.gray));
            holder.dislikeView.setTextColor(Color.BLACK);
        } else {
            holder.likeView.setTextColor(context.getResources().getColor(R.color.gray));
            holder.dislikeView.setTextColor(context.getResources().getColor(R.color.gray));
        }
    }

    private static void likeTranslation(Context context, AlienWordTranslation translation, TextView likeView, TextView dislikeView, BadgeView likeBadgeView, BadgeView dislikeBadgeView){
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

    private static void dislikeTranslation(Context context, AlienWordTranslation translation, TextView likeView, TextView dislikeView, BadgeView likeBadgeView, BadgeView dislikeBadgeView){
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

    private static BadgeView addBadge(Context context, View view, int count){
        BadgeView badge = new BadgeView(context, view);
        badge.setText(count+"");
        badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        badge.setBadgeBackgroundColor(ThemeUtil.getAccentColor(context));
        badge.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        badge.setTextSize(11);
        badge.show();
        return badge;
    }

    private static void disableLikeFor1Second(final View likeView, final View dislikeView){
        likeView.setEnabled(false);
        dislikeView.setEnabled(false);
        Util.asyncCall(1000, new Runnable() {
            @Override
            public void run() {
                likeView.setEnabled(true);
                dislikeView.setEnabled(true);
            }
        });
    }

}