package cafe.adriel.nmsalphabet.util;

import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONObject;

import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.Constant;

public class SocialUtil {

    public static String getUserImageUrl(){
        return String.format(Constant.FACEBOOK_USER_IMAGE_URL, App.getUser().getFacebookUserId());
    }

    public static void updateFacebookProfile(){
        Bundle params = new Bundle();
        params.putString("fields", "name, gender, locale");
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String name = object.getString("name");
                    App.getUser().setFacebookUserId(AccessToken.getCurrentAccessToken().getUserId());
                    App.getUser().setName(name);
                    App.getUser().saveEventually();
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                }
            }
        });
        request.setParameters(params);
        request.executeAndWait();
    }

    public static void updateFabricProfile(){
        Crashlytics.setUserIdentifier(App.getUser().getObjectId());
        Crashlytics.setUserName(App.getUser().getName());
    }

}