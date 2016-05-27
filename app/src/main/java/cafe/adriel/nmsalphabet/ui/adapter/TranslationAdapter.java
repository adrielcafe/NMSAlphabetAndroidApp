package cafe.adriel.nmsalphabet.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.util.TranslationUtil;

public class TranslationAdapter extends RecyclerView.Adapter<TranslationAdapter.ViewHolder> {

    private Context context;
    private List<AlienWordTranslation> translations;

    public TranslationAdapter(Context context, List<AlienWordTranslation> translations) {
        this.context = context;
        this.translations = translations;
    }

    @Override
    public int getItemCount() {
        return translations.size();
    }

    @Override
    public TranslationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_translation, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        AlienWordTranslation translation = translations.get(position);
        TranslationUtil.setupTranslation(context, holder, translation);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.translation)
        public TextView translationView;
        @BindView(R.id.like)
        public TextView likeView;
        @BindView(R.id.dislike)
        public TextView dislikeView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}