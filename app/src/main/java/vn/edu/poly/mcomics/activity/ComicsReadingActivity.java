package vn.edu.poly.mcomics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.json.JSONException;

import java.util.ArrayList;

import vn.edu.poly.mcomics.R;
import vn.edu.poly.mcomics.object.handle.backgroundtask.CheckInternet;
import vn.edu.poly.mcomics.object.handle.backgroundtask.LoadJsonInBackground;
import vn.edu.poly.mcomics.object.handle.custom.adapter.AdapterImage;
import vn.edu.poly.mcomics.object.handle.eventlistener.DownloadEvent;
import vn.edu.poly.mcomics.object.handle.eventlistener.OrientationChangeListener;
import vn.edu.poly.mcomics.object.handle.json.ParserJSON;
import vn.edu.poly.mcomics.object.handle.other.NavigationDrawer;
import vn.edu.poly.mcomics.object.handle.other.SettingHandle;
import vn.edu.poly.mcomics.object.handle.social.FacebookAPI;
import vn.edu.poly.mcomics.object.variable.Content;


public class ComicsReadingActivity extends AppCompatActivity implements OrientationChangeListener{
    private ArrayList<Content> urlList;
    private RecyclerView recyclerView;
    private String id;
    private String chapter;
    private FacebookAPI facebookAPI;
    private NavigationDrawer navigationDrawer;
    private SettingHandle settingHandle;
    private LinearLayoutManager layoutManager;

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
        navigationDrawer = new NavigationDrawer(this, R.layout.activity_comics_reading, (ViewGroup) (findViewById(R.id.root).getParent()));
        navigationDrawer.hideActionbar();

        settingHandle = navigationDrawer.getSettingHandle();
        settingHandle.setOrientationListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.cardView);
        layoutManager = new LinearLayoutManager(getApplication());
        layoutManager.setOrientation(settingHandle.getOrientation());
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        chapter = intent.getIntExtra("chapter", 0) + "";
        LoadJsonInBackground loadJsonInBackground = new LoadJsonInBackground();
        loadJsonInBackground.setOnFinishEvent(new DownloadEvent() {
            @Override
            public void onLoadFinish(String string) {
                load(string);
            }
        });
        loadJsonInBackground.execute("http://grayguy.xyz/index.php?kind=comics_content&comics_id=" + id + "&chapter_number=" + chapter);
    }

    public void load(String s) {
        ParserJSON parserJSON = new ParserJSON();
        try {
            urlList = parserJSON.getPage(s);
            showView();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookAPI.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onChanged(int orientation) {
        showView();
    }

    public void showView(){
        layoutManager.setOrientation(settingHandle.getOrientation());
        recyclerView.setLayoutManager(layoutManager);
        AdapterImage adapter = new AdapterImage(this, urlList);
        recyclerView.setAdapter(adapter);
    }
}
