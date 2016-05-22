package cafe.adriel.nmsalphabet.util;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.crashlytics.android.answers.ShareEvent;
import com.crashlytics.android.answers.SignUpEvent;

import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.model.AlienWordTranslation;
import cafe.adriel.nmsalphabet.model.User;
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

    public static void wordViewEvent(AlienRace race, AlienWord word){
        if(isInitialized()) {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentId(word.getObjectId())
                    .putContentName(word.getWord())
                    .putContentType(word.getClass().getSimpleName())
                    .putCustomAttribute("race", race == null ? "" : race.getName()));
        }
    }

    public static void searchEvent(AlienRace race, String word){
        if(isInitialized()) {
            Answers.getInstance().logSearch(new SearchEvent()
                    .putQuery(word)
                    .putCustomAttribute("race", race == null ? "ALL" : race.getName()));
        }
    }

    public static void shareEvent(AlienWord word){
        if(isInitialized()) {
            Answers.getInstance().logShare(new ShareEvent()
                    .putContentId(word.getObjectId())
                    .putContentName(word.getWord()));
        }
    }

    public static void ocrEvent(String phrase){
        if(isInitialized()) {
            Answers.getInstance().logCustom(new CustomEvent("ocr")
                    .putCustomAttribute("phrase", phrase));
        }
    }

    public static void translateEvent(AlienRace race, String phrase){
        if(isInitialized()) {
            Answers.getInstance().logCustom(new CustomEvent("translate")
                    .putCustomAttribute("race", race == null ? "" : race.getName())
                    .putCustomAttribute("phrase", phrase));
        }
    }

    public static void addTranslationEvent(AlienRace race, AlienWord word){
        if(isInitialized()) {
            Answers.getInstance().logCustom(new CustomEvent("addTranslation")
                    .putCustomAttribute("race", race == null ? "" : race.getName())
                    .putCustomAttribute("word", word.getWord()));
        }
    }

    public static void editTranslationEvent(AlienRace race, AlienWord word){
        if(isInitialized()) {
            Answers.getInstance().logCustom(new CustomEvent("editTranslation")
                    .putCustomAttribute("race", race == null ? "" : race.getName())
                    .putCustomAttribute("word", word.getWord()));
        }
    }

    public static void deleteTranslationEvent(AlienRace race, AlienWord word){
        if(isInitialized()) {
            Answers.getInstance().logCustom(new CustomEvent("deleteTranslation")
                    .putCustomAttribute("race", race == null ? "" : race.getName())
                    .putCustomAttribute("word", word.getWord()));
        }
    }

    public static void likeEvent(AlienRace race, AlienWord word, AlienWordTranslation translation){
        if(isInitialized()) {
            Answers.getInstance().logCustom(new CustomEvent("like")
                    .putCustomAttribute("race", race == null ? "" : race.getName())
                    .putCustomAttribute("word", word.getWord())
                    .putCustomAttribute("translation", translation.getTranslation()));
        }
    }

    public static void dislikeEvent(AlienRace race, AlienWord word, AlienWordTranslation translation){
        if(isInitialized()) {
            Answers.getInstance().logCustom(new CustomEvent("dislike")
                    .putCustomAttribute("race", race == null ? "" : race.getName())
                    .putCustomAttribute("word", word.getWord())
                    .putCustomAttribute("translation", translation.getTranslation()));
        }
    }

    public static void sawEastereggEvent(User user){
        if(isInitialized()) {
            Answers.getInstance().logCustom(new CustomEvent("sawEasteregg")
                    .putCustomAttribute("userId", user.getObjectId()));
        }
    }

    public static boolean isInitialized(){
        return Fabric.isInitialized() && Answers.getInstance() != null;
    }

}