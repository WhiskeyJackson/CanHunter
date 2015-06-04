package com.spectralogic.canhunter.canLogSearch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CanLogSearcher {

	private final File canLog;
	private final Pattern lineFilter;
	private final Pattern universalMatcher = CanLine.universalRegex();
	private final Pattern fwPattern = CanLine.regex(".*", "303|302|323|333", "25 00 00 00", "[0-9A-Fa-f]");
	private final boolean lookingFrFirmwareResponse = false;
	private CanLine firmwareRequest;
	private Pattern fwResponse;

	public CanLogSearcher(File file, Pattern filter) {
		canLog = file;
		lineFilter = filter;
	}

	public List<List<CanLine>> search() {
		List<List<CanLine>> filteredPackets = new ArrayList<List<CanLine>>();

		try (Stream<String> lines = Files.lines(canLog.toPath())){

			Iterator<String> lineIterator = lines.iterator();

			while(lineIterator.hasNext()){
				String line = lineIterator.next();


				Matcher matcher = lineFilter.matcher(line);
				if(matcher.matches()){
					CanLine startOfPacket = CanLine.create(matcher);
					List<CanLine> packet = parsePacket(lineIterator, startOfPacket);
					filteredPackets.add(packet);
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filteredPackets;
	}

	private List<CanLine> parsePacket(Iterator<String> lineIterator, CanLine startOfPacket) {

		List<CanLine> packet = new ArrayList<CanLine>();
		packet.add(startOfPacket);
		if(startOfPacket.eop.equals("0")){
			while(lineIterator.hasNext() ){

				Matcher nextMatcher = universalMatcher.matcher(lineIterator.next());
				if(nextMatcher.matches()){
					CanLine nextLine = CanLine.create(nextMatcher);
					if(nextLine.xid.equals(startOfPacket.xid) && nextLine.source.equals(startOfPacket.source) && nextLine.destination.equals(startOfPacket.destination)){
						packet.add(nextLine);
						if(nextLine.eop.equals("1")){
							break;
						}
					}
				}

			}
		}
		return packet;

	}
}
