package vn.edu.poly.mcomics.object.variable;

/**
 * Created by vuong on 30/11/2016.
 */

public class Content {
    private int page_number;
    private String link;

    public Content(int page_number, String link) {
        this.page_number = page_number;
        this.link = link;
    }

    

    public int getPage_number() {
        return page_number;
    }

    public void setPage_number(int page_number) {
        this.page_number = page_number;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
