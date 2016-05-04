package framgia.vn.photoSketch.models;

/**
 * Created by hoada921 on 2016-04-21.
 */
public class Effect {
    private String mName;
    private int mValue;

    public Effect() {
    }

    public Effect(String name, int value) {
        this.mName = name;
        this.mValue = value;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        this.mValue = value;
    }
}
