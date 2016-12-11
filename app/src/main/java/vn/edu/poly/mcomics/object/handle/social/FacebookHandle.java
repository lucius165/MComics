package vn.edu.poly.mcomics.object.handle.social;

import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import vn.edu.poly.mcomics.object.handle.eventlistener.DownloadEvent;
import vn.edu.poly.mcomics.object.handle.other.Show;
import vn.edu.poly.mcomics.object.variable.FaceBookComment;

/**
 * Created by lucius on 03/12/2016.
 */

public class FacebookHandle {
    public static final String COMMENTS = "comments";
    public static final String LIKES = "likes";
    public static final String SHAREDPOSTS = "sharedposts";

    public void getCount(String id, String objectName, final DownloadEvent downloadEvent) {
        Show.log("getCount.id", id);
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + id + "/" + objectName,
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Show.log("getCount.response", response.toString());
                        try {
                            int count = response.getJSONObject().getJSONArray("data").length();
                            Show.log("getCount", count + "");
                            downloadEvent.onLoadFinish(response.getJSONObject().getJSONArray("data").length() + "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    public ArrayList<FaceBookComment> getFbCommentList(JSONArray array) throws JSONException {
        ArrayList<FaceBookComment> list = new ArrayList<>();
        for (int x = 0; x < array.length(); x++) {
            JSONObject data = array.getJSONObject(x);
            JSONObject user = data.getJSONObject("from");
            list.add(new FaceBookComment(
                    data.getString("created_time"),
                    user.getString("id"),
                    user.getString("name"),
                    data.getString("message"),
                    data.getString("id")));
        }
        return list;
    }


}
