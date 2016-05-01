package cafe.adriel.nmsalphabet;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.tsengvn.typekit.Typekit;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.SubscriberExceptionEvent;

import java.util.ArrayList;
import java.util.List;

import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.model.User;
import cafe.adriel.nmsalphabet.util.DbUtil;
import cafe.adriel.nmsalphabet.util.Util;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import io.fabric.sdk.android.Fabric;

public class App extends Application {

    private static List<AlienRace> races;
    private static User user;

    @Override
    public void onCreate() {
        super.onCreate();
        CustomActivityOnCrash.install(this);
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "fonts/LatoLatin-Regular.ttf"))
                .addBold(Typekit.createFromAsset(this, "fonts/LatoLatin-Bold.ttf"))
                .addItalic(Typekit.createFromAsset(this, "fonts/LatoLatin-Italic.ttf"))
                .addBoldItalic(Typekit.createFromAsset(this, "fonts/LatoLatin-BoldItalic.ttf"))
                .addCustom1(Typekit.createFromAsset(this, "fonts/Geomanist-Regular.otf"));
        EventBus.getDefault().register(this);
        initFabric();
        initParse();
        initFacebook();

//        Util.printAppKeyHash(this);
    }

    @Subscribe(sticky = true)
    public void onEvent(SubscriberExceptionEvent event){
        if(event != null){
            event.throwable.printStackTrace();
        }
    }

    private void initFabric(){
        Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics(), new Answers())
                .debuggable(true)
                .build();
        Fabric.with(fabric);
    }

    private void initParse(){
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(AlienRace.class);
        ParseObject.registerSubclass(AlienWord.class);
        ParseObject.registerSubclass(AlienWordTranslation.class);
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(getString(R.string.parse_app_id))
                .server(Constant.PARSE_SERVER_URL)
                .build());
        Parse.setLogLevel(BuildConfig.DEBUG ? Parse.LOG_LEVEL_VERBOSE : Parse.LOG_LEVEL_NONE);
    }

    private void initFacebook(){
        ParseFacebookUtils.initialize(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        AppEventsLogger.activateApp(this);
    }

    public static void signOut(Context context){
        ParseUser.logOut();
        Util.getSettings(context).edit().clear().commit();
        user = null;
    }

    public static boolean isSignedIn(){
        return getUser() != null;
    }

    public static User getUser(){
        if(user == null){
            user = (User) ParseUser.getCurrentUser();
        }
        return user;
    }

    public static List<AlienRace> getRaces(){
        return races;
    }

    public static List<String> getRacesName(){
        List<String> racesName = new ArrayList<>();
        for(AlienRace race : races){
            racesName.add(race.getName());
        }
        return racesName;
    }

    public static AlienRace getRace(String name){
        for(AlienRace race : races){
            if(race.getName().toUpperCase().equals(name.toUpperCase())){
                return race;
            }
        }
        return null;
    }

    public static void loadRacesWordsAndTranslations(){
        races = DbUtil.getRaces();
    }
}