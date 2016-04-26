package cafe.adriel.nmsalphabet;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;

public class App extends Application {

    public static boolean isLoggedIn = true;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(getString(R.string.parse_app_id))
                .server("http://nmsalphabet.herokuapp.com/api/")
                .build());
    }

    public static void signIn(Context context){

    }

    public static void signOut(Context context){

    }

}