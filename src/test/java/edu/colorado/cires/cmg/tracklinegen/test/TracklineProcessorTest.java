package edu.colorado.cires.cmg.tracklinegen.test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.colorado.cires.cmg.tracklinegen.GeometrySimplifier;
import edu.colorado.cires.cmg.tracklinegen.SimplifiedPointCountExceededException;
import edu.colorado.cires.cmg.tracklinegen.geometrySimplifier.GeoSimplifierProcessor;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class TracklineProcessorTest {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void test() throws Exception {

    final int geoJsonPrecision = 5;
    final double simplificationTolerance = 0.0001;
    final int simplifierBatchSize = 3000;
    final int msSplit = 3600000;
    final long maxCount = 0;
    GeometrySimplifier geometrySimplifier = new GeometrySimplifier(simplificationTolerance);
    ObjectMapper objectMapper = new ObjectMapper();

    String actualDir = "target/test-classes/phase1/actual";
    File directory= new File(actualDir);
    if (! directory.exists()){
      directory.mkdir();
    }
    Path dataFile = Paths.get("src/test/resources/phase1/test1/data.txt");
    String gsf = actualDir + "/geoSimplfied.json";

    GeoSimplifierProcessor tracklineProcessor = new GeoSimplifierProcessor(geoJsonPrecision, msSplit, geometrySimplifier, simplifierBatchSize,
        dataFile, objectMapper, Paths.get(gsf), maxCount);

    tracklineProcessor.process();

    JsonNode actual = objectMapper.readTree(new File(gsf));
    JsonNode expected = objectMapper.readTree(new File("src/test/resources/phase1/test1/expected.geojson"));
    assertEquals(expected,actual);
  }

  @Test
  public void testPointsExceeded() throws Exception {

    final int geoJsonPrecision = 5;
    final double simplificationTolerance = 0.0001;
    final int simplifierBatchSize = 3000;
    final int msSplit = 3600000;
    final long maxCount = 1;
    GeometrySimplifier geometrySimplifier = new GeometrySimplifier(simplificationTolerance);
    ObjectMapper objectMapper = new ObjectMapper();

    String actualDir = "target/test-classes/phase1/actual";
    File directory= new File(actualDir);
    if (! directory.exists()){
      directory.mkdir();
    }
    Path dataFile = Paths.get("src/test/resources/phase1/test1/data.txt");
    String gsf = actualDir + "/geoSimplfied.json";

    GeoSimplifierProcessor tracklineProcessor = new GeoSimplifierProcessor(geoJsonPrecision, msSplit, geometrySimplifier, simplifierBatchSize,
        dataFile, objectMapper, Paths.get(gsf), maxCount);

    SimplifiedPointCountExceededException ex = assertThrows(SimplifiedPointCountExceededException.class, () -> tracklineProcessor.process());

    assertEquals("Simplified point count exceeded: allowed = 1 batched points = 2", ex.getMessage());

  }
}