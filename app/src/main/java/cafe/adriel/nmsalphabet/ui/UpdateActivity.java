package cafe.adriel.nmsalphabet.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.util.Util;

public class UpdateActivity extends BaseActivity {

    @BindView(R.id.bg)
    ImageView bgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        tintManager.setTintColor(Color.BLACK);
        ButterKnife.bind(this);
        init();
    }

    @OnClick(R.id.update)
    public void updateApp(){
        Uri marketUri = Uri.parse(Constant.MARKET_URI + Util.getPackageName(UpdateActivity.this));
        Intent i = new Intent(Intent.ACTION_VIEW, marketUri);
        startActivity(i);
        finish();
    }

    @Override
    protected void init() {
        adjustMarginAndPadding();
        Glide.with(this)
                .load(R.drawable.bg_update)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(bgView);
    }
}