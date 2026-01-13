<p align="center">
  <img src="https://github.com/user-attachments/assets/7ff2cda8-f564-4e70-a971-d34152f969f0#gh-light-mode-only" alt="MapLibre Logo" width="200">
  <img src="https://github.com/user-attachments/assets/cee8376b-9812-40ff-91c6-2d53f9581b83#gh-dark-mode-only" alt="MapLibre Logo" width="200">
</p>

## earcut4j

[![CI](https://github.com/louwers/earcut4j/actions/workflows/ci.yml/badge.svg)](https://github.com/louwers/earcut4j/actions/workflows/ci.yml) [![Maven Central Version](https://img.shields.io/maven-central/v/nl.bartlouwers/earcut4j)](https://central.sonatype.com/artifact/nl.bartlouwers/earcut4j) [![](https://img.shields.io/badge/Slack-%23maplibre-2EB67D?logo=slack)](https://slack.openstreetmap.us/)

This triangulation library is based on the javascript version located in [@mapbox/earcut](https://github.com/mapbox/earcut). It is a fork of [earcut4j/earcut4j](https://github.com/earcut4j/earcut4j) now maintained as a [MapLibre](https://maplibre.org/) project.

#### The algorithm

The library implements a modified ear slicing algorithm,
optimized by [z-order curve](http://en.wikipedia.org/wiki/Z-order_curve) hashing
and extended to handle holes, twisted polygons, degeneracies and self-intersections
in a way that doesn't _guarantee_ correctness of triangulation,
but attempts to always produce acceptable results for practical data.

It's based on ideas from
[FIST: Fast Industrial-Strength Triangulation of Polygons](http://www.cosy.sbg.ac.at/~held/projects/triang/triang.html) by Martin Held
and [Triangulation by Ear Clipping](http://www.geometrictools.com/Documentation/TriangulationByEarClipping.pdf) by David Eberly.

#### Installation

Download the latest version:

##### Maven dependency:

```XML
<dependency>
  <groupId>nl.bartlouwers</groupId>
  <artifactId>earcut4j</artifactId>
  <version>3.0.0</version>
</dependency>
```
       
##### Or when using Gradle:

```groovy
dependencies {
  compile "nl.bartlouwers:earcut4j:3.0.0"
}
```

#### Usage

```java
List<Integer> triangles = Earcut.earcut(new double[] { 10,0, 0,50, 60,60, 70,10 }, null, 2);
// returns [1,0,3, 3,2,1]
```

Signature: `earcut(double[] data, int[] holeIndices, int dim)`.

* `data` is a flat array of vertice coordinates like `[x0,y0, x1,y1, x2,y2, ...]`.
* `holeIndices` is an array of hole _indices_ if any
  (e.g. `[5, 8]` for a 12-vertice input would mean one hole with vertices 5&ndash;7 and another with 8&ndash;11).
* `dim` is the number of coordinates per vertice in the input array (`2` by default).

Each group of three vertice indices in the resulting array forms a triangle.

```java
// triangulating a polygon with a hole
List<Integer> triangles = Earcut.earcut(new double[] { 0, 0, 100, 0, 100, 100, 0, 100, 20, 20, 80, 20, 80, 80, 20, 80 }, new int[] { 4 }, 2);
// [3,0,4, 5,4,0, 3,4,7, 5,0,1, 2,3,7, 6,5,1, 2,7,6, 6,1,2]

// triangulating a polygon with 3d coords
List<Integer> triangles = Earcut.earcut(new double[] { 10, 0, 1, 0, 50, 2, 60, 60, 3, 70, 10, 4 }, null, 3);
// [1,0,3, 3,2,1]

// verify triangulation quality
double deviation = Earcut.deviation(data, holes, dimensions, triangles);
// returns relative difference between triangle area and polygon area (0 = perfect)
```

If you pass a single vertice as a hole, Earcut treats it as a Steiner point.





