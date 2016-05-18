package cafe.adriel.nmsalphabet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.R;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.content_layout)
    FrameLayout contentLayout;
    @BindView(R.id.code_with_love)
    TextView codeWithLoveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.settings);
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    protected void init() {
        adjustMarginAndPadding();
        codeWithLoveView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(SettingsActivity.this, VersionActivity.class));
                return true;
            }
        });
    }
}