package vn.edu.poly.mcomics.object.handle.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import vn.edu.poly.mcomics.object.handle.other.Show;
import vn.edu.poly.mcomics.object.variable.Comics;
import vn.edu.poly.mcomics.object.variable.ComicsKind;
import vn.edu.poly.mcomics.object.variable.Content;
import vn.edu.poly.mcomics.object.variable.FacebookContent;

/**
 * Created by lucius on 11/16/16.
 */

public class ParserJSON {
    public ArrayList<Comics> getComicArray(String JSON) throws JSONException {
        ArrayList<Comics> comicsArray = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(JSON);
        for (int x = 0; x < jsonArray.length(); x++) {
            comicsArray.add(getComic(jsonArray.getJSONObject(x)));
        }
        return comicsArray;
    }

    public Comics getComic(JSONObject jsonObject) throws JSONException {
        return new Comics(jsonObject.getInt("id"),
                jsonObject.getString("comics_name"),
                jsonObject.getString("kind"),
                jsonObject.getString("thumbnail"),
                jsonObject.getInt("views"),
                jsonObject.getString("author"),
                jsonObject.getString("episodes"),
                jsonObject.getString("content"));
    }

    public ArrayList<ComicsKind> getComicKindArray(String json) throws JSONException {
        ArrayList<ComicsKind> comicKindArray = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(json);
        for (int x = 0; x < jsonArray.length(); x++) {
            comicKindArray.add(getComicKind(jsonArray.getJSONObject(x)));
        }
        return comicKindArray;
    }

    public ComicsKind getComicKind(JSONObject jsonObject) throws JSONException {
        return new ComicsKind(jsonObject.getInt("id"),
                jsonObject.getString("kind_name"));
    }

    public ArrayList<Integer> getChapterArray(String json) throws JSONException {
        ArrayList<Integer> chapterArray = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(json);
        for (int x = 0; x < jsonArray.length(); x++) {
            chapterArray.add(jsonArray.getJSONObject(x).getInt("chapter_number"));
        }
        return chapterArray;
    }

    public ArrayList<Content> getPage(String json) throws JSONException {
        ArrayList<Content> list = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(json);
        for (int x = 0; x < jsonArray.length(); x++) {
            int page = Integer.parseInt(jsonArray.getJSONObject(x).getString("page_number"));
            String link = jsonArray.getJSONObject(x).getString("link");
            list.add(new Content(page, link));
        }
        return list;
    }

    public FacebookContent getFacebookContentInfo(String JSON) throws JSONException {
        JSONObject jsonObject = new JSONArray(JSON).getJSONObject(0);
        Show.log("getFbContentInfo", jsonObject.toString());
        return new FacebookContent(
                jsonObject.getString("comics_master_id"),
                jsonObject.getString("fb_short_id"),
                jsonObject.getString("fb_long_id"),
                jsonObject.getString("fb_link"));
    }
}
