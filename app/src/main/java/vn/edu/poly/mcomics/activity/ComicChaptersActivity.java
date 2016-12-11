package vn.edu.poly.mcomics.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONException;

import java.util.ArrayList;

import vn.edu.poly.mcomics.R;
import vn.edu.poly.mcomics.object.handle.backgroundtask.CheckInternet;
import vn.edu.poly.mcomics.object.handle.backgroundtask.LoadJsonInBackground;
import vn.edu.poly.mcomics.object.handle.custom.adapter.ChapterListAdapter;
import vn.edu.poly.mcomics.object.handle.eventlistener.DownloadEvent;
import vn.edu.poly.mcomics.object.handle.json.ParserJSON;
import vn.edu.poly.mcomics.object.handle.other.NavigationDrawer;
import vn.edu.poly.mcomics.object.handle.social.FacebookAPI;

public class ComicChaptersActivity extends AppCompatActivity implements DownloadEvent {
    private GridView gridView;
    private String comicId;
    private NavigationDrawer navigationDrawer;
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
        navigationDrawer = new NavigationDrawer(this, R.layout.activity_comic_chapters, (ViewGroup) (findViewById(R.id.root).getParent()));

        comicId = getIntent().getStringExtra("id");
        LoadJsonInBackground loadJson = new LoadJsonInBackground();
        loadJson.setOnFinishEvent(this);
        loadJson.execute("http://grayguy.xyz/?kind=chapter_list&comics_id=" + comicId);
    }

    @Override
    public void onLoadFinish(String string) {
        ParserJSON parserJSON = new ParserJSON();
        try {
            final ArrayList<Integer> list = parserJSON.getChapterArray(string);
            gridView = ((GridView) findViewById(R.id.gridView));
            gridView.setAdapter(new ChapterListAdapter(this, list, comicId));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getBaseContext(), ComicsReadingActivity.class);
                    intent.putExtra("id", comicId);
                    intent.putExtra("chapter", position + 1);
                    startActivity(intent);
                }
            });
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
