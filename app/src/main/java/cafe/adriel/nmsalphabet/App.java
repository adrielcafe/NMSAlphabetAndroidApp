package cafe.adriel.nmsalphabet;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.parse.Parse;
import com.parse.ParseObject;
import com.tsengvn.typekit.Typekit;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.SubscriberExceptionEvent;

import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.model.User;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import io.fabric.sdk.android.Fabric;

public class App extends Application {

    public static boolean isLoggedIn = true;

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
        initFabric();
        initParse();
    }

    @Subscribe(sticky = true)
    public void onEvent(SubscriberExceptionEvent event){
        if(event != null){
            event.throwable.printStackTrace();
        }
    }

    public static void signIn(Context context){

    }

    public static void signOut(Context context){

    }

    private void initFabric(){
        Fabric.with(this,
                new Crashlytics(),
                new Answers());
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
    }

}