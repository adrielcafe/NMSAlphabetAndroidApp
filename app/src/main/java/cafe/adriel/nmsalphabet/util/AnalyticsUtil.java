package cafe.adriel.nmsalphabet.util;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.crashlytics.android.answers.SignUpEvent;

import cafe.adriel.nmsalphabet.model.AlienWord;

public class AnalyticsUtil {

    public static void eventSignUp(String network){
        Answers.getInstance().logSignUp(new SignUpEvent()
                .putMethod(network)
                .putSuccess(true));
    }

    public static void eventSignIn(String network){
        Answers.getInstance().logLogin(new LoginEvent()
                .putMethod(network)
                .putSuccess(true));
    }

    public static void eventWordView(AlienWord word){
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName(word.getClassName())
                .putContentType("Word")
                .putContentId(word.getObjectId()));
    }

    public static void eventSearch(String word){
        Answers.getInstance().logSearch(new SearchEvent()
                .putQuery(word));
    }

    public static void eventTranslate(String word){
        // TODO
    }

    public static void eventAddTranslation(String word){
        // TODO
    }

}