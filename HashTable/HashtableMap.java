import java.util.NoSuchElementException;

/**
 * This class implements a HashtableMap.
 *
 * @param <KeyType>   the type of keys stored in the hashtable
 * @param <ValueType> the type of values stored in the hashtable
 */
public class HashtableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {
    // The default capacity is set to be 8
    private static final int DEFAULT_CAPACITY = 8;
    // The load factor is set to be 0.7
    private static final float LOAD_FACTOR_THRESHOLD = 0.7f;
    private Entry<KeyType, ValueType>[] table;
    private int size;

    /**
     * This class represents an entry in the HashtableMap.
     *
     * @param <KeyType>   the type of key
     * @param <ValueType> the type of value
     */
    private static class Entry<KeyType, ValueType> {
        KeyType key;
        ValueType value;

        /**
         * Constructs a new entry with the specified key and value.
         *
         * @param key   the key of the entry
         * @param value the value of the entry
         */
        public Entry(KeyType key, ValueType value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * Constructs a new HashtableMap with the specified capacity.
     *
     * @param capacity the initial capacity of the hashtable
     */
    public HashtableMap(int capacity) {
        this.table = new Entry[capacity];
        this.size = 0;
    }

    /**
     * Constructs a new HashtableMap with the default capacity.
     */
    public HashtableMap() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Inserts the specified key-value pair into the hashtable.
     *
     * @param key   the key to be inserted
     * @param value the value associated with the key
     * @throws IllegalArgumentException if the key is null or already exists in the hashtable
     */
    @Override
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {
        if (key == null) throw new IllegalArgumentException("Key cannot be null.");
        if (containsKey(key)) throw new IllegalArgumentException("Duplicate key.");

        // Move i to the desired position with linear probing
        int i;
        for (i = getIndex(key); table[i] != null; i = (i + 1) % table.length) ;

        table[i] = new Entry<>(key, value);
        size++;

        // Check if resizing is needed after insertion
        if ((float) (size) / table.length >= LOAD_FACTOR_THRESHOLD) resize();
    }

    /**
     * put method specialized to resize()
     *
     * @param entry the entry to resize
     */
    private void putForResize(Entry<KeyType, ValueType> entry) {
        int i;
        for (i = getIndex(entry.key); table[i] != null; i = (i + 1) % table.length) ;
        table[i] = entry;
    }


    /**
     * Checks if the hashtable contains the specified key.
     *
     * @param key the key to check for existence
     * @return true if the key is found, false otherwise
     * @throws IllegalArgumentException if the key is null
     */
    @Override
    public boolean containsKey(KeyType key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null.");

        for (int i = getIndex(key); table[i] != null; i = (i + 1) % table.length) {
            if (table[i].key.equals(key)) return true;
        }

        return false;
    }

    /**
     * Retrieves the value associated with the specified key.
     *
     * @param key the key whose value is to be retrieved
     * @return the value associated with the key
     * @throws NoSuchElementException   if the key is not found in the hashtable
     * @throws IllegalArgumentException if the key is null
     */
    @Override
    public ValueType get(KeyType key) throws NoSuchElementException {
        if (key == null) throw new IllegalArgumentException("Key cannot be null.");
        // Find the corresponding index
        for (int i = getIndex(key); table[i] != null; i = (i + 1) % table.length) {
            if (table[i].key.equals(key)) return table[i].value;
        }

        throw new NoSuchElementException("Key not found.");
    }

    /**
     * Removes the key-value pair associated with the specified key from the hashtable.
     *
     * @param key the key to be removed
     * @return the value associated with the removed key
     * @throws NoSuchElementException   if the key is not found in the hashtable
     * @throws IllegalArgumentException if the key is null
     */
    @Override
    public ValueType remove(KeyType key) throws NoSuchElementException {
        if (key == null) throw new IllegalArgumentException("Key cannot be null.");

        int index = -1;
        // Find the target index
        for (int i = getIndex(key); table[i] != null; i = (i + 1) % table.length) {
            if (table[i].key.equals(key)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new NoSuchElementException("Key not found.");
        }

        ValueType val = table[index].value;
        table[index] = null;
        size--;

        // rehash the remaining keys in this probe sequence.
        for (int i = (index + 1) % table.length; table[i] != null; i = (i + 1) % table.length) {
            Entry<KeyType, ValueType> entry = table[i];
            table[i] = null;
            size--;  // decrement size before reinserting the element
            putForResize(entry);
        }

        return val;
    }

    /**
     * Removes all key-value pairs from the hashtable, resetting it to an empty state.
     */
    @Override
    public void clear() {
        // create a new hash table and swap
        Entry<KeyType, ValueType>[] newTable = new Entry[table.length];
        table = newTable;
        size = 0;
    }

    /**
     * Get the current size of the table
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * Get the current capacity of the table
     */
    @Override
    public int getCapacity() {
        return table.length;
    }

    /**
     * Get the index of the assigned key
     */
    private int getIndex(KeyType key) {
        return (Math.abs(key.hashCode()) % table.length);
    }

    /**
     * Resizes the hashtable by doubling its capacity and rehashing all key-value pairs.
     * This method is called when the load factor threshold is exceeded.
     */
    private void resize() {
        Entry<KeyType, ValueType>[] temp = table;
        int oldSize = size;
        table = new Entry[temp.length * 2];
        size = 0;

        for (Entry<KeyType, ValueType> entry : temp) {
            if (entry != null) {
                putForResize(entry);
            }
        }
        size = oldSize;
    }
}
