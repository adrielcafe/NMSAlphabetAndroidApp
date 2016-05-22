package cafe.adriel.nmsalphabet.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.goebl.david.Webb;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.readystatesoftware.viewbadger.BadgeView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.ui.adapter.TranslationAdapter;
import cafe.adriel.nmsalphabet.ui.util.EndlessRecyclerOnScrollListener;
import mehdi.sakout.dynamicbox.DynamicBox;

public class TranslationUtil {

    private static final String VISION_API_URL          = "https://vision.googleapis.com/v1/images:annotate?key=";
    private static final String VISION_API_REQUEST_BODY =
            "{\"requests\": [{\"features\": [{\"type\": \"TEXT_DETECTION\"}],\"image\": {\"content\": \"%s\"}}]}";

    public static String extractTextFromImage(Context context, Bitmap image){
        String base64Img = Util.toBase64(image);
        String body = String.format(VISION_API_REQUEST_BODY, base64Img);
        try {
            JSONObject json = Util.getWebb()
                    .post(VISION_API_URL + context.getString(R.string.google_vision_key))
                    .header(Webb.HDR_CONTENT_TYPE, Webb.APP_JSON)
                    .body(body)
                    .ensureSuccess()
                    .asJsonObject()
                    .getBody();
            String text = json.getJSONArray("responses")
                    .getJSONObject(0)
                    .getJSONArray("textAnnotations")
                    .getJSONObject(0)
                    .getString("description")
                    .toUpperCase();
            AnalyticsUtil.ocrEvent(text);
            return text;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void showTranslationsDialog(final Context context, final AlienWord word, final String languageCode){
        View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_translations, null, false);
        RecyclerView translationsView = (RecyclerView) rootView.findViewById(R.id.translations);

        final List<AlienWordTranslation> translations = new ArrayList<>();
        final AlienRace race = DbUtil.getRaceById(word.getRace().getObjectId());
        final TranslationAdapter adapter = new TranslationAdapter(context, translations);
        final DynamicBox viewState = createViewState(context, translationsView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        EndlessRecyclerOnScrollListener infiniteScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                updateTranslations(context, adapter, viewState, race, word, translations, languageCode, currentPage);
            }
        };

        viewState.showCustomView(Constant.STATE_LOADING);
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

        updateTranslations(context, adapter, viewState, race, word, translations, languageCode, 0);

        new AlertDialog.Builder(context)
                .setIcon(flagResId)
                .setTitle(word.getWord())
                .setView(rootView)
                .setNegativeButton(R.string.close, null)
                .show();
    }

    public static DynamicBox createViewState(Context context, View view){
        View loadingState = LayoutInflater.from(context).inflate(R.layout.state_translations_loading, null, false);
        View emptyState = LayoutInflater.from(context).inflate(R.layout.state_translations_empty, null, false);
        View noInternetState = LayoutInflater.from(context).inflate(R.layout.state_translations_no_internet, null, false);

        DynamicBox viewState = new DynamicBox(context, view);
        viewState.addCustomView(loadingState, Constant.STATE_LOADING);
        viewState.addCustomView(emptyState, Constant.STATE_EMPTY);
        viewState.addCustomView(noInternetState, Constant.STATE_NO_INTERNET);
        return viewState;
    }

    private static void updateTranslations(Context context, final TranslationAdapter adapter, final DynamicBox viewState, AlienRace race, AlienWord word, final List<AlienWordTranslation> translations, String languageCode, final int page){
        if(Util.isConnected(context)) {
            DbUtil.getTranslations(race, word, languageCode, page, new FindCallback<AlienWordTranslation>() {
                @Override
                public void done(List<AlienWordTranslation> result, ParseException e) {
                    if (Util.isNotEmpty(result)) {
                        translations.addAll(result);
                        adapter.notifyDataSetChanged();
                        viewState.hideAll();
                    } else if (page == 0) {
                        viewState.showCustomView(Constant.STATE_EMPTY);
                    }
                }
            });
        } else {
            viewState.showCustomView(Constant.STATE_NO_INTERNET);
        }
    }

    public static void setupTranslation(final Context context, final TranslationAdapter.ViewHolder holder, final AlienWordTranslation translation){
        final BadgeView likeBadgeView = addBadge(context, holder.likeView, translation.getLikesCount());
        final BadgeView dislikeBadgeView = addBadge(context, holder.dislikeView, translation.getDislikesCount());

        holder.translationView.setText(translation.getTranslation());

        if(App.isSignedIn()) {
            holder.likeView.setEnabled(true);
            holder.dislikeView.setEnabled(true);
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
        } else {
            holder.likeView.setEnabled(false);
            holder.dislikeView.setEnabled(false);
            holder.likeView.setOnClickListener(null);
            holder.dislikeView.setOnClickListener(null);
            holder.likeView.setTextColor(context.getResources().getColor(R.color.gray_light));
            holder.dislikeView.setTextColor(context.getResources().getColor(R.color.gray_light));
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
            AnalyticsUtil.likeEvent(translation.getRace(), translation.getWord(), translation);
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
            AnalyticsUtil.dislikeEvent(translation.getRace(), translation.getWord(), translation);
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