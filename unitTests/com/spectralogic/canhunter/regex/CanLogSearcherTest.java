package com.spectralogic.canhunter.regex;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.spectralogic.canhunter.canLogSearch.CanLine;
import com.spectralogic.canhunter.canLogSearch.CanLogSearcher;

public class CanLogSearcherTest {

	File tempFile = new File("./unitTestTempCan.log");

	@Before
	public void setup(){
		List<String> strings = Arrays.asList(
				"303, 30e,  d, 1, 0, 0, 1, 6, 00 00 00 00 45 a3      , , 06:23:29.870 03-24-2015\n",
				"30e, 303,  e, 0, 0, 0, 1, 7, 71 00 01 00 00 6f ad   , , 06:23:29.871 03-24-2015\n",
				"302, 303,  e, 0, 0, 0, 1, 6, 6f 22 00 00 01 ff      , , 06:23:29.890 03-24-2015\n",
				"303, 302,  e, 1, 0, 0, 1, 6, 00 00 00 00 00 00      , , 06:23:29.931 03-24-2015\n",
				"302, 30e,  4, 0, 0, 0, 0, 8, 11 00 41 40 00 02 96 5e, , 06:23:29.932 03-24-2015\n",
				"302, 30e,  4, 0, 0, 1, 0, 8, 00 00 76 ea 00 00 76 e5, , 06:23:29.932 03-24-2015\n",
				"302, 30e,  4, 0, 0, 2, 0, 8, 00 05 00 03 00 08 ff fe, , 06:23:29.933 03-24-2015\n",
				"302, 30e,  4, 0, 0, 3, 0, 8, 00 00 00 28 00 13 ff f0, , 06:23:29.933 03-24-2015\n",
				"302, 30e,  4, 0, 0, 0, 0, 8, 00 00 03 6b 00 07 00 00, , 06:23:29.934 03-24-2015\n",
				"302, 30e,  4, 0, 0, 1, 0, 8, 00 00 11 98 00 00 00 00, , 06:23:29.934 03-24-2015\n",
				"302, 30e,  4, 0, 0, 2, 0, 8, 00 00 00 00 00 00 00 00, , 06:23:29.935 03-24-2015\n",
				"302, 30e,  4, 0, 0, 3, 1, 8, 00 00 00 e9 00 03 13 22, , 06:23:29.935 03-24-2015\n",
				"303, 302,  c, 0, 0, 0, 1, 8, 74 00 06 25 00 00 00 00, , 06:23:29.936 03-24-2015\n",
				"302, 303,  c, 1, 0, 0, 1, 6, 00 01 00 00 00 00      , , 06:23:29.936 03-24-2015\n",
				"303, 302,  d, 0, 0, 0, 1, 6, 6f 22 00 00 02 a8      , , 06:23:29.937 03-24-2015\n",
				"302, 303,  d, 1, 0, 0, 1, 6, 00 02 00 00 01 e6      , , 06:23:29.937 03-24-2015\n",
				"301, 30e,  2, 1, 0, 0, 1, 6, 00 00 ff ff ff e7      , , 06:23:30.442 03-24-2015\n",
				"303, 30e,  f, 0, 0, 0, 0, 8, 11 00 41 40 00 02 62 bb, , 06:23:30.485 03-24-2015\n",
				"303, 30e,  f, 0, 0, 1, 0, 8, 00 00 31 b8 00 00 31 b8, , 06:23:30.493 03-24-2015\n",
				"303, 30e,  f, 0, 0, 2, 0, 8, 00 00 00 00 00 00 ff fd, , 06:23:30.493 03-24-2015\n",
				"303, 30e,  f, 0, 0, 3, 0, 8, 00 00 00 0e 00 06 ff fa, , 06:23:30.494 03-24-2015\n",
				"303, 30e,  f, 0, 0, 0, 0, 8, 00 00 03 18 00 13 00 00, , 06:23:30.494 03-24-2015\n",
				"303, 30e,  f, 0, 0, 1, 0, 8, 00 00 1c f5 00 00 00 00, , 06:23:30.494 03-24-2015\n",
				"303, 30e,  f, 0, 0, 2, 0, 8, 00 00 00 00 00 00 00 00, , 06:23:30.495 03-24-2015\n",
				"303, 30e,  f, 0, 0, 3, 1, 8, 00 00 00 d3 00 02 8f a5, , 06:23:30.496 03-24-2015\n",
				"302, 30e,  b, 1, 0, 0, 1, 6, 00 00 00 00 5a a1      , , 06:23:31.096 03-24-2015\n",
				"303, 30e,  0, 0, 0, 0, 1, 8, 11 00 3e 04 00 00 6d 74, , 06:23:31.433 03-24-2015\n",
				"303, 302,  e, 0, 0, 0, 1, 8, 74 00 06 25 00 00 00 00, , 06:23:31.757 03-24-2015\n",
				"302, 303,  e, 1, 0, 0, 1, 6, 00 01 00 00 00 00      , , 06:23:31.766 03-24-2015");

		if(tempFile.exists()){
			tempFile.delete();
		}

		try(FileWriter writer = new FileWriter(tempFile)){
			tempFile.createNewFile();
			for(String line : strings){
				writer.append(line);
			}

		}catch( IOException e){
			e.printStackTrace();
		}
	}

	@After
	public void after(){
		if(tempFile.exists()){
			tempFile.delete();
		}
	}

	@Test
	public void testSearch() {
		try{

			Pattern pattern = CanLine.regex(".*", ".*", "74 00 06", ".*");

			CanLogSearcher searcher = new CanLogSearcher(tempFile, pattern);

			List<List<CanLine>> results = searcher.search();

			assertEquals(2, results.size());
			assertEquals(2, results.get(0).size());
			assertEquals(2, results.get(1).size());

		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

}
