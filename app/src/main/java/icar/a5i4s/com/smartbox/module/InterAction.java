package icar.a5i4s.com.smartbox.module;

/**
 * Created by light on 2016/11/9.
 */

public class InterAction {
    private String uri;
    private int tag;

    public InterAction(String uri, int tag) {
        this.uri = uri;
        this.tag = tag;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }
}
