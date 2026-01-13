package nl.bartlouwers.earcut4j;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite that runs basic JavaScript earcut test cases against the Java implementation
 */
public class JavaScriptTestSuite {
    
    @Test
    public void testBasicIndices2D() {
        // Test case from JavaScript: indices-2d
        List<Integer> triangles = Earcut.earcut(new double[]{10, 0, 0, 50, 60, 60, 70, 10});
        assertArrayEquals(new Object[]{1, 0, 3, 3, 2, 1}, triangles.toArray());
    }

    @Test
    public void testBasicIndices3D() {
        // Test case from JavaScript: indices-3d
        List<Integer> triangles = Earcut.earcut(new double[]{10, 0, 0, 0, 50, 0, 60, 60, 0, 70, 10, 0}, null, 3);
        assertArrayEquals(new Object[]{1, 0, 3, 3, 2, 1}, triangles.toArray());
    }

    @Test
    public void testEmpty() {
        // Test case from JavaScript: empty
        List<Integer> triangles = Earcut.earcut(new double[]{});
        assertTrue(triangles.isEmpty());
    }
}