package cafe.adriel.nmsalphabet.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
            settingsList.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if(scrollState == SCROLL_STATE_IDLE) {
                        updatePreferencies(settingsList);
                    }
                }
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) { }
            });
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()){
            case Constant.SETTINGS_ACCOUNT_STATUS:
                changeStatus();
                break;
            case Constant.SETTINGS_ACCOUNT_UPGRADE_PRO:
                upgradePro();
                break;
            case Constant.SETTINGS_ABOUT_NEW_RACE:
                sendNewRace();
                break;
            case Constant.SETTINGS_ABOUT_FEEDBACK:
                sendFeedback();
                break;
            case Constant.SETTINGS_ABOUT_TRANSLATORS:
                showTranslators();
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
        PreferenceCategory account = (PreferenceCategory) findPreference(Constant.SETTINGS_ACCOUNT);
        Preference accountStatus = findPreference(Constant.SETTINGS_ACCOUNT_STATUS);
        Preference accountUpgradePro = findPreference(Constant.SETTINGS_ACCOUNT_UPGRADE_PRO);
        Preference aboutNewRace = findPreference(Constant.SETTINGS_ABOUT_NEW_RACE);
        Preference aboutFeedback = findPreference(Constant.SETTINGS_ABOUT_FEEDBACK);
        Preference aboutTranslators = findPreference(Constant.SETTINGS_ABOUT_TRANSLATORS);
        Preference aboutShare = findPreference(Constant.SETTINGS_ABOUT_SHARE);
        Preference aboutRate = findPreference(Constant.SETTINGS_ABOUT_RATE);
        Preference aboutVersion = findPreference(Constant.SETTINGS_ABOUT_VERSION);

        accountStatus.setOnPreferenceClickListener(this);
        accountUpgradePro.setOnPreferenceClickListener(this);
        aboutNewRace.setOnPreferenceClickListener(this);
        aboutFeedback.setOnPreferenceClickListener(this);
        aboutTranslators.setOnPreferenceClickListener(this);
        aboutShare.setOnPreferenceClickListener(this);
        aboutRate.setOnPreferenceClickListener(this);

        if(App.isSignedIn()){
            accountStatus.setTitle(getString(R.string.connected_as) + " " + App.getUser().getName());
            accountStatus.setSummary(R.string.signout);
        } else {
            accountStatus.setTitle(R.string.signin);
            accountStatus.setSummary(R.string.signin_to_add_translations);
        }
        if(App.isPro(getContext())){
            account.removePreference(accountUpgradePro);
        }
        aboutVersion.setSummary(Util.getAppVersionName(getActivity()));

        String language = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Constant.SETTINGS_ACCOUNT_LANGUAGE, LanguageUtil.LANGUAGE_EN);
        accountLanguage.setSummary(getLanguageEntry(language));

        String theme = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Constant.SETTINGS_ACCOUNT_THEME, ThemeUtil.THEME_1);
        accountTheme.setSummary(getThemeEntry(theme));
    }

    private void updatePreferencies(ListView accountList){
        for(int i = 0; i < accountList.getChildCount(); i++) {
            try {
                LinearLayout rootLayout = (LinearLayout) accountList.getChildAt(i);
                RelativeLayout preferenceLayout = (RelativeLayout) rootLayout.getChildAt(1);
                TextView titleView = (TextView) preferenceLayout.getChildAt(0);
                TextView summaryView = (TextView) preferenceLayout.getChildAt(1);
                if(titleView.getText().toString().equals(getString(R.string.language))) {
                    summaryView.setCompoundDrawablePadding(10);
                    summaryView.setCompoundDrawablesRelativeWithIntrinsicBounds(LanguageUtil.getLanguageFlagDrawable(getActivity(),
                            LanguageUtil.getCurrentLanguageCode(getActivity())), null, null, null);
                } else if(titleView.getText().toString().equals(getString(R.string.theme))) {
                    summaryView.setText(ThemeUtil.getThemePreview(getActivity(), ThemeUtil.getCurrentTheme(getActivity())));
                    summaryView.setTextSize(30);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
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

    private void upgradePro(){
        Uri marketUri = Uri.parse(Constant.MARKET_URI + Util.getProPackageName(getContext()));
        Intent i = new Intent(Intent.ACTION_VIEW, marketUri);
        startActivity(i);
    }

    private void sendNewRace() {
        String subject;
        if(App.isSignedIn()) {
            subject = String.format("%s - New race discovered by %s [ID: %s]", getString(R.string.app_name), App.getUser().getName(), App.getUser().getObjectId());
        } else {
            subject = getString(R.string.app_name) + " - New race discovered";
        }
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + Constant.CONTACT_EMAIL));
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(new StringBuilder()
                .append("<b>Race Name: </b>")
                .append("<br>")
                .append("<b>System and planet where you find her: </b>")
                .append("<br>")
                .append("<b>Other info: </b>")
                .append("<br><br>")
                .append("<i>* You can attach a photo if you want</i>")
                .toString())
        );
        startActivity(Intent.createChooser(i, getContext().getString(R.string.feedback)));
    }

    private void showTranslators(){
        startActivity(new Intent(getContext(), TranslatorsActivity.class));
    }

    private void sendFeedback() {
        String subject;
        if(App.isSignedIn()) {
            subject = String.format("%s - Feedback from %s [ID: %s]", getString(R.string.app_name), App.getUser().getName(), App.getUser().getObjectId());
        } else {
            subject = getString(R.string.app_name) + " - Feedback";
        }
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + Constant.CONTACT_EMAIL));
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        startActivity(Intent.createChooser(i, getContext().getString(R.string.feedback)));
    }

    private void shareApp() {
        Util.shareText(getActivity(), getString(R.string.share_msg) + "\n" + Util.getGooglePlayUrl(getActivity()));
    }

    private void rateApp() {
        Uri marketUri = Uri.parse(Constant.MARKET_URI + Util.getPackageName(getActivity()));
        Intent i = new Intent(Intent.ACTION_VIEW, marketUri);
        startActivity(i);
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