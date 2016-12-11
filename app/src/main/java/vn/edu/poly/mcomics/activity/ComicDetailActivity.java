package vn.edu.poly.mcomics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import vn.edu.poly.mcomics.R;
import vn.edu.poly.mcomics.object.handle.backgroundtask.CheckInternet;
import vn.edu.poly.mcomics.object.handle.backgroundtask.LoadJsonInBackground;
import vn.edu.poly.mcomics.object.handle.eventlistener.DownloadEvent;
import vn.edu.poly.mcomics.object.handle.json.ParserJSON;
import vn.edu.poly.mcomics.object.handle.other.NavigationDrawer;
import vn.edu.poly.mcomics.object.handle.other.Show;
import vn.edu.poly.mcomics.object.handle.social.FacebookAPI;
import vn.edu.poly.mcomics.object.handle.social.FacebookHandle;
import vn.edu.poly.mcomics.object.variable.Comics;
import vn.edu.poly.mcomics.object.variable.FacebookContent;

public class ComicDetailActivity extends AppCompatActivity implements DownloadEvent {
    private Button btn_openComics;
    private FacebookAPI facebookAPI;
    private boolean isShow;
    private TextView txv_readMoreTop, txv_readMoreBottom, txv_review;
    private NavigationDrawer navigationDrawer;
    private String id;
    private Comics comics;
    private LinearLayout ll_social, ll_login;
    private TextView like, share, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!CheckInternet.check(this)) {
            setContentView(R.layout.view_connect_fail);
            return;
        }
        facebookAPI = new FacebookAPI(this);
        facebookAPI.init();
        setContentView(R.layout.view_navigation);
        navigationDrawer = new NavigationDrawer(this, R.layout.activity_comics_detail, (ViewGroup) findViewById(R.id.root).getParent());
        getView();

        id = getIntent().getExtras().getString("id");
        Show.log("id", id);

        LoadJsonInBackground loadJson = new LoadJsonInBackground();
        loadJson.setOnFinishEvent(this);
        loadJson.execute("http://grayguy.xyz/?kind=comics_detail&id=" + id);

        btn_openComics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getBaseContext(), ComicChaptersActivity.class);
                intent2.putExtra("id", id);
                startActivity(intent2);
            }
        });

        txv_readMoreBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHideReview();
            }
        });
        createSocial();
    }

    public void setLoginButton() {
        facebookAPI.createLoginButton(
                ((LoginButton) findViewById(R.id.loginButton)),
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Show.toastSHORT(getBaseContext(), "Đăng nhập thành công");
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Show.toastSHORT(getBaseContext(), "Đăng nhập thất bại. Hãy thử lại sau.");
                    }
                }
        );
    }

    public void mappingsSocial() {
        ll_social = ((LinearLayout) findViewById(R.id.ll_social));
        ll_login = ((LinearLayout) findViewById(R.id.ll_login));
        like = (TextView) findViewById(R.id.like);
        comment = (TextView) findViewById(R.id.comment);
        share = (TextView) findViewById(R.id.share);
    }

    public void createSocial() {
        if (!facebookAPI.isLogged()) {
            setLoginButton();
            return;
        } else {
            mappingsSocial();
            ll_social.setVisibility(View.VISIBLE);
            ll_login.setVisibility(View.GONE);
        }
        LoadJsonInBackground loadJson = new LoadJsonInBackground();
        loadJson.setOnFinishEvent(new DownloadEvent() {
            @Override
            public void onLoadFinish(String string) {
                try {
                    enableSocial(new ParserJSON().getFacebookContentInfo(string));
                } catch (Exception e) {
                    disableSocial();
                    return;
                }
            }
        });
        loadJson.execute("http://grayguy.xyz/?kind=fb_content_info&id=" + id);
    }

    public void enableSocial(final FacebookContent fbInfo) {
        ll_social.setAlpha(1);
        setButtonClickable(like, share, comment, true);
        setLikesButton(fbInfo);
        setCommentsButton(fbInfo);
        setSharesButton(fbInfo);
        setSendComment(fbInfo);
    }

    public void disableSocial() {
        ll_social.setAlpha(0.7f);
        setButtonClickable(like, share, comment, false);
    }

    public void setButtonClickable(TextView like, TextView share, TextView comment, boolean value) {
        like.setClickable(value);
        comment.setClickable(value);
        share.setClickable(value);
    }

    public void setLikesButton(final FacebookContent fbInfo) {
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.scale));
                facebookAPI.showLikeDialog(fbInfo);
            }
        });
    }

    public void setSharesButton(final FacebookContent fb) {
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.scale));
                facebookAPI.showShareDialog(fb);
            }
        });
    }

    public void setCommentsButton(FacebookContent fbInfo) {
        facebookAPI.showCount(fbInfo.getFbLongId(), FacebookHandle.COMMENTS, comment);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.scale));
                LinearLayout ll_comment = ((LinearLayout) findViewById(R.id.ll_comment));
                LinearLayout ll_review = ((LinearLayout) findViewById(R.id.ll_review));
                EditText editText = (EditText) findViewById(R.id.edtx_input);
                if (ll_review.getVisibility() == View.VISIBLE) {
                    ll_review.setVisibility(View.GONE);
                    ll_comment.setVisibility(View.VISIBLE);
                    editText.setEnabled(true);
                } else {
                    ll_comment.setVisibility(View.GONE);
                    ll_review.setVisibility(View.VISIBLE);
                    editText.setEnabled(false);
                }
            }
        });
    }

    public void setSendComment(final FacebookContent fbInfo) {
        facebookAPI.showFbCommentList(fbInfo.getFbLongId(), R.id.ll_commentList);
        final TextView btn_send = ((TextView) findViewById(R.id.txv_send));
        final EditText input = ((EditText) findViewById(R.id.edtx_input));
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookAPI.comment(fbInfo, input.getText().toString(), comment);
                input.setText("");
            }
        });
    }

    public void getView() {
        btn_openComics = (Button) findViewById(R.id.btnDoctruyen);
        txv_review = (TextView) findViewById(R.id.txv_review);
        txv_readMoreTop = (TextView) findViewById(R.id.txv_readMoreTop);
        txv_readMoreBottom = (TextView) findViewById(R.id.txv_readMoreBottom);
    }

    public void showHideReview() {
        if (isShow = !isShow) {
            show();
        } else {
            hide();
        }
    }

    public void show() {
        txv_review.setMaxLines(Integer.MAX_VALUE);
        txv_readMoreBottom.setText("Ẩn");
    }

    public void hide() {
        txv_review.setMaxLines(3);
        txv_readMoreBottom.setText("Đọc thêm");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookAPI.onActivityResult(requestCode, resultCode, data);
        navigationDrawer.onActivityResult(requestCode, resultCode, data);
        createSocial();
    }

    @Override
    public void onLoadFinish(String string) {
        try {
            comics = new ParserJSON().getComic(new JSONArray(string).getJSONObject(0));
            Picasso.with(this).load(comics.getThumbnail()).resize(400, 600).error(R.mipmap.bia).into(((ImageView) findViewById(R.id.imv_cover)));
            ((TextView) findViewById(R.id.txv_name)).setText(comics.getComicsName());
            ((TextView) findViewById(R.id.txv_author)).setText(comics.getAuthor());
            ((TextView) findViewById(R.id.txv_kinds)).setText(comics.getKind());
            ((TextView) findViewById(R.id.txv_chapter)).setText(comics.getEpisodes());
            ((TextView) findViewById(R.id.txv_views)).setText(comics.getViews() + "");
            ((TextView) findViewById(R.id.txv_review)).setText(comics.getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
