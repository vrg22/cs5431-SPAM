import java.util.*;

public abstract class StorageEntry {
    protected Map<String, String> values;

    public StorageEntry() {
        values = new HashMap<String, String>();
    }

    protected String get(String key) {
        return values.get(key);
    }

    protected void put(String key, String value) {
        values.put(key, value);
    }
}
