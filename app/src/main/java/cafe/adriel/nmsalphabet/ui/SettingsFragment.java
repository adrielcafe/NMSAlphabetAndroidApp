package cafe.adriel.nmsalphabet.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.ui.adapter.ThemePreferenceAdapter;
import cafe.adriel.nmsalphabet.util.LanguageUtil;
import cafe.adriel.nmsalphabet.util.ThemeUtil;
import cafe.adriel.nmsalphabet.util.Util;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private ListPreference accountLanguage;
    private ThemePreferenceAdapter accountTheme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageUtil.updateLanguage(getActivity());
        addPreferencesFromResource(R.xml.settings);
        init();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout rootView = (LinearLayout) getView();
        if(rootView != null) {
            final ListView settingsList = (ListView) rootView.findViewById(android.R.id.list);
            settingsList.setBackgroundColor(getResources().getColor(R.color.bg_white));
            settingsList.post(new Runnable() {
                @Override
                public void run() {
                    updatePreferencies(settingsList);
                }
            });
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
        if(getActivity() != null) {
            switch (key) {
                case Constant.SETTINGS_ACCOUNT_LANGUAGE:
                    String language = sharedPreferences.getString(Constant.SETTINGS_ACCOUNT_LANGUAGE, LanguageUtil.LANGUAGE_EN);
                    changeLanguage(language);
                    break;
                case Constant.SETTINGS_ACCOUNT_THEME:
                    String theme = sharedPreferences.getString(Constant.SETTINGS_ACCOUNT_THEME, ThemeUtil.THEME_1);
                    changeTheme(theme);
                    break;
            }
        }
    }

    private void init(){
        accountLanguage = (ListPreference) findPreference(Constant.SETTINGS_ACCOUNT_LANGUAGE);
        accountTheme = (ThemePreferenceAdapter) findPreference(Constant.SETTINGS_ACCOUNT_THEME);
        Preference accountStatus = findPreference(Constant.SETTINGS_ACCOUNT_STATUS);
        Preference aboutFeedback = findPreference(Constant.SETTINGS_ABOUT_FEEDBACK);
        Preference aboutShare = findPreference(Constant.SETTINGS_ABOUT_SHARE);
        Preference aboutRate = findPreference(Constant.SETTINGS_ABOUT_RATE);
        Preference aboutVersion = findPreference(Constant.SETTINGS_ABOUT_VERSION);

        accountStatus.setOnPreferenceClickListener(this);
        aboutFeedback.setOnPreferenceClickListener(this);
        aboutShare.setOnPreferenceClickListener(this);
        aboutRate.setOnPreferenceClickListener(this);

        if(App.isSignedIn()){
            accountStatus.setTitle(getString(R.string.connected_as) + " " + App.getUser().getName());
            accountStatus.setSummary(R.string.signout);
        } else {
            accountStatus.setTitle(R.string.signin);
            accountStatus.setSummary(R.string.signin_to_add_translations);
        }
        aboutVersion.setSummary(Util.getAppVersionName(getActivity()));

        String language = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Constant.SETTINGS_ACCOUNT_LANGUAGE, LanguageUtil.LANGUAGE_EN);
        accountLanguage.setSummary(getLanguageEntry(language));

        String theme = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Constant.SETTINGS_ACCOUNT_THEME, ThemeUtil.THEME_1);
        accountTheme.setSummary(getThemeEntry(theme));
    }

    private void updatePreferencies(ListView accountList){
        try {
            LinearLayout languageLayout = (LinearLayout) accountList.getChildAt(2);
            RelativeLayout summaryLayout = (RelativeLayout) languageLayout.getChildAt(1);
            TextView summaryView = (TextView) summaryLayout.getChildAt(1);
            summaryView.setCompoundDrawablePadding(10);
            summaryView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    LanguageUtil.getLanguageFlagDrawable(getActivity(), LanguageUtil.getCurrentLanguage(getActivity())), null, null, null);
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            LinearLayout themeLayout = (LinearLayout) accountList.getChildAt(3);
            RelativeLayout summaryLayout = (RelativeLayout) themeLayout.getChildAt(1);
            TextView summaryView = (TextView) summaryLayout.getChildAt(1);
            summaryView.setText(ThemeUtil.getThemeCircles(getActivity(), ThemeUtil.getCurrentTheme(getActivity())));
            summaryView.setTextSize(30);
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            LinearLayout versionLayout = (LinearLayout) accountList.getChildAt(8);
            versionLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showQuote();
                    return true;
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void changeStatus() {
        Activity activity = getActivity();
        if(activity != null) {
            App.signOut(activity);
            startActivity(new Intent(activity, SplashActivity.class));
            activity.finish();
            if(MainActivity.getInstance() != null){
                MainActivity.getInstance().finish();
            }
        }
    }

    private void changeLanguage(String language){
        accountLanguage.setSummary(getLanguageEntry(language));
        Util.restartActivity(MainActivity.getInstance());
        Util.restartActivity(getActivity());
    }

    private void changeTheme(String theme){
        accountTheme.setSummary(getThemeEntry(theme));
        Util.restartActivity(MainActivity.getInstance());
        Util.restartActivity(getActivity());
    }

    private void sendFeedback() {
        String subject = String.format("Feedback from %s [ID: %s]", App.getUser().getName(), App.getUser().getObjectId());
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + Constant.CONTACT_EMAIL));
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        getActivity().startActivity(Intent.createChooser(i, getContext().getString(R.string.feedback)));
    }

    private void shareApp() {
        Util.shareText(getActivity(), getString(R.string.share_msg) + "\n" + Util.getGooglePlayUrl(getActivity()));
    }

    private void rateApp() {
        Uri marketUri = Uri.parse(Constant.MARKET_URI + Util.getPackageName(getActivity()));
        Intent i = new Intent(Intent.ACTION_VIEW, marketUri);
        startActivity(i);
    }

    private void showQuote(){
        Toast.makeText(getActivity(), R.string.quote, Toast.LENGTH_SHORT).show();
    }

    private String getLanguageEntry(String value){
        switch (value){
            case LanguageUtil.LANGUAGE_PT:
                return getString(R.string.portuguese);
            case LanguageUtil.LANGUAGE_DE:
                return getString(R.string.german);
            default:
                return getString(R.string.english);
        }
    }

    private String getThemeEntry(String value){
        switch (value){
            case ThemeUtil.THEME_2:
                return getString(R.string.theme2);
            case ThemeUtil.THEME_3:
                return getString(R.string.theme3);
            case ThemeUtil.THEME_4:
                return getString(R.string.theme4);
            case ThemeUtil.THEME_5:
                return getString(R.string.theme5);
            default:
                return getString(R.string.theme1);
        }
    }

}