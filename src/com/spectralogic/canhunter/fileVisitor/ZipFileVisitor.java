package com.spectralogic.canhunter.fileVisitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.scene.control.Label;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import com.spectralogic.canhunter.canLogSearch.CanLine;
import com.spectralogic.canhunter.canLogSearch.CanLogSearcher;
import com.spectralogic.canhunter.canLogSearch.CanMetric;
import com.spectralogic.canhunter.canLogSearch.CanMetricAverages;
import com.spectralogic.canhunter.canLogSearch.SeeEssVeeAble;

public class ZipFileVisitor extends SimpleFileVisitor<Path> {

	private final Pattern filter;
	private final File outputDirectory;
	private final Label statusLabel;
	private long fileCount = 0;
	public boolean wantFilesSorted;
	public boolean calculateMetrics;

	public ZipFileVisitor(Pattern filter, File outputDirectory, Label statusLabel) {
		this.filter = filter;
		this.outputDirectory = outputDirectory;
		this.statusLabel = statusLabel;
	}

	@Override
	public FileVisitResult visitFile(Path path, BasicFileAttributes attr) {

		if(path.toString().endsWith(".zip")){
			File tempDir = new File(path.getParent().toString() + "/" + System.currentTimeMillis());
			tempDir.mkdir();

			try {
				ZipFile zip = new ZipFile(path.toFile());
				zip.extractAll(tempDir.getPath());

				Arrays.asList(tempDir.listFiles()).forEach(file -> visitCanLog(file.toPath()));
				Arrays.asList(tempDir.listFiles()).forEach(file -> file.delete());


			} catch (ZipException e) {
				e.printStackTrace();
			} finally{
				tempDir.delete();
			}
		} else {
			visitCanLog(path);
		}

		return FileVisitResult.CONTINUE;


	}

	private void visitCanLog(Path path){
		try {
			int dspVersion = digestLog(path);
			if(wantFilesSorted) sortFile(path.toFile(), dspVersion);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sortFile(File file, int dspVersion) throws IOException{
		if(dspVersion == 1){
			File dsp1dir = new File(outputDirectory.getAbsolutePath() + "/dsp1/");
			dsp1dir.mkdirs();
			File filecopy = new File(outputDirectory.getAbsolutePath() + "/dsp1/" + System.currentTimeMillis() + "_" + file.getName());
			Files.copy(file.toPath(), filecopy.toPath());
		} else if(dspVersion == 2){
			File dsp2dir = new File(outputDirectory.getAbsolutePath() + "/dsp2/");
			dsp2dir.mkdirs();
			File filecopy = new File(outputDirectory.getAbsolutePath() + "/dsp2/" + System.currentTimeMillis() + "_" + file.getName());
			Files.copy(file.toPath(), filecopy.toPath());
		}

	}

	private int digestLog(Path path) throws IOException {
		int dspVersion = 0;
		fileCount++;
		if(path.toString().endsWith(".log")){

			Platform.runLater(()->statusLabel.setText("LogsFinished: " + fileCount + ", Parsing log: " + path.toString()));

			if(wantFilesSorted){
				dspVersion = determineDspVersionFromFwRequest(path, dspVersion);
			}

			//Actually look at logs
			CanLogSearcher searcher = new CanLogSearcher(path.toFile(), filter);
			List<List<CanLine>> packets = searcher.search();
			String resultsPath = outputDirectory.getPath() + "/" + path.toString().replace("/", "_");

			if(wantFilesSorted){
				dspVersion = determineDspVersionFromMetrics(dspVersion, packets);
			}

			//write out filtered can logs
			//			List<SeeEssVeeAble> flattenedPackets = new ArrayList<SeeEssVeeAble>();
			//			packets.forEach(flattenedPackets::addAll);
			//			File outputFile = new File(resultsPath + ".csv");
			//			writeToFile(flattenedPackets, outputFile);

			//write out metrics
			if(calculateMetrics) parseAndWriteMetrics(packets);

		}
		return dspVersion;
	}

	private void parseAndWriteMetrics(List<List<CanLine>> packets) {
		List<SeeEssVeeAble> metrics = parseAllMetrics(packets);
		File metricsResutls = new File(outputDirectory.getPath() + "/metrics.csv");
		writeToFile(metrics, metricsResutls);
	}

	private int determineDspVersionFromMetrics(int dspVersion, List<List<CanLine>> packets) throws IOException {
		for(List<CanLine> packet: packets){
			if(packet.get(0).data.contains("11 00 41 21")){
				if(dspVersion == 2){
					throw new IOException("log has both dsp versions in it. 2 then 1.");
				} else {
					dspVersion = 1;
				}
			}
		}
		return dspVersion;
	}

	private int determineDspVersionFromFwRequest(Path path, int dspVersion) throws IOException {
		Pattern dspVersionFilter = CanLine.regex("302|303|323|333", ".*", "04 0f 2[cd]", ".*");
		CanLogSearcher dspVersionSearcher = new CanLogSearcher(path.toFile(), dspVersionFilter);
		List<List<CanLine>> dspPackets = dspVersionSearcher.search();
		for(List<CanLine> packet: dspPackets){
			if(packet.get(0).data.contains("04 0f 2c")){
				if(dspVersion == 2){
					throw new IOException("log has both dsp versions in it. 2 then 1.");
				} else {
					dspVersion = 1;
				}
			}else if(packet.get(0).data.contains("04 0f 2d")){
				if(dspVersion == 1){
					throw new IOException("log has both dsp versions in it. 1 then 2.");
				} else {
					dspVersion = 2;
				}
			}
		}
		return dspVersion;
	}



	private List<SeeEssVeeAble> parseAllMetrics(List<List<CanLine>> packets) {
		List<SeeEssVeeAble> metrics = new ArrayList<SeeEssVeeAble>();
		for(List<CanLine> packet: packets){
			try {
				metrics.add(CanMetric.create(packet));
			} catch (IOException | NumberFormatException | IndexOutOfBoundsException e) {

				e.printStackTrace();
			}
		}
		return metrics;
	}

	private SeeEssVeeAble summrizeMetrics(List<SeeEssVeeAble> metrics) {
		CanMetricAverages compiledMetrics = new CanMetricAverages();
		for(SeeEssVeeAble metric: metrics){
			compiledMetrics.add((CanMetric)metric);
		}
		return compiledMetrics;
	}

	private void writeToFile(List<SeeEssVeeAble> packets, File outputFile) {
		if(packets.size() > 0){

			try(FileWriter fileWriter = new FileWriter(outputFile, true)){

				if(outputFile.length() == 0 ){
					fileWriter.append(packets.get(0).csvHeaders());
				}

				for(SeeEssVeeAble line: packets){
					fileWriter.append(line.toCsv());
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
