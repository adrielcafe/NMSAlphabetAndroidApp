package cafe.adriel.nmsalphabet.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.util.Util;

public class UpdateActivity extends BaseActivity {

    @BindView(R.id.bg)
    ImageView bgView;
    @BindView(R.id.update)
    RelativeLayout updateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        tintManager.setTintColor(Color.BLACK);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void init() {
        adjustMarginAndPadding();
        Glide.with(this)
                .load(R.drawable.bg_update)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(bgView);
        updateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri marketUri = Uri.parse(Constant.MARKET_URI + Util.getPackageName(UpdateActivity.this));
                Intent i = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(i);
                finish();
            }
        });
    }
}