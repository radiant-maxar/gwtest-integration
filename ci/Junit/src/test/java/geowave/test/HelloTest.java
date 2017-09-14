package geowave.test;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import geowave.util.CmdUtils;
import geowave.util.TestUtils;

public class HelloTest {
	
	private static String hostname;
	static {
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			hostname = "localhost";
		}
	}
	
	Scanner s;
	// Names:
	private String vStore = "gdelt";
	private String vStoreKDE = "gdelt-kde";
	private String vIndex = "gdelt-spatial";
	private String vCoverage = "gdeltevent";
	private String vCoverageKDE = "gdeltevent_kde";
	private String vSpace = "geowave.gdelt";
	private String vSpaceKDE = "geowave.kde_gdelt";
	
	private String rStore = "landsatraster";
	private String rCopiedStore = "landsatvector";
	private String rIndex = "spatial";
	private String rCoverage = "berlin_mosaic";
	private String rSpace = "landsat_raster";
	private String rSpaceCopy = "landsat_vector";
	
	// Commands:
	private String germany = "MULTIPOLYGON (((8.710256576538086 47.696809768676758,8.678594589233398 47.69334602355957,8.670557022094727 47.71110725402832,8.710256576538086 47.696809768676758)),((6.806390762329102 53.60222053527832,6.746946334838867 53.560274124145508,6.658334732055664 53.58610725402832,6.806390762329102 53.60222053527832)),((6.939443588256836 53.669443130493164,6.87639045715332 53.67027473449707,7.088335037231445 53.684167861938477,6.939443588256836 53.669443130493164)),((7.242498397827148 53.704439163208008,7.135835647583008 53.706110000610352,7.346944808959961 53.721109390258789,7.242498397827148 53.704439163208008)),((8.191110610961914 53.72471809387207,8.120000839233398 53.713052749633789,8.142778396606445 53.733606338500977,8.191110610961914 53.72471809387207)),((7.622224807739258 53.75444221496582,7.467779159545898 53.733057022094727,7.485834121704102 53.757501602172852,7.622224807739258 53.75444221496582)),((7.758890151977539 53.760553359985352,7.664445877075195 53.761667251586914,7.812780380249023 53.775552749633789,7.758890151977539 53.760553359985352)),((8.42527961730957 53.928056716918945,8.411664962768555 53.95555305480957,8.454999923706055 53.963052749633789,8.42527961730957 53.928056716918945)),((13.940279006958008 54.024995803833008,13.925832748413086 54.018327713012695,13.934446334838867 54.027772903442383,13.940279006958008 54.024995803833008)),((8.695554733276367 54.041109085083008,8.671388626098633 54.077775955200195,8.693334579467773 54.082498550415039,8.695554733276367 54.041109085083008)),((14.001317977905273 54.065362930297852,14.225557327270508 53.928606033325195,14.218889236450195 53.869020462036133,13.823431015014648 53.85374641418457,14.056005477905273 53.984865188598633,13.759164810180664 54.159997940063477,14.001317977905273 54.065362930297852)),((10.97944450378418 54.380556106567383,11.017778396606445 54.380273818969727,11.003053665161133 54.37693977355957,10.97944450378418 54.380556106567383)),((8.893056869506836 54.461938858032227,8.815000534057617 54.500833511352539,8.960554122924805 54.519166946411133,8.893056869506836 54.461938858032227)),((11.312776565551758 54.406946182250977,11.006387710571289 54.461664199829102,11.184167861938477 54.519998550415039,11.312776565551758 54.406946182250977)),((8.662778854370117 54.494165420532227,8.59111213684082 54.527772903442383,8.710832595825195 54.551668167114258,8.662778854370117 54.494165420532227)),((13.073610305786133 54.488611221313477,13.09666633605957 54.590555191040039,13.151388168334961 54.602777481079102,13.073610305786133 54.488611221313477)),((13.383054733276367 54.638887405395508,13.730833053588867 54.275835037231445,13.11833381652832 54.333887100219727,13.267499923706055 54.382501602172852,13.146963119506836 54.54560661315918,13.503091812133789 54.493097305297852,13.244722366333008 54.559167861938477,13.383054733276367 54.638887405395508)),((8.364442825317383 54.61332893371582,8.294443130493164 54.666666030883789,8.353887557983398 54.711664199829102,8.364442825317383 54.61332893371582)),((8.567777633666992 54.685274124145508,8.396944046020508 54.713884353637695,8.551111221313477 54.753885269165039,8.567777633666992 54.685274124145508)),((10.97944450378418 54.380556106567383,10.818536758422852 53.890054702758789,12.526945114135742 54.474161148071289,12.924165725708008 54.426942825317383,12.369722366333008 54.26500129699707,13.023889541625977 54.399721145629883,13.455831527709961 54.096109390258789,13.718332290649414 54.169717788696289,13.813055038452148 53.845277786254883,14.275629043579102 53.699068069458008,14.149168014526367 52.86277961730957,14.640275955200195 52.57249641418457,14.599443435668945 51.818605422973633,15.03639030456543 51.285554885864258,14.828332901000977 50.86583137512207,14.309720993041992 51.053606033325195,12.093706130981445 50.322534561157227,12.674444198608398 49.424997329711914,13.833612442016602 48.77360725402832,12.758333206176758 48.12388801574707,13.016668319702148 47.470277786254883,12.735555648803711 47.684167861938477,11.095556259155273 47.396112442016602,10.478055953979492 47.591943740844727,10.173334121704102 47.274721145629883,9.56672477722168 47.54045295715332,8.566110610961914 47.806940078735352,8.576421737670898 47.591371536254883,7.697225570678711 47.543329238891602,7.58827018737793 47.584482192993164,7.578889846801758 48.119722366333008,8.226079940795898 48.964418411254883,6.36216926574707 49.459390640258789,6.524446487426758 49.808610916137695,6.134416580200195 50.127847671508789,6.39820671081543 50.323175430297852,6.011800765991211 50.757272720336914,5.864721298217773 51.046106338500977,6.222223281860352 51.465829849243164,5.962499618530273 51.807779312133789,6.828889846801758 51.965555191040039,7.065557479858398 52.385828018188477,6.68889045715332 52.549165725708008,7.051668167114258 52.643610000610352,7.208364486694336 53.242807388305664,7.015554428100586 53.41472053527832,7.295835494995117 53.685274124145508,8.008333206176758 53.710000991821289,8.503053665161133 53.354166030883789,8.665555953979492 53.893884658813477,9.832498550415039 53.536386489868164,8.899721145629883 53.940828323364258,8.883611679077148 54.294168472290039,8.599443435668945 54.333887100219727,9.016942977905273 54.498331069946289,8.580549240112305 54.867879867553711,8.281110763549805 54.746942520141602,8.393331527709961 55.053056716918945,8.664545059204102 54.913095474243164,9.44536018371582 54.825403213500977,9.972776412963867 54.761110305786133,9.870279312133789 54.454439163208008,10.97944450378418 54.380556106567383),(11.459165573120117 53.96110725402832,11.488611221313477 54.023050308227539,11.37388801574707 53.988611221313477,11.459165573120117 53.96110725402832),(11.544168472290039 54.06138801574707,11.612421035766602 54.104585647583008,11.511110305786133 54.048608779907227,11.544168472290039 54.06138801574707),(12.72972297668457 54.416666030883789,12.702775955200195 54.42833137512207,12.68610954284668 54.418329238891602,12.72972297668457 54.416666030883789)))";
	private String berlin = "BBOX(shape,13.0535, 52.3303, 13.7262, 52.6675)";

	private String addStore = String.format("geowave config addstore %s --gwNamespace %s -t hbase --zookeeper %s:2181", vStore, vSpace, hostname);
	private String addStore_KDE = String.format("geowave config addstore %s --gwNamespace %s -t hbase --zookeeper %s:2181", vStoreKDE, vSpaceKDE, hostname);
	private String addIndex = String.format("geowave config addindex -t spatial %s --partitionStrategy round_robin --numPartitions 32", vIndex);
	private String[] ingestGermany = {"geowave", "ingest", "localtogw", "/mnt/gdelt", vStore, vIndex, "-f", "gdelt", "--gdelt.cql",  "INTERSECTS(geometry," + germany + ")"};
	private String runKDE = String.format("hadoop jar /usr/local/geowave-0.9.6-apache/tools/geowave-tools-0.9.6-apache.jar analytic kde --featureType gdeltevent --minLevel 5 --maxLevel 26 --minSplits 32 --maxSplits 32 --coverageName %s --hdfsHostPort %s:8020 --jobSubmissionHostPort %s:8032 --tileSize 1 %s %s", vCoverageKDE, hostname, hostname, vStore, vStoreKDE);
	
	private String addStore_raster = String.format("geowave config addstore -t hbase -z %s:2181 %s --gwNamespace %s", hostname, rStore, rSpace);
	private String copyStore_raster = String.format("geowave config cpstore %s %s --gwNamespace %s", rStore, rCopiedStore, rSpaceCopy);
	private String addIndex_raster = String.format("geowave config addindex -t spatial %s", rIndex);
	private String[] cacheGermany = {"geowave", "landsat", "analyze", "--nbestperspatial", "--nbestscenes", "1", "--usecachedscenes", "--cql", "INTERSECTS(shape," + germany + ") AND band='B8' AND cloudCover>0", "-ws", "/mnt/landsat"};
	private String[] cacheBerlin = {"geowave", "landsat", "analyze", "--nbestperspatial", "--nbestscenes", "1", "--usecachedscenes", "--cql", berlin + " AND band='B8' AND cloudCover>0", "-ws", "/mnt/landsat"};
	private String[] ingestBerlin = {"geowave", "landsat", "ingest", "--nbestperspatial", "--nbestscenes", "1", "--usecachedscenes", "--cql", berlin + " AND band='B8' AND cloudCover>0", "--crop", "--retainimages", "-ws", "/mnt/landsat", "--vectorstore", rCopiedStore, "--pyramid", "--coverage", rCoverage, rStore, rIndex};
	
	// Environment Variables
	private String[] hadoop_home = {"HADOOP_HOME=/usr/hdp/2.6.0.3-8/hadoop/"};
	private String[] ld_library_path = {"LD_LIBRARY_PATH=/mnt"};

	//TODO - Remove comment from @Before.
	
	@Before
	public void setUp() throws Exception {
		TestUtils.assertSuccess(CmdUtils.send("geowave --version"));
		System.out.println("Using HOSTNAME = " + hostname);
//		// Verify no data in GS:
//		assertTrue(TestUtils.insensitiveMatch(CmdUtils.send("geowave gs listfl"), "{\"layers\": []}"));
//		assertTrue(TestUtils.insensitiveMatch(CmdUtils.send("geowave gs listds"), "[]"));
//		assertTrue(TestUtils.insensitiveMatch(CmdUtils.send("geowave gs listcs"), "[]"));
//		
//		// Verify there is no ingested data:
//		for (String store : new String[]{vStoreKDE, vStore, rStore, rCopiedStore}) {
//			assertTrue(TestUtils.insensitiveMatch(CmdUtils.send("geowave remote liststats " + store), "exception"));
//		}
//		
	}

	@After
	public void tearDown() throws Exception {
		for (String store : new String[]{vStoreKDE, vStore, rStore, rCopiedStore}) {
			CmdUtils.send("geowave remote clear " + store);
			CmdUtils.send("geowave config rmstore " + store);
		}
		for (String index : new String[]{vIndex, rIndex}) {
			CmdUtils.send("geowave config rmindex " + index);
		}
		for (String layer : new String[]{vCoverage, vCoverageKDE, rCoverage, "band", "scene"}) {
			CmdUtils.send("geowave gs rmfl " + layer);
		}
		CmdUtils.send("geowave gs rmstyle styleName_sub");
		CmdUtils.send("geowave gs rmstyle styleName_kde");
		CmdUtils.send("geowave gs rmds " + vStore + "-vector");
		CmdUtils.send("geowave gs rmds " + rCopiedStore + "-vector");
		CmdUtils.send("geowave gs rmcs " + vStoreKDE + "-raster");
		CmdUtils.send("geowave gs rmcs " + rStore + "-raster");
	}

	@Test
	public void version() {
		assertTrue(TestUtils.insensitiveMatch(CmdUtils.send("geowave --version"), "version"));
	}

	@Test
	public void usage() {
		assertTrue(TestUtils.insensitiveMatch(CmdUtils.send("geowave"), "usage"));
	}

	@Test
	public void vector_happyPath() {
		
		// Create store/index
		TestUtils.assertSuccess(CmdUtils.send(addStore));
		TestUtils.assertSuccess(CmdUtils.send(addIndex));
		TestUtils.assertSuccess(CmdUtils.send(addStore_KDE));
		
		// Verify
		String configList = CmdUtils.send("geowave config list");
		assertEquals(vSpace, CmdUtils.getProperty(configList, String.format("store.%s.opts.gwNamespace", vStore)));
		assertEquals(vSpaceKDE, CmdUtils.getProperty(configList, String.format("store.%s.opts.gwNamespace", vStoreKDE)));
		assertEquals("32", CmdUtils.getProperty(configList, String.format("index.%s.opts.numPartitions", vIndex)));
		assertEquals("spatial", CmdUtils.getProperty(configList, String.format("index.%s.type", vIndex)));
		
		// Ingest
		TestUtils.assertSuccess(CmdUtils.send(ingestGermany));
		
		// Verify
		TestUtils.assertSuccess(CmdUtils.send("geowave remote liststats " + vStore)); // Should not have exception if ingest was successful.

		// Start Geoserver
		TestUtils.assertSuccess(CmdUtils.send(String.format("geowave config geoserver %s:8000", hostname)));
		TestUtils.assertSuccess(CmdUtils.send("sudo service geowave restart"));
		assertTrue(TestUtils.tryUntilOK(String.format("http://%s:8000/geoserver/web/", hostname), 240));
		
		// Run a Kernel Density Estimation
		TestUtils.assertSuccess(CmdUtils.send(hadoop_home, runKDE));
		
		// Verify
		TestUtils.assertSuccess(CmdUtils.send("geowave remote liststats " + vStore)); // Should not have exception if KDE was successful.
		
		// Add Vector Layer
		TestUtils.assertSuccess(CmdUtils.send(hadoop_home, "geowave gs addlayer " + vStore));
		
		// Verify
		assertTrue(TestUtils.insensitiveMatch(CmdUtils.send("geowave gs listfl"), vCoverage));
		assertTrue(TestUtils.insensitiveMatch(CmdUtils.send("geowave gs listds"), vStore + "-vector"));	
		
		// Add KDE Layer
		TestUtils.assertSuccess(CmdUtils.send(hadoop_home, "geowave gs addlayer " + vStoreKDE));
		
		// Verify
		assertTrue(TestUtils.insensitiveMatch(CmdUtils.send("geowave gs listfl"), vCoverageKDE));
		assertTrue(TestUtils.insensitiveMatch(CmdUtils.send("geowave gs listcs"), vStoreKDE + "-raster"));	
		
		TestUtils.assertSuccess(CmdUtils.send("geowave gs addstyle styleName_kde -sld /mnt/KDEColorMap.sld"));
		TestUtils.assertSuccess(CmdUtils.send("geowave gs addstyle styleName_sub -sld /mnt/SubsamplePoints.sld"));
		
		// Verify
		String styles = CmdUtils.send("geowave gs liststyles");
		assertTrue(TestUtils.insensitiveMatch(styles, "styleName_kde"));
		assertTrue(TestUtils.insensitiveMatch(styles, "styleName_sub"));
		
		// Set Default Styles
		TestUtils.assertSuccess(CmdUtils.send(String.format("geowave gs setls %s --styleName styleName_kde", vCoverageKDE)));
		TestUtils.assertSuccess(CmdUtils.send(String.format("geowave gs setls %s --styleName styleName_sub", vCoverage)));

		// Verify
		assertTrue(TestUtils.insensitiveMatch(CmdUtils.send("geowave gs getfl " + vCoverage), "styleName_sub"));
		assertTrue(TestUtils.insensitiveMatch(CmdUtils.send("geowave gs getfl " + vCoverageKDE), "styleName_kde"));
	}
	
	@Test
	public void raster_happyPath() {
		// Add stores and index
		TestUtils.assertSuccess(CmdUtils.send(addStore_raster));
		TestUtils.assertSuccess(CmdUtils.send(copyStore_raster));
		TestUtils.assertSuccess(CmdUtils.send(addIndex_raster));
		
		// Verify
		String configList = CmdUtils.send("geowave config list");
		assertEquals(rSpace, CmdUtils.getProperty(configList, String.format("store.%s.opts.gwNamespace", rStore)));
		assertEquals(rSpaceCopy, CmdUtils.getProperty(configList, String.format("store.%s.opts.gwNamespace", rCopiedStore)));
		assertEquals("spatial", CmdUtils.getProperty(configList, String.format("index.%s.type", rIndex)));
		
		// Analyze Data
		TestUtils.assertSuccess(CmdUtils.send(cacheGermany));
		TestUtils.assertSuccess(CmdUtils.send(cacheBerlin));
		
		// Ingest Data
		TestUtils.assertSuccess(CmdUtils.send(ld_library_path, ingestBerlin));
		
		// Verify
		TestUtils.assertSuccess(CmdUtils.send("geowave remote liststats " + rStore));
		TestUtils.assertSuccess(CmdUtils.send("geowave remote liststats " + rCopiedStore)); // Should not have exception if ingest was successful.
		
		// Start Geoserver
		TestUtils.assertSuccess(CmdUtils.send(String.format("geowave config geoserver %s:8000", hostname)));
		TestUtils.assertSuccess(CmdUtils.send("sudo service geowave restart"));
		assertTrue(TestUtils.tryUntilOK(String.format("http://%s:8000/geoserver/web/", hostname), 240));
		
		// Add First Layer
		TestUtils.assertSuccess(CmdUtils.send(hadoop_home, "geowave gs addlayer " + rStore));
		
		// Verify
		assertTrue(TestUtils.insensitiveMatch(CmdUtils.send("geowave gs listfl"), rCoverage));
		assertTrue(TestUtils.insensitiveMatch(CmdUtils.send("geowave gs listcs"), rStore + "-raster"));	
		
		// Add Copied Layer
		TestUtils.assertSuccess(CmdUtils.send(hadoop_home, "geowave gs addlayer --add ALL " + rCopiedStore));
		
		// Verify
		assertTrue(TestUtils.insensitiveMatch(CmdUtils.send("geowave gs listfl"), "band"));
		assertTrue(TestUtils.insensitiveMatch(CmdUtils.send("geowave gs listfl"), "scene"));
		assertTrue(TestUtils.insensitiveMatch(CmdUtils.send("geowave gs listds"), rCopiedStore + "-vector"));	
	}
}
