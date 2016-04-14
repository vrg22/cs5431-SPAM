import java.util.*;

public abstract class StorageEntry {
	private Map<String, String> values;

    public StorageEntry() {
        values = new HashMap<String, String>();
    }

    protected String get(String key) {
        return values.get(key);
    }

    protected void put(String key, String value) {
        values.put(key, value);
    }

    protected void update(String key, String value) {
        values.replace(key, value);
    }

    public String toString() {
        return "Entry:" + values;
    }
}
