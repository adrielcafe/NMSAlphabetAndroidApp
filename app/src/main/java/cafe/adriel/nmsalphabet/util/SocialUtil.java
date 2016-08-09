package cafe.adriel.nmsalphabet.util;

import android.content.Context;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

import org.json.JSONObject;

import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.Constant;

public class SocialUtil {

    public static void logOut(){
        if(AccessToken.getCurrentAccessToken() != null) {
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {
                    LoginManager.getInstance().logOut();
                }
            }).executeAsync();
        }
    }

    public static String getUserImageUrl(Context context){
        String facebookUserId = Util.getSettings(context).getString(Constant.SETTINGS_FACEBOOK_USER_ID, "");
        return String.format(Constant.FACEBOOK_USER_IMAGE_URL, facebookUserId);
    }

    public static void updateFacebookProfile(final Context context){
        Bundle params = new Bundle();
        params.putString("fields", "name, gender");
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String name = object.getString("name");
                    String gender = object.getString("gender");
                    App.getUser().setName(name);
                    App.getUser().setGender(Util.isEmpty(gender) ? Constant.GENDER_MALE : gender);
                    App.getUser().saveInBackground();
                    Util.getSettings(context).edit()
                            .putString(Constant.SETTINGS_FACEBOOK_USER_ID, AccessToken.getCurrentAccessToken().getUserId())
                            .apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        request.setParameters(params);
        request.executeAndWait();
    }

    public static void updateFabricProfile(){
        if(AnalyticsUtil.isInitialized()) {
            Crashlytics.setUserIdentifier(App.getUser().getObjectId());
            Crashlytics.setUserName(App.getUser().getName());
        }
    }

}