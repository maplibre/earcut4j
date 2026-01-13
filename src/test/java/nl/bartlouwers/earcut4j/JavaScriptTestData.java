package nl.bartlouwers.earcut4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class to load and parse JavaScript test data from the earcut-js submodule
 */
public class JavaScriptTestData {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Represents flattened polygon data
     */
    public static class FlattenedData {
        public final double[] vertices;
        public final int[] holes;
        public final int dimensions;
        
        public FlattenedData(double[] vertices, int[] holes, int dimensions) {
            this.vertices = vertices;
            this.holes = holes;
            this.dimensions = dimensions;
        }
    }
    
    /**
     * Loads and parses a test fixture from the earcut-js test data
     */
    public static FlattenedData loadFixture(String fixtureName) throws IOException {
        String filePath = "earcut-js/test/fixtures/" + fixtureName + ".json";
        InputStream inputStream = JavaScriptTestData.class.getClassLoader().getResourceAsStream(filePath);
        
        if (inputStream == null) {
            // Try reading from submodule directly
            try {
                java.nio.file.Path path = java.nio.file.Paths.get(filePath);
                if (java.nio.file.Files.exists(path)) {
                    inputStream = java.nio.file.Files.newInputStream(path);
                } else {
                    throw new IOException("Could not find test fixture: " + fixtureName + " at " + filePath);
                }
            } catch (Exception e) {
                throw new IOException("Could not find test fixture: " + fixtureName, e);
            }
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            
            JsonNode root = objectMapper.readTree(content.toString());
            return flatten(root);
        }
    }
    
    /**
     * Flattens GeoJSON-style polygon data into the format expected by earcut
     * This mimics the JavaScript flatten function
     */
    public static FlattenedData flatten(JsonNode data) {
        List<Double> vertices = new ArrayList<>();
        List<Integer> holes = new ArrayList<>();
        
        int dimensions = 2; // Default to 2D
        int holeIndex = 0;
        int prevLen = 0;
        
        for (JsonNode ring : data) {
            int ringLength = 0;
            for (JsonNode point : ring) {
                for (JsonNode coord : point) {
                    vertices.add(coord.asDouble());
                }
                ringLength++;
            }
            
            // First ring is outer boundary, subsequent rings are holes
            if (prevLen > 0) {
                holeIndex += prevLen;
                holes.add(holeIndex);
            }
            prevLen = ringLength;
        }
        
        // Convert to arrays
        double[] verticesArray = new double[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            verticesArray[i] = vertices.get(i);
        }
        
        int[] holesArray = new int[holes.size()];
        for (int i = 0; i < holes.size(); i++) {
            holesArray[i] = holes.get(i);
        }
        
        return new FlattenedData(verticesArray, holesArray, dimensions);
    }
    
    /**
     * Gets the set of available fixture files
     */
    public static java.util.Set<String> getAvailableFixtures() {
        java.util.Set<String> fixtures = new java.util.HashSet<>();
        java.io.File fixturesDir = new java.io.File("earcut-js/test/fixtures");
        if (fixturesDir.exists() && fixturesDir.isDirectory()) {
            java.io.File[] files = fixturesDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (java.io.File file : files) {
                    String name = file.getName();
                    fixtures.add(name.substring(0, name.length() - 5)); // Remove .json extension
                }
            }
        }
        return fixtures;
    }

    /**
     * Loads expected test results from the expected.json file
     */
    public static TestExpectations loadExpectations() throws IOException {
        String filePath = "earcut-js/test/expected.json";
        InputStream inputStream = JavaScriptTestData.class.getClassLoader().getResourceAsStream(filePath);
        
        if (inputStream == null) {
            // Try reading from submodule directly
            try {
                java.nio.file.Path path = java.nio.file.Paths.get(filePath);
                if (java.nio.file.Files.exists(path)) {
                    inputStream = java.nio.file.Files.newInputStream(path);
                } else {
                    throw new IOException("Could not find expected.json at " + filePath);
                }
            } catch (Exception e) {
                throw new IOException("Could not find expected.json", e);
            }
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            
            JsonNode root = objectMapper.readTree(content.toString());
            return new TestExpectations(root);
        }
    }
    
    /**
     * Container for test expectations
     */
    public static class TestExpectations {
        private final JsonNode triangles;
        private final JsonNode errors;
        private final JsonNode errorsWithRotation;
        
        public TestExpectations(JsonNode root) {
            this.triangles = root.get("triangles");
            this.errors = root.get("errors");
            this.errorsWithRotation = root.get("errors-with-rotation");
        }
        
        public int getExpectedTriangleCount(String fixtureName) {
            JsonNode node = triangles.get(fixtureName);
            return node != null ? node.asInt() : 0;
        }
        
        public double getExpectedError(String fixtureName) {
            JsonNode node = errors.get(fixtureName);
            return node != null ? node.asDouble() : 0.0;
        }
        
        public double getExpectedErrorWithRotation(String fixtureName) {
            JsonNode node = errorsWithRotation.get(fixtureName);
            return node != null ? node.asDouble() : getExpectedError(fixtureName);
        }
    }
}
