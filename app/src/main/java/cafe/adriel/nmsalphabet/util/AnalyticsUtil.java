package cafe.adriel.nmsalphabet.util;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.crashlytics.android.answers.SignUpEvent;

import cafe.adriel.nmsalphabet.model.AlienWord;
import io.fabric.sdk.android.Fabric;

public class AnalyticsUtil {

    public static void signUpEvent(String network){
        if(isInitialized()) {
            Answers.getInstance().logSignUp(new SignUpEvent()
                    .putMethod(network)
                    .putSuccess(true));
        }
    }

    public static void signInEvent(String network){
        if(isInitialized()) {
            Answers.getInstance().logLogin(new LoginEvent()
                    .putMethod(network)
                    .putSuccess(true));
        }
    }

    public static void wordViewEvent(AlienWord word){
        if(isInitialized()) {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName(word.getClassName())
                    .putContentType("Word")
                    .putContentId(word.getObjectId()));
        }
    }

    public static void searchEvent(String word){
        if(isInitialized()) {
            Answers.getInstance().logSearch(new SearchEvent()
                    .putQuery(word));
        }
    }

    public static void useOcrEvent(String word){
        // TODO
    }

    public static void translateEvent(String word){
        // TODO
    }

    public static void addTranslationEvent(String word){
        // TODO
    }

    private static boolean isInitialized(){
        return Fabric.isInitialized() && Answers.getInstance() != null;
    }
}