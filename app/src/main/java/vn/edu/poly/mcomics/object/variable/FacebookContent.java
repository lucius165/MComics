package vn.edu.poly.mcomics.object.variable;

/**
 * Created by lucius on 04/12/2016.
 */

public class FacebookContent {
    private String comicsMasterId;
    private String fbShortId;
    private String fbLongId;
    private String fb_link;

    public FacebookContent(String comicsMasterId, String fbShortId, String fbLongId, String fb_link) {
        this.comicsMasterId = comicsMasterId;
        this.fbShortId = fbShortId;
        this.fbLongId = fbLongId;
        this.fb_link = fb_link;
    }

    public String getComicsMasterId() {
        return comicsMasterId;
    }

    public void setComicsMasterId(String comicsMasterId) {
        this.comicsMasterId = comicsMasterId;
    }

    public String getFbShortId() {
        return fbShortId;
    }

    public void setFbShortId(String fbShortId) {
        this.fbShortId = fbShortId;
    }

    public String getFbLongId() {
        return fbLongId;
    }

    public void setFbLongId(String fbLongId) {
        this.fbLongId = fbLongId;
    }

    public String getFb_link() {
        return fb_link;
    }

    public void setFb_link(String fb_link) {
        this.fb_link = fb_link;
    }
}
