import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains unit tests for the HashtableMap class.
 */
public class HashtableMapTests {
    private HashtableMap<String, Integer> map;

    @BeforeEach
    public void setUp() {
        map = new HashtableMap<>();
    }

    /**
     * Test that put method works correctly.
     */
    @Test
    public void testPut() {
        map.put("key1", 1);
        assertEquals(1, map.getSize());
        assertEquals(1, map.get("key1"));
    }

    /**
     * Test that get method works correctly.
     */
    @Test
    public void testGet() {
        map.put("key1", 1);
        assertEquals(1, map.get("key1"));

        try {
            map.get("key2");
            fail("Expected NoSuchElementException to be thrown");
        } catch (NoSuchElementException e) {
            // Test passed
        }
    }

    /**
     * Test that remove method works correctly.
     */
    @Test
    public void testRemove() {
        map.put("key1", 1);
        assertEquals(1, map.remove("key1"));

        try {
            map.remove("key1");
            fail("Expected NoSuchElementException to be thrown");
        } catch (NoSuchElementException e) {
            // Test passed
        }
    }

    /**
     * Test that the map clears correctly.
     */
    @Test
    public void testClear() {
        map.clear();
        assertEquals(0, map.getSize());
    }

    /**
     * Test that the map resizes correctly.
     */
    @Test
    public void testResize() {
        // Make sure a resize operation should be triggered.
        for (int i = 0; i < 10; i++) {
            map.put("key" + i, i);
        }

        // Check that all items are still there
        for (int i = 0; i < 10; i++) {
            assertEquals(i, map.get("key" + i));
        }

        // Check that the capacity has increased
        assertTrue(map.getCapacity() > 8);
    }

    /**
     * Test the functionality of array growth when reaching threshold
     */
    @Test
    public void testArrayGrowth() {
        // Arrange
        HashtableMap<String, Integer> hashtable = new HashtableMap<>();
        int initialCapacity = hashtable.getCapacity();
        int loadFactorThreshold = (int)(initialCapacity * 0.7f);  // calculates where the load factor threshold will be hit

        // Act & Assert
        for (int i = 0; i < loadFactorThreshold; i++) {
            hashtable.put("key" + i, i);
            assertEquals(initialCapacity, hashtable.getCapacity(), "Capacity should not have changed yet.");
        }

        // Insert one more item to exceed the load factor threshold.
        hashtable.put("key" + loadFactorThreshold, loadFactorThreshold);
        assertTrue(hashtable.getCapacity() > initialCapacity, "Capacity should have increased.");
    }

}
