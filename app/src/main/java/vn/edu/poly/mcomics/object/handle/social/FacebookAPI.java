package vn.edu.poly.mcomics.object.handle.social;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.internal.LikeContent;
import com.facebook.share.internal.LikeDialog;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.Arrays;

import vn.edu.poly.mcomics.R;
import vn.edu.poly.mcomics.object.handle.eventlistener.DownloadEvent;
import vn.edu.poly.mcomics.object.handle.other.Show;
import vn.edu.poly.mcomics.object.variable.FaceBookComment;
import vn.edu.poly.mcomics.object.variable.FacebookContent;

/**
 * Created by lucius on 30/11/2016.
 */

public class FacebookAPI {
    private Activity activity;
    private CallbackManager callbackManager;
    private FacebookHandle fbHandle;
    private String access_token = "EAAYLZBLbKcAkBAKwVCsqkXCRUKFI70f9J9Wh5mNkyctLjqLas8mCZC26j9g5DpJQ2vX1VdipXnMQRDqBpLeeHL1uj4ecZB8jTSNxIqtdhkswDlRoUlXINCflt3oTXOrCkuzlrV1ae7se3maQjZAf3asKnnWVhpZBTM4ZADFLnGDwZDZD";

    public FacebookAPI(Activity activity) {
        this.activity = activity;
        fbHandle = new FacebookHandle();
    }

    public void init() {
        FacebookSdk.sdkInitialize(activity);
        AppEventsLogger.activateApp(activity);
        callbackManager = CallbackManager.Factory.create();
    }

    public void createLoginButton(LoginButton loginButton, FacebookCallback<LoginResult> facebookCallback) {
        loginButton.setPublishPermissions(Arrays.asList(new String[]{
                "manage_pages",
                "publish_pages",
                "publish_actions"}));
        loginButton.registerCallback(callbackManager, facebookCallback);
    }

    public void showLikeDialog(final FacebookContent fb) {
        LikeDialog likeDialog = new LikeDialog(activity);
        likeDialog.registerCallback(callbackManager, new FacebookCallback<LikeDialog.Result>() {
            @Override
            public void onSuccess(LikeDialog.Result result) {
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
            }
        });

        LikeContent likeContent = new LikeContent.Builder().setObjectId(fb.getFb_link()).setObjectType(null).build();
        likeDialog.show(likeContent);
    }

    public void showShareDialog(final FacebookContent fb) {
        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Show.toastSHORT(activity, "Chia sẻ thành công.");
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Show.toastSHORT(activity, "Chia sẻ thất bại.");
            }
        });
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Hi there!")
                    .setContentDescription("MComics - app đọc truyện online trên smartphone")
                    .setContentUrl(Uri.parse(fb.getFb_link()))
                    .build();
            shareDialog.show(linkContent);
        }
    }

    @Deprecated
    public void like(final String objectId, final TextView like) {
        Bundle params = new Bundle();
        params.putString("access_token", access_token);
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + objectId + "/likes", params, HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Show.log("like.response", response.toString());
                        if (response.toString().indexOf("\"success\":true") != -1) {
                            showCount(objectId, FacebookHandle.LIKES, like);
                        } else {
                            Show.toastSHORT(activity, "Lỗi");
                        }
                    }
                }
        ).executeAsync();
    }

    @Deprecated
    public void share(final String objectId, String status) {
        GraphRequest request = GraphRequest.newPostRequest(AccessToken.getCurrentAccessToken(), "me/feed", null, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                Show.log("share.response", response.toString());
                if (response.getError() == null) {
                    Show.toastSHORT(activity, "Chia sẻ thành công!");
                } else Toast.makeText(activity, "Chia sẻ không thành công!", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("message", status);
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void comment(final FacebookContent fbInfo, String comment, final TextView commentView) {
        Bundle params = new Bundle();
        params.putString("message", comment);
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + fbInfo.getFbLongId() + "/comments", params, HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            Show.log("comment.GraphResponse", response.toString());
                            showCount(fbInfo.getFbLongId(), FacebookHandle.COMMENTS, commentView);
                            showFbCommentList(fbInfo.getFbLongId(), R.id.ll_commentList);
                        } catch (Exception e) {
                            Log.e("send", response.toString());
                        }
                    }
                }
        ).executeAsync();
    }

    public void showFbCommentList(String objectId, final int view) {
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + objectId + "/comments", null, HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Show.log("showFbCommentList.response", response.toString());
                        try {
                            ArrayList<FaceBookComment> list = fbHandle.getFbCommentList(response.getJSONObject().getJSONArray("data"));
                            ViewGroup parent = (ViewGroup) activity.findViewById(view);
                            parent.removeAllViews();
                            for (int x = 0; x < list.size(); x++) {
                                FaceBookComment temp = list.get(x);
                                View cmt = ((LayoutInflater.from(activity)).inflate(R.layout.view_comment, parent, false));
                                ((TextView) cmt.findViewById(R.id.name)).setText(temp.getUserName());
                                ((TextView) cmt.findViewById(R.id.txv_comment)).setText(temp.getUserMessage());
                                ((TextView) cmt.findViewById(R.id.txv_time)).setText(temp.getTime().substring(0, temp.getTime().indexOf("+")));
                                parent.addView(cmt, x);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    public void showCount(String objectId, final String action, final TextView textView) {
        fbHandle.getCount(objectId, action, new DownloadEvent() {
            @Override
            public void onLoadFinish(String string) {
                if (Integer.valueOf(string) == 0) {
                    return;
                }
                String name;
                if (action.equals(FacebookHandle.LIKES)) {
                    name = "Liked";
                } else {
                    name = "Commented";
                }
                textView.setText(name + " (" + string + ")");
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            }
        });
    }

    public boolean isLogged() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
