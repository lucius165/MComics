package vn.edu.poly.mcomics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;

import vn.edu.poly.mcomics.R;
import vn.edu.poly.mcomics.object.handle.backgroundtask.CheckInternet;
import vn.edu.poly.mcomics.object.handle.backgroundtask.LoadJsonInBackground;
import vn.edu.poly.mcomics.object.handle.custom.adapter.ComicListCustomAdapter;
import vn.edu.poly.mcomics.object.handle.eventlistener.DownloadEvent;
import vn.edu.poly.mcomics.object.handle.json.ParserJSON;
import vn.edu.poly.mcomics.object.handle.other.NavigationDrawer;
import vn.edu.poly.mcomics.object.handle.social.FacebookAPI;
import vn.edu.poly.mcomics.object.variable.Comics;

public class ComicsCategoryActivity extends AppCompatActivity implements DownloadEvent {
    private GridView androidGridView;
    private TextView text;
    private FacebookAPI facebookAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!CheckInternet.check(this)){
            setContentView(R.layout.view_connect_fail);
            return;
        }
        facebookAPI = new FacebookAPI(this);
        facebookAPI.init();
        setContentView(R.layout.view_navigation);
        new NavigationDrawer(this, R.layout.activity_comics_category, (ViewGroup) (findViewById(R.id.root)).getParent());

        text = (TextView) findViewById(R.id.text);
        LoadJsonInBackground backgroundTask = new LoadJsonInBackground();
        backgroundTask.setOnFinishEvent(this);
        backgroundTask.execute("http://grayguy.xyz/?kind=by_kind&comic_kind=" + getIntent().getExtras().getInt("id"));

    }

    //button search
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action, menu);
        return true;
    }

    @Override
    public void onLoadFinish(String string) {
        ParserJSON parserJSON = new ParserJSON();
        try {
            ArrayList<Comics> arrComics = parserJSON.getComicArray(string);
            ComicListCustomAdapter adapterViewAndroid = new ComicListCustomAdapter(ComicsCategoryActivity.this, arrComics);
            androidGridView = (GridView) findViewById(R.id.grid_view_image_text);
            androidGridView.setAdapter(adapterViewAndroid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookAPI.onActivityResult(requestCode, resultCode, data);
    }
}
