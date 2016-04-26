package cafe.adriel.nmsalphabet.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;

import com.github.machinarius.preferencefragment.PreferenceFragment;

import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.Util;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private ListPreference accountLanguage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        init();
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View rootView = getView();
        if(rootView != null) {
            View settingsView = rootView.findViewById(android.R.id.list);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) settingsView.getLayoutParams();
            params.setMargins(60, 60, 60, 60);
            settingsView.setLayoutParams(params);
            settingsView.setBackgroundColor(getResources().getColor(R.color.bg_white));
            settingsView.setElevation(4);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()){
            case Constant.SETTINGS_ACCOUNT_STATUS:
                changeStatus();
                break;
            case Constant.SETTINGS_ABOUT_FEEDBACK:
                sendFeedback();
                break;
            case Constant.SETTINGS_ABOUT_SHARE:
                shareApp();
                break;
            case Constant.SETTINGS_ABOUT_RATE:
                rateApp();
                break;
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(getContext() != null) {
            switch (key) {
                case Constant.SETTINGS_ACCOUNT_LANGUAGE:
                    String language = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Constant.SETTINGS_ACCOUNT_LANGUAGE, "en");
                    accountLanguage.setSummary(getLanguageEntry(language));
                    Util.restartActivity(getActivity());
                    break;
            }
        }
    }

    private void init(){
        accountLanguage = (ListPreference) findPreference(Constant.SETTINGS_ACCOUNT_LANGUAGE);
        Preference accountStatus = findPreference(Constant.SETTINGS_ACCOUNT_STATUS);
        Preference aboutFeedback = findPreference(Constant.SETTINGS_ABOUT_FEEDBACK);
        Preference aboutShare = findPreference(Constant.SETTINGS_ABOUT_SHARE);
        Preference aboutRate = findPreference(Constant.SETTINGS_ABOUT_RATE);
        Preference aboutVersion = findPreference(Constant.SETTINGS_ABOUT_VERSION);

        accountStatus.setOnPreferenceClickListener(this);
        aboutFeedback.setOnPreferenceClickListener(this);
        aboutShare.setOnPreferenceClickListener(this);
        aboutRate.setOnPreferenceClickListener(this);
        aboutVersion.setOnPreferenceClickListener(this);

        if(App.isLoggedIn){
            accountStatus.setTitle(getString(R.string.connected_as) + " Adriel Café");
            accountStatus.setSummary(R.string.signout);
        } else {
            accountStatus.setTitle(R.string.signin);
            accountStatus.setSummary(R.string.signin_to_add_translations);
        }
        aboutVersion.setSummary(Util.getAppVersionName(getContext()));

        String language = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Constant.SETTINGS_ACCOUNT_LANGUAGE, "en");
        accountLanguage.setSummary(getLanguageEntry(language));
        accountLanguage.setDefaultValue(language);
    }

    private void changeStatus() {
        Activity activity = getActivity();
        if(activity != null) {
            if(App.isLoggedIn) {
                App.signOut(activity);
                startActivity(new Intent(activity, SplashActivity.class));
            } else {
                startActivity(new Intent(activity, SplashActivity.class));
            }
            activity.finish();
        }
    }

    // TODO missing user name and ID
    private void sendFeedback() {
//        Intent i = new Intent(Intent.ACTION_SEND);
//        i.putExtra(Intent.EXTRA_EMAIL, new String[] { Constant.CONTACT_EMAIL} );
//        i.putExtra(Intent.EXTRA_SUBJECT, String.format("Feedback from %s [ID: %s]", App.getUser().getName(), App.getUser().getObjectId()));
//        i.putExtra(Intent.EXTRA_TEXT, "");
//        i.setType("message/rfc822");
//        startActivity(Intent.createChooser(i, getString(R.string.feedback)));
    }

    private void shareApp() {
        Util.shareText(getContext(), getString(R.string.share_msg) + "\n" + Constant.GOOGLE_PLAY_URL);
    }

    private void rateApp() {
        Intent i = new Intent(Intent.ACTION_VIEW, Constant.MARKET_URI);
        startActivity(i);
    }

    private String getLanguageEntry(String value){
        switch (value){
            case "pt":
                return getString(R.string.portuguese);
            case "de":
                return getString(R.string.german);
            default:
                return getString(R.string.english);
        }
    }
}