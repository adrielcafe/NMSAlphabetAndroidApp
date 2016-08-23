package cafe.adriel.nmsalphabet.util;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import cafe.adriel.nmsalphabet.model.AlienRace;
import io.fabric.sdk.android.Fabric;

public class AnalyticsUtil {

    public static void ocrEvent(String phrase){
        if(isInitialized()) {
            Answers.getInstance().logCustom(new CustomEvent("ocr")
                    .putCustomAttribute("phrase", phrase));
        }
    }

    public static void translateEvent(String race, String phrase){
        if(isInitialized()) {
            Answers.getInstance().logCustom(new CustomEvent("translate")
                    .putCustomAttribute("race", race == null ? "" : race)
                    .putCustomAttribute("phrase", phrase));
        }
    }

    public static void sawEastereggEvent(){
        if(isInitialized()) {
            Answers.getInstance().logCustom(new CustomEvent("sawEasteregg"));
        }
    }

    public static boolean isInitialized(){
        return Fabric.isInitialized() && Answers.getInstance() != null;
    }

}