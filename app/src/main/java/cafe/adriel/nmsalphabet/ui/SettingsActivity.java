package cafe.adriel.nmsalphabet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.OnLongClick;
import cafe.adriel.nmsalphabet.R;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

    @OnLongClick(R.id.code_with_love)
    public boolean showVersionActivity(){
        startActivity(new Intent(SettingsActivity.this, VersionActivity.class));
        return true;
    }

    @Override
    protected void init() {
        adjustMarginAndPadding();
    }
}