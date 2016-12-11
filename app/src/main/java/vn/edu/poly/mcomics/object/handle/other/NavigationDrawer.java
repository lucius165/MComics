package vn.edu.poly.mcomics.object.handle.other;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.widget.ShareDialog;

import vn.edu.poly.mcomics.R;
import vn.edu.poly.mcomics.object.handle.social.FacebookAPI;
import vn.edu.poly.mcomics.object.variable.FacebookContent;

/**
 * Created by lucius on 11/16/16.
 */

public class NavigationDrawer implements View.OnClickListener {
    private LayoutInflater inflater;
    private ViewGroup parent;
    private View mainView;
    private Activity activity;
    private Dialog brightnessDialog, viewModeDialog;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private FacebookAPI facebookAPI;
    private LoginButton loginButton;
    private TextView txv_log;
    private LinearLayout btn_vertical, btn_horizontal;
    private SettingHandle settingHandle;
    protected SeekBar seekBar;
    private long firstClickTime = -1, secondClickTime = -1;
    private Dialog dialog;

    private final int REQUEST_CODE = 200;
    private TableRow btn_likes, btn_shares, btn_log, btn_brightness, btn_viewMode, btn_exit;
    private final float ONE_PERCENT = 255f / 100f;

    public NavigationDrawer(final Activity activity, int layout, ViewGroup viewGroup) {
        this.activity = activity;
        this.inflater = (LayoutInflater.from(activity));
        this.parent = viewGroup;
        mappings(layout);

        ((LinearLayout) mainView).addView(toolbar, 0);
        ((FrameLayout) parent.findViewById(R.id.root)).addView(mainView);
        dialog = new Dialog(activity);
        showDialog(R.id.btn_info, R.layout.view_information_app);
        createFbLoginButton();
        setButtonOnClick();
        createBrightnessDialog();
        createViewModeDialog();
        doubleClickExitButton();
        new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                checkLogin();
            }
        };

        createNavigationButton();
    }

    public void mappings(int layout) {
        facebookAPI = new FacebookAPI(activity);
        facebookAPI.init();
        toolbar = (Toolbar) inflater.inflate(R.layout.view_toolbar, ((ViewGroup) mainView), false).findViewById(R.id.toolBar);
        drawerLayout = ((DrawerLayout) parent);
        mainView = (inflater.inflate(layout, parent, false));
        loginButton = (LoginButton) parent.findViewById(R.id.fb_login);
        btn_likes = (TableRow) parent.findViewById(R.id.btn_like);
        btn_shares = (TableRow) parent.findViewById(R.id.btn_share);
        btn_log = (TableRow) parent.findViewById(R.id.btn_log);
        btn_brightness = (TableRow) parent.findViewById(R.id.btn_brightness);
        btn_viewMode = (TableRow) parent.findViewById(R.id.btn_viewMode);
        txv_log = (TextView) parent.findViewById(R.id.txv_log);
        settingHandle = new SettingHandle(activity);
    }

    public void showDialog(int id, final int layout) {
        ImageView thongTin = (ImageView) parent.findViewById(id);
        thongTin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                dialog.setContentView(layout);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.show();
            }
        });

    }

    public void doubleClickExitButton() {
        ImageView btn_Exit = (ImageView) parent.findViewById(R.id.btn_exit);
        btn_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstClickTime == -1) {
                    Show.toastSHORT(activity, "Nhấn 2 lần để thoát");
                    firstClickTime = System.currentTimeMillis();
                } else if ((firstClickTime != -1) && ((secondClickTime = System.currentTimeMillis()) - firstClickTime) <= 2000) {
                    System.exit(0);
                } else if ((firstClickTime != -1) && ((secondClickTime = System.currentTimeMillis()) - firstClickTime) > 2000) {
                    Show.toastSHORT(activity, "Nhấn 2 lần để thoát");
                    firstClickTime = System.currentTimeMillis();
                }
            }
        });
    }

    public void setButtonOnClick() {
        btn_likes.setOnClickListener(this);
        btn_shares.setOnClickListener(this);
        btn_log.setOnClickListener(this);
        btn_brightness.setOnClickListener(this);
        btn_viewMode.setOnClickListener(this);
    }

    public void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
                if (Settings.System.canWrite(activity)) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_SETTINGS}, REQUEST_CODE);
                } else {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + activity.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                }
            }
        }
    }

    public int getScreenWidth() {
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return point.x;
    }

    public int getScreenHeight() {
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return point.y;
    }

    public void checkLogin() {
        if (facebookAPI.isLogged()) {
            btn_likes.setAlpha(1);
            btn_shares.setAlpha(1);
            txv_log.setText("Đăng xuất");
        } else {
            btn_likes.setAlpha(0.7f);
            btn_shares.setAlpha(0.7f);
            txv_log.setText("Đăng Nhập");
        }
    }

    public void createBrightnessDialog() {
        getPermission();

        brightnessDialog = new Dialog(activity);
        brightnessDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        brightnessDialog.setContentView(R.layout.view_dialog_brightness);

        ((LinearLayout) brightnessDialog.findViewById(R.id.dialog_brightness)).getLayoutParams().width = (int) (getScreenWidth() * 0.90);

        seekBar = (SeekBar) brightnessDialog.findViewById(R.id.sbBrightness);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
                ((TextView) brightnessDialog.findViewById(R.id.txv_currentBrightness)).setText((int) (progress / ONE_PERCENT) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void createViewModeDialog() {
        viewModeDialog = new Dialog(activity);
        viewModeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        viewModeDialog.setContentView(R.layout.view_dialog_swipe_mode);
        ((LinearLayout) viewModeDialog.findViewById(R.id.dialog_viewMode)).getLayoutParams().width = (int) (getScreenWidth() * 0.90);

        btn_vertical = (LinearLayout) viewModeDialog.findViewById(R.id.ll_btn_Vertical);
        btn_horizontal = (LinearLayout) viewModeDialog.findViewById(R.id.ll_btn_Horizontal);
        refreshViewMode();

        btn_vertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show.log("btn_vertical", LinearLayoutManager.VERTICAL + "");
                settingHandle.setOrientation(LinearLayoutManager.VERTICAL);
                refreshViewMode();
            }
        });

        btn_horizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show.log("btn_horizontal", LinearLayoutManager.HORIZONTAL + "");
                settingHandle.setOrientation(LinearLayoutManager.HORIZONTAL);
                refreshViewMode();
            }
        });
    }

    public void refreshViewMode() {
        clearBorderViewMode();
        if (settingHandle.getOrientation() == LinearLayoutManager.VERTICAL) {
            btn_vertical.setBackgroundColor(Color.parseColor("#000000"));
        } else {
            btn_horizontal.setBackgroundColor(Color.parseColor("#000000"));
        }
        Show.log("orientation", settingHandle.getOrientation() + "");
    }

    public void clearBorderViewMode() {
        btn_vertical.setBackgroundColor(Color.parseColor("#FFFFFF"));
        btn_horizontal.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }

    public void createFbLoginButton() {
        facebookAPI.createLoginButton(loginButton,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Toast.makeText(activity, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(activity, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
        checkLogin();
    }

    public void createNavigationButton() {
        ((ImageView) toolbar.findViewById(R.id.navigation_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHideNavigation();
            }
        });
    }

    public void showHideNavigation() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == btn_likes.getId()) {
            likesClicked();
        } else if (id == btn_shares.getId()) {
            sharesClicked();
        } else if (id == btn_log.getId()) {
            loginClicked();
        } else if (id == btn_brightness.getId()) {
            brightnessClicked();
        } else if (id == btn_viewMode.getId()) {
            viewModeClicked();
        }
    }

    public void loginClicked() {
        facebookAPI.createLoginButton(loginButton, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Show.toastSHORT(activity, "Đăng nhập thành công");
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Show.toastSHORT(activity, "Đăng nhập thất bại");
            }
        });
        loginButton.performClick();
    }

    public void sharesClicked() {
        facebookAPI.showShareDialog(new FacebookContent(null, null, null, "https://www.facebook.com/MComics-252252888524492/"));
    }

    public void likesClicked() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/MComics-252252888524492/"));
        activity.startActivity(browserIntent);
    }

    public void brightnessClicked() {
        int currentBrightness = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);
        seekBar.setProgress(currentBrightness);
        brightnessDialog.show();
    }

    public void viewModeClicked() {
        viewModeDialog.show();
    }

    public void hideActionbar() {
        toolbar.setVisibility(View.GONE);
    }

    public SettingHandle getSettingHandle() {
        return settingHandle;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookAPI.onActivityResult(requestCode, resultCode, data);
    }
}
