package cafe.adriel.nmsalphabet;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.model.User;

public class App extends Application {

    public static boolean isLoggedIn = true;

    @Override
    public void onCreate() {
        super.onCreate();
        initParse();
    }

    public static void signIn(Context context){

    }

    public static void signOut(Context context){

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