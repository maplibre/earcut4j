package nl.bartlouwers.earcut4j;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Dynamic tests for each JavaScript fixture using JUnit 5 @TestFactory
 */
public class IndividualJavaScriptFixtureTests {
    
    @TestFactory
    Stream<DynamicTest> testAllJavaScriptFixtures() throws IOException {
        JavaScriptTestData.TestExpectations expectations = JavaScriptTestData.loadExpectations();
        Set<String> availableFixtures = JavaScriptTestData.getAvailableFixtures();
        
        return availableFixtures.stream()
            .map(fixtureName -> DynamicTest.dynamicTest(
                "testFixture[" + fixtureName + "]",
                () -> testFixture(fixtureName, expectations)
            ));
    }
    
    private void testFixture(String fixtureName, JavaScriptTestData.TestExpectations expectations) throws IOException {
        JavaScriptTestData.FlattenedData data = JavaScriptTestData.loadFixture(fixtureName);
        List<Integer> triangles = Earcut.earcut(data.vertices, data.holes, data.dimensions);
        
        // Get expected results
        int expectedTriangles = expectations.getExpectedTriangleCount(fixtureName);
        double expectedDeviation = expectations.getExpectedError(fixtureName);
        
        if (expectedTriangles > 0) {
            int actualTriangles = triangles.size() / 3;
            
            if (actualTriangles == expectedTriangles) {
                // Check deviation if expected
                if (expectedDeviation > 0) {
                    double deviation = Earcut.deviation(data.vertices, data.holes, data.dimensions, triangles);
                    
                    if (deviation > expectedDeviation) {
                        fail("Deviation too high for " + fixtureName + ": " + deviation + " > " + expectedDeviation);
                    }
                }
            } else {
                fail("Triangle count mismatch for " + fixtureName + " expected:<" + expectedTriangles + "> but was:<" + actualTriangles + ">");
            }
        }
        // If no expected result, just check that it doesn't crash (which we've already done)
    }
}
