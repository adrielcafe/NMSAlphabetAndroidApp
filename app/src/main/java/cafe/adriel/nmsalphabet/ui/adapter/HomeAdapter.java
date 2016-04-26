package cafe.adriel.nmsalphabet.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramotion.foldingcell.FoldingCell;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.R;
import me.grantland.widget.AutofitHelper;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context context;
    private List<String> wordList;

    public HomeAdapter(Context context, List<String> wordList) {
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
        String word = wordList.get(position);
        holder.cardLayout.initialize(1000, context.getResources().getColor(R.color.gray), 2);
        holder.alienWordTitleView.setText(word);
        holder.alienWordView.setText(word);
        holder.alienRaceTitleView.setText("Korvax");
        holder.alienRaceView.setText("Korvax's Word");

        AutofitHelper.create(holder.alienWordTitleView);
        AutofitHelper.create(holder.alienWordView);

        addBadge(holder.translation1View, 12345);
        addBadge(holder.translation2View, 1234);
        addBadge(holder.translation3View, 123);
        addBadge(holder.translation4View, 12);
        addBadge(holder.translation5View, 1);
    }

    private void addBadge(View view, int count){
        BadgeView badge = new BadgeView(context, view);
        badge.setText(count+"");
        badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        badge.setBadgeBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        badge.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        badge.setTextSize(11);
        badge.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.card_layout)
        FoldingCell cardLayout;
        @Bind(R.id.alien_word_title)
        TextView alienWordTitleView;
        @Bind(R.id.alien_word)
        TextView alienWordView;
        @Bind(R.id.tag_title)
        TextView alienRaceTitleView;
        @Bind(R.id.tag)
        TextView alienRaceView;
        @Bind(R.id.country_flag)
        ImageView countryFlagView;
        @Bind(R.id.translation_1)
        TextView translation1View;
        @Bind(R.id.translation_2)
        TextView translation2View;
        @Bind(R.id.translation_3)
        TextView translation3View;
        @Bind(R.id.translation_4)
        TextView translation4View;
        @Bind(R.id.translation_5)
        TextView translation5View;
        @Bind(R.id.add_translation)
        TextView addTranslationView;
        @Bind(R.id.report_translation)
        TextView reportTranslationView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}