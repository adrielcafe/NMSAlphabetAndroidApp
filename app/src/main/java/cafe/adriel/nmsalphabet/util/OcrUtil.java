package cafe.adriel.nmsalphabet.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;

import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OcrUtil {

    private static final String OCR_SPACE_API_URL   = "https://api.ocr.space/parse/image";
    private static final String VISION_API_URL      = "https://vision.googleapis.com/v1/images:annotate?key=";
    private static final String VISION_API_BODY     =
            "{\"requests\": [{\"features\": [{\"type\": \"TEXT_DETECTION\"}],\"image\": {\"content\": \"%s\"}}]}";

    public static String extractTextFromImage(final Activity activity, Bitmap image){
        String text;
        try {
            if(App.isPro(activity)) {
                text = extractTextWithVisionApi(activity, image);
                if(text == null){
                    text = extractTextWithOcrSpaceApi(activity, image);
                }
            } else {
                text = extractTextWithOcrSpaceApi(activity, image);
            }
            if(text == null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, R.string.service_unavailable, Toast.LENGTH_SHORT).show();
                    }
                });
                AnalyticsUtil.ocrEvent(activity.getString(R.string.service_unavailable));
            } else {
                AnalyticsUtil.ocrEvent(text);
            }
            return text;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static String extractTextWithVisionApi(Context context, Bitmap image){
        String base64Img = Util.toBase64(image);
        String body = String.format(VISION_API_BODY, base64Img);
        try {
            RequestBody postBody = RequestBody.create(MediaType.parse(""), body);
            Request request = new Request.Builder()
                    .url(VISION_API_URL + context.getString(R.string.google_vision_key))
                    .post(postBody)
                    .header("Content-Type", "application/json")
                    .build();
            Response response = Util.getHttpClient().newCall(request).execute();
            if(response.isSuccessful()) {
                JSONObject json = new JSONObject(response.body().string());
                return json.getJSONArray("responses")
                        .getJSONObject(0)
                        .getJSONArray("textAnnotations")
                        .getJSONObject(0)
                        .getString("description")
                        .toUpperCase();
            } else {
                return null;
            }
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static String extractTextWithOcrSpaceApi(final Activity activity, Bitmap image){
        File imageFile = Util.toFile(activity, image, "alien_phrase_");
        try {
            RequestBody formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("apikey", activity.getString(R.string.ocr_space_key))
                    .addFormDataPart("file", imageFile.getName(), RequestBody.create(MediaType.parse("image/jpg"), imageFile))
                    .build();
            Request request = new Request.Builder()
                    .url(OCR_SPACE_API_URL)
                    .post(formBody)
                    .build();
            Response response = Util.getHttpClient().newCall(request).execute();
            if(response.isSuccessful()) {
                JSONObject json = new JSONObject(response.body().string());
                final String error = json.getString("ErrorMessage");
                if (Util.isEmpty(error)) {
                    return json.getJSONArray("ParsedResults")
                            .getJSONObject(0)
                            .getString("ParsedText")
                            .toUpperCase();
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}