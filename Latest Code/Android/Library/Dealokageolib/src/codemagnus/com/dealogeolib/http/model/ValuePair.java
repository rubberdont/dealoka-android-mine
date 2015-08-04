package codemagnus.com.dealogeolib.http.model;

/**
 * Created by codemagnus on 4/6/15.
 */
public class ValuePair {
    private String key;
    private String value;

    public ValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
