package com.spectralogic.canhunter.regex;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import com.spectralogic.canhunter.canLogSearch.CanLine;

public class RegexTests {

	@Test
	public void testHexCharacterGroup() {
		String xidGroup = "([0-9A-Fa-f])";
		Pattern pattern = Pattern.compile(xidGroup);

		assertTrue(pattern.matcher("0").matches());
		assertTrue(pattern.matcher("4").matches());
		assertTrue(pattern.matcher("a").matches());
		assertTrue(pattern.matcher("f").matches());
		assertTrue(pattern.matcher("F").matches());
		assertFalse(pattern.matcher("123").matches());
		assertFalse(pattern.matcher("Z").matches());
		assertFalse(pattern.matcher("z").matches());

	}

	@Test
	public void testBitCharacterGroup() {
		String bitGroup = "(0|1)";
		Pattern pattern = Pattern.compile(bitGroup);

		assertTrue(pattern.matcher("0").matches());
		assertTrue(pattern.matcher("1").matches());
		assertFalse(pattern.matcher("a").matches());
		assertFalse(pattern.matcher("9").matches());
	}

	@Test
	public void testDataGroup(){
		String data = "302, 37f,  2, 0, 0, 0, 1, 5, 11 00 41 21 01         , 060eec22, 19:19:27.339 03-25-2015,RX\r\n";
		Pattern pattern = Pattern.compile("([^,]*11 00 41 21|40[^,]*)");
		assertTrue(pattern.matcher(data).find());
	}

	@Test
	public void testDataForDspVersionGroup(){
		String data1 = "302, 37f,  2, 0, 0, 0, 1, 5, 11 00 00 04 0f 2c      , 060eec22, 19:19:27.339 03-25-2015,RX\r\n";
		String data2 = "333, 37f,  2, 0, 0, 0, 1, 5, 11 00 00 04 0f 2d      , 060eec22, 19:19:27.339 03-25-2015,RX\r\n";
		Pattern pattern = CanLine.regex("302|303|323|333", ".*", "04 0f 2[cd]", ".*");
		assertTrue(pattern.matcher(data1).matches());
		assertTrue(pattern.matcher(data2).matches());
	}

	@Test
	public void testCanLineGroupDongleLogs() {
		try{
			List<String> strings = Arrays.asList(
					"300, 30e,  2, 1, 0, 0, 1, 6, 00 01 00 00 0d 10      , 05f6814e, 18:52:47.130 03-25-2015,RX\r\n",
					"30e, 303,  6, 0, 0, 0, 1, 8, 74 00 06 25 00 00 00 00, 05f6814e, 18:52:47.132 03-25-2015,RX\r\n",
					"303, 30e,  6, 1, 0, 0, 1, 6, 00 01 00 00 00 00      , 05f6815e, 18:52:47.141 03-25-2015,RX\r\n",
					"30e, 302,  c, 0, 0, 0, 1, 8, 74 00 06 25 00 00 00 00, 05f6815e, 18:52:47.142 03-25-2015,RX\r\n",
					"302, 30e,  c, 1, 0, 0, 1, 6, 00 01 00 00 00 00      , 05f6816d, 18:52:47.154 03-25-2015,RX\r\n",
					"322, 37f,  f, 0, 0, 0, 0, 8, 11 00 47 34 4d 7b 30 7d, 05f6816d, 18:52:47.157 03-25-2015,RX\r\n",
					"322, 37f,  f, 0, 0, 1, 0, 8, 20 50 75 74 20 4c 55 42, 05f6816d, 18:52:47.157 03-25-2015,RX\r\n",
					"322, 37f,  f, 0, 0, 2, 0, 8, 47 44 4b 58 20 74 6f 20, 05f6816d, 18:52:47.158 03-25-2015,RX\r\n",
					"322, 37f,  f, 0, 0, 3, 0, 8, 44 72 61 77 65 72 7b 69, 05f6816d, 18:52:47.158 03-25-2015,RX\r\n",
					"322, 37f,  f, 0, 0, 0, 0, 8, 64 3a 35 36 2c 64 72 61, 05f6816d, 18:52:47.159 03-25-2015,RX\r\n",
					"322, 37f,  f, 0, 0, 1, 0, 8, 77 65 72 50 61 74 68 3a, 05f6816d, 18:52:47.159 03-25-2015,RX\r\n",
					"322, 37f,  f, 0, 0, 2, 1, 8, 31 3a 66 3a 30 3a 35 7d, 05f6816d, 18:52:47.160 03-25-2015,RX\r\n",
					"30e, 37f,  c, 0, 0, 0, 0, 8, 11 00 47 34 4d 7b 30 7d, 05f6816d, 18:52:47.161 03-25-2015,RX\r\n",
					"30e, 37f,  c, 0, 0, 1, 0, 8, 20 50 75 74 20 4c 55 42, 05f6816d, 18:52:47.163 03-25-2015,RX\r\n",
					"30e, 37f,  c, 0, 0, 2, 0, 8, 47 44 4b 58 20 74 6f 20, 05f6816d, 18:52:47.164 03-25-2015,RX\r\n",
					"30e, 37f,  c, 0, 0, 3, 0, 8, 44 72 61 77 65 72 7b 69, 05f6817d, 18:52:47.165 03-25-2015,RX\r\n",
					"30e, 37f,  c, 0, 0, 0, 0, 8, 64 3a 35 36 2c 64 72 61, 05f6817d, 18:52:47.166 03-25-2015,RX\r\n",
					"30e, 37f,  c, 0, 0, 1, 0, 8, 77 65 72 50 61 74 68 3a, 05f6817d, 18:52:47.167 03-25-2015,RX\r\n",
					"30e, 37f,  c, 0, 0, 2, 1, 8, 31 3a 66 3a 30 3a 35 7d, 05f6817d, 18:52:47.168 03-25-2015,RX\r\n",
					"322,  fd,  3, 0, 0, 0, 0, 8, 6f 07 00 10 98 00 10 03, 05f6817d, 18:52:47.178 03-25-2015,RX\r\n",
					"322,  fd,  3, 0, 0, 1, 0, 8, 03 01 04 05 02 21 02 03, 05f6817d, 18:52:47.178 03-25-2015,RX\r\n",
					"322,  fd,  3, 0, 0, 2, 1, 4, 01 38 19 01            , 05f6817d, 18:52:47.178 03-25-2015,RX");


			Pattern pattern = CanLine.regex(".*", ".*", ".*", ".*");
			strings.forEach(str -> assertTrue(pattern.matcher(str).matches()));

			Pattern badSourcePattern = CanLine.regex("junk", ".*", ".*", ".*");
			strings.forEach(str -> assertFalse(badSourcePattern.matcher(str).matches()));


		}catch (Exception e){
			e.printStackTrace(System.out);
		}

	}

}
