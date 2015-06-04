package com.spectralogic.canhunter.canLogSearch;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

public class CanMetric implements SeeEssVeeAble  {

	String sourceMotor = "";
	short version = 0;
	short moveCount = 0;
	long moveTarget = 0;
	long currentPosition = 0;
	short postMoveMeanError = 0;
	short postMoveErrorVariance = 0;
	short postMoveMaxPositiveError = 0;
	short postMoveMaxNegativeError = 0;
	short overallPostMoveMeanError = 0;
	short overallPostMoveErrorVariance = 0;
	short overallPostMoveMaxPositiveError = 0;
	short overallPostMoveMaxNegativeError = 0;

	short failedToleranceMoveCompleteFlag = 0;
	short badMotorParametersFlag = 0;
	short postMoveCompleteMeanPwm = 0;
	short postMoveCompletePwmVariance = 0;
	short unSCurveFreezeTimeMillis = 0;
	short postMoveIsrCnt = 0;
	long inDeadBand = 0;
	long inSoftBand = 0;
	long inHardBand = 0;
	long motorRunningTimeMillis = 0;
	short motorRunningPwmSum = 0;


	public static CanMetric create(List<CanLine> packet) throws IOException {

		CanMetric metric = new CanMetric();
		metric.sourceMotor = packet.get(0).source;

		if(packet.get(0).data.contains("11 00 41 21")){
			if(packet.size() != 4){
				throw new IOException("Version 1 stats with wrong packet size: " +  packet.size());
			}
			metric.version = 1;
		} else if(packet.get(0).data.contains("11 00 41 40")){
			if( packet.size() != 8){
				throw new IOException("Version 2 stats with wrong packet size: " +  packet.size());
			}
			metric.version = 2;
		} else {
			throw new IOException("Packet is not a motor metric");
		}

		parseVersion1Stats(metric, packet);

		if(metric.version == 2){
			parseVersion2Stats(metric, packet);
		}


		return metric;
	}


	private static void parseVersion2Stats(CanMetric metric, List<CanLine> packet) {
		metric.failedToleranceMoveCompleteFlag = parseSignedShortData(packet.get(4), IntStream.of(0));;
		metric.badMotorParametersFlag = parseSignedShortData(packet.get(4), IntStream.of(1));;
		metric.postMoveCompleteMeanPwm = parseSignedShortData(packet.get(4), IntStream.of(2,3));;
		metric.postMoveCompletePwmVariance = parseSignedShortData(packet.get(4), IntStream.of(4,5));;
		metric.unSCurveFreezeTimeMillis = parseSignedShortData(packet.get(4), IntStream.of(6,7));;
		metric.postMoveIsrCnt = parseSignedShortData(packet.get(5), IntStream.of(0,1,2,3));;
		metric.inDeadBand = parseLongData(packet.get(5), IntStream.of(4,5,6,7));;
		metric.inSoftBand = parseLongData(packet.get(6), IntStream.of(0,1,2,3));;
		metric.inHardBand = parseLongData(packet.get(6), IntStream.of(4,5,6,7));;
		metric.motorRunningTimeMillis = parseLongData(packet.get(7), IntStream.of(0,1,2,3));;
		metric.motorRunningPwmSum = parseSignedShortData(packet.get(7), IntStream.of(4,5,6,7));;

	}


	private static void parseVersion1Stats(CanMetric metric, List<CanLine> packet) {

		metric.moveCount = parseSignedShortData(packet.get(0), IntStream.of(6,7));
		metric.moveTarget = parseLongData(packet.get(1), IntStream.of(0,1,2,3));
		metric.currentPosition = parseLongData(packet.get(1), IntStream.of(4,5,6,7));
		metric.postMoveMeanError = parseSignedShortData(packet.get(2), IntStream.of(0,1));
		metric.postMoveErrorVariance = parseSignedShortData(packet.get(2), IntStream.of(2,3));
		metric.postMoveMaxPositiveError = parseSignedShortData(packet.get(2), IntStream.of(4,5));
		metric.postMoveMaxNegativeError = parseSignedShortData(packet.get(2), IntStream.of(6,7));
		metric.overallPostMoveMeanError = parseSignedShortData(packet.get(3), IntStream.of(0,1));
		metric.overallPostMoveErrorVariance = parseSignedShortData(packet.get(3), IntStream.of(2,3));
		metric.overallPostMoveMaxPositiveError = parseSignedShortData(packet.get(3), IntStream.of(4,5));
		metric.overallPostMoveMaxNegativeError = parseSignedShortData(packet.get(3), IntStream.of(6,7));

	}

	private static short parseSignedShortData(CanLine line, IntStream range){
		String[] dataBytes = line.data.split(" ");
		StringBuilder dataFromRange = new StringBuilder();
		range.forEach(i -> dataFromRange.append(dataBytes[i]));
		return Integer.valueOf(dataFromRange.toString(), 16).shortValue();
	}

	private static Long parseLongData(CanLine line, IntStream range){
		String[] dataBytes = line.data.split(" ");
		StringBuilder dataFromRange = new StringBuilder();
		range.forEach(i -> dataFromRange.append(dataBytes[i]));
		return Long.parseLong(dataFromRange.toString(), 16);
	}



	@Override
	public String toCsv(){

		return sourceMotor + ", " + version + ", " + moveCount + ", " + moveTarget + ", " + currentPosition + ", " + postMoveMeanError + ", " + postMoveErrorVariance
				+ ", " + postMoveMaxPositiveError + ", " + postMoveMaxNegativeError	+ ", " + overallPostMoveMeanError + ", "
				+ overallPostMoveErrorVariance + ", " + overallPostMoveMaxPositiveError	+ ", " + overallPostMoveMaxNegativeError + ", "
				+ failedToleranceMoveCompleteFlag + ", " + badMotorParametersFlag + ", " + postMoveCompleteMeanPwm + ", "
				+ postMoveCompletePwmVariance + ", " + unSCurveFreezeTimeMillis + ", " + postMoveIsrCnt + ", " + inDeadBand + ", "
				+ inSoftBand + ", " + inHardBand + ", " + motorRunningTimeMillis + ", " + motorRunningPwmSum + "\r\n";
	}

	@Override
	public String csvHeaders(){
		return "sourceMotor, version, moveCount, moveTarget, currentPosition, postMoveMeanError, postMoveErrorVariance, "
				+ "postMoveMaxPositiveError, postMoveMaxNegativeError, overallPostMoveMeanError, "
				+ "overallPostMoveErrorVariance, overallPostMoveMaxPositiveError, overallPostMoveMaxNegativeError, "
				+ "failedToleranceMoveCompleteFlag, badMotorParametersFlag, postMoveCompleteMeanPwm, "
				+ "postMoveCompletePwmVariance, unSCurveFreezeTimeMillis, postMoveIsrCnt, inDeadBand, "
				+ "inSoftBand, inHardBand, motorRunningTimeMillis, motorRunningPwmSum\r\n";
	}


}
