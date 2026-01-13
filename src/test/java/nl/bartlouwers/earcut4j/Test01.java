package nl.bartlouwers.earcut4j;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Test01 {

	@Test
	public void simpleTriangle() {
		List<Integer> triangles = Earcut.earcut(new double[] { 0, 0, 0, 50, 50, 00}, null, 2);
		assertArrayEquals(new Object[] { 1, 0, 2 }, triangles.toArray());
	}
	
	@Test
	public void notSoSimpleTriangle() {
		List<Integer> triangles = Earcut.earcut(new double[] { 0, 0, 0, 25, 0, 50, 25, 25, 50, 0, 25, 0}, null, 2);
		// Not optimal, but correct.
		assertArrayEquals(new Object[] { 1, 0, 5, 5, 4, 3, 3, 2, 1, 1, 5, 3 }, triangles.toArray());
	}
	
	@Test
	public void lShape() {
		List<Integer> triangles = Earcut.earcut(new double[] { 0, 0, 10, 0, 10, 5, 5, 5, 5, 15, 0, 15}, null, 2);
		// "symmetrical" cut, among the line from the inner corner of the L to the outer bottom left corner. 
		assertArrayEquals(new Object[] { 4, 5, 0, 0, 1, 2, 3, 4, 0, 0, 2, 3 }, triangles.toArray());
	}

	@Test
	public void simplePolygon() {
		List<Integer> triangles = Earcut.earcut(new double[] { 10, 0, 0, 50, 60, 60, 70, 10 }, null, 2);
		// returns [1,0,3, 3,2,1]
		assertArrayEquals(new Object[] { 1, 0, 3, 3, 2, 1 }, triangles.toArray());
	}

	@Test
	public void polygonWithHole() {
		List<Integer> triangles = Earcut.earcut(
				new double[] { 0, 0, 100, 0, 100, 100, 0, 100, 20, 20, 80, 20, 80, 80, 20, 80 }, new int[] { 4 }, 2);
		// Correct result from JavaScript implementation: [0,4,7, 5,4,0, 3,0,7, 5,0,1, 2,3,7, 6,5,1, 2,7,6, 6,1,2]
		assertArrayEquals(
				new Object[] { 0, 4, 7, 5, 4, 0, 3, 0, 7, 5, 0, 1, 2, 3, 7, 6, 5, 1, 2, 7, 6, 6, 1, 2 },
				triangles.toArray());
	}

	@Test
	public void poluygonWith3DCoords() {
		double[] polygon = new double[] { 10, 0, 1, 0, 50, 2, 60, 60, 3, 70, 10, 4 };
		List<Integer> triangles = Earcut.earcut(polygon, null, 3);
		// [1,0,3, 3,2,1]
		assertArrayEquals(new Object[] { 1, 0, 3, 3, 2, 1 }, triangles.toArray());
	}

	@Test
	public void issue2() {
		List<Integer> triangles = Earcut.earcut(
				new double[] { 5400.0, 200.0, 0.0, 5400.0, 200.0, 5000.0, 5400.0, 0.0, 5000.0, 5400.0, 0.0, 0.0 }, null,
				3);
		System.out.println(triangles);
	}
}
