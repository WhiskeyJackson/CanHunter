package com.spectralogic.canhunter.canLogSearch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CanLine implements SeeEssVeeAble{

	public String source = "";
	public String destination = "";
	public String data = "";
	public String xid = "";
	public String responseBit = "";
	public String statusBit = "";
	public String sequence = "";
	public String eop = "";
	public String frameSize = "";
	public String timeStamp = "";


	public static Pattern regex(String canSource, String canDest, String canData, String xid) {



		canSource = "(" + canSource + ")";
		canDest = "(" + canDest + ")";
		canData = "([^,]*" + canData + "[^,]*)";
		//String xidGroup = "([0-9A-Fa-f])";
		String xidGroup = "(" + xid + ")";
		String responseBitGroup = "(0|1)";
		String statusBitGroup = "(0|1)";
		String sequenceDigitGroup = "([0-3])";
		String endOfPacketBitGroup = "(0|1)";
		String sizeGroup = "([1-8])";
		String commaSpace = ",\\s+";
		String mysteryhexGroup = "([0-9A-Fa-f]*)";
		String timeStampGroup = "([^,]+)";
		String lineEnd = ".*\\s*";

		StringBuilder builder = new StringBuilder();

		builder.append(canSource);
		builder.append(commaSpace);
		builder.append(canDest);
		builder.append(commaSpace);
		builder.append(xidGroup);
		builder.append(commaSpace);
		builder.append(responseBitGroup);
		builder.append(commaSpace);
		builder.append(statusBitGroup);
		builder.append(commaSpace);
		builder.append(sequenceDigitGroup);
		builder.append(commaSpace);
		builder.append(endOfPacketBitGroup);
		builder.append(commaSpace);
		builder.append(sizeGroup);
		builder.append(commaSpace);
		builder.append(canData);
		builder.append(commaSpace);
		builder.append(mysteryhexGroup);
		builder.append(commaSpace);
		builder.append(timeStampGroup);
		builder.append(lineEnd);

		return Pattern.compile(builder.toString());
	}




	public static CanLine create(Matcher matcher){
		CanLine line = new CanLine();
		line.source = matcher.group(1);
		line.destination = matcher.group(2);
		line.xid = matcher.group(3);
		line.responseBit = matcher.group(4);
		line.statusBit = matcher.group(5);
		line.sequence = matcher.group(6);
		line.eop = matcher.group(7);
		line.frameSize = matcher.group(8);
		line.data = matcher.group(9);
		line.timeStamp = matcher.group(11);

		return line;

	}

	@Override
	public String toCsv(){
		return source + ", " + destination + ", " + xid + ", " + responseBit + ", " + statusBit + ", " + sequence + ", " + eop
				+ ", " + frameSize + ", " + data + ", " + timeStamp + "\r\n";
	}

	@Override
	public String toString() {
		return "CanLine [source=" + source + ", destination=" + destination + ", data=" + data + ", xid=" + xid + ", responseBit="
				+ responseBit + ", statusBit=" + statusBit + ", sequence=" + sequence + ", eop=" + eop + ", frameSize=" + frameSize
				+ ", timeStamp=" + timeStamp + "]";
	}

	public static Pattern universalRegex() {
		return regex(".*", ".*", ".*", "[0-9A-Fa-f]");
	}

	@Override
	public String csvHeaders() {
		return "source, destination, xid, responseBit, statusBit, sequence, eop,"
				+ "frameSize, data, timeStamp\r\n";
	}


}
