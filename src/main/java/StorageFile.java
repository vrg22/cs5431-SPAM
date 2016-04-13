import java.util.*;

public abstract class StorageFile {
    protected List<StorageEntry> entries;

    public StorageFile() {
        entries = new ArrayList<StorageEntry>();
    }

    protected StorageEntry get(String key, String value) {
        for (StorageEntry entry : entries) {
            if (entry.get(key).equals(value)) {
                return entry;
            }
        }

        return null;
    }

    // Precondition: `entry` does not conflict with any existing entries
    // TODO: check that this precondition is met, rather than just assuming
    protected void put(StorageEntry entry) {
        entries.add(entry);
    }

    // @return true if element did exist and was removed, false otherwise
    protected boolean remove(String key, String value) {
        Iterator<StorageEntry> iter = entries.iterator();
        while (iter.hasNext()) {
           StorageEntry entry = iter.next();

           if (entry.get(key).equals(value)) {
               iter.remove();
               return true;
           }
        }

        // No such element existed
        return false;
    }

    protected boolean contains(String key, String value) {
        return get(key, value) != null; //Contains a value if you get something non-null
    	//return get(key, value) == null;
    }

    public String toString() {
        return Arrays.toString(entries.toArray());
    }
}
