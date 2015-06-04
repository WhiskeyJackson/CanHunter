package com.spectralogic.canhunter.canLogSearch;

import java.util.HashMap;

public class CanMetricAverages implements SeeEssVeeAble {

	int version1Count = 0;
	int version2Count = 0;
	HashMap<String, CanMetric> version1Metrics = new HashMap<String, CanMetric>();
	HashMap<String, CanMetric> version2Metrics = new HashMap<String, CanMetric>();

	public void add(CanMetric metric) {
		if(metric.version == 1){
			if(version1Metrics.get(metric.sourceMotor) == null){
				version1Metrics.put(metric.sourceMotor, metric);

			} else {
				CanMetric tempMetric = version1Metrics.get(metric.sourceMotor);
				tempMetric.version += metric.version;
				tempMetric.moveCount += metric.moveCount;
				tempMetric.moveTarget += metric.moveTarget;
				tempMetric.currentPosition += metric.currentPosition;
				tempMetric.postMoveMeanError += metric.postMoveMeanError;
				tempMetric.postMoveErrorVariance += metric.postMoveErrorVariance;
				tempMetric.postMoveMaxPositiveError += metric.postMoveMaxPositiveError;
				tempMetric.postMoveMaxNegativeError += metric.postMoveMaxNegativeError;
				tempMetric.overallPostMoveMeanError += metric.overallPostMoveMeanError;
				tempMetric.overallPostMoveErrorVariance += metric.overallPostMoveErrorVariance;
				tempMetric.overallPostMoveMaxPositiveError += metric.overallPostMoveErrorVariance;
				tempMetric.overallPostMoveMaxNegativeError += metric.overallPostMoveMaxNegativeError;

				version1Metrics.put(tempMetric.sourceMotor, tempMetric);
			}
			version1Count++;

		} else if(metric.version == 2){
			if(version2Metrics.get(metric.sourceMotor) == null){
				version2Metrics.put(metric.sourceMotor, metric);

			}else {
				CanMetric tempMetric = version2Metrics.get(metric.sourceMotor);
				tempMetric.version += metric.version;
				tempMetric.moveCount += metric.moveCount;
				tempMetric.moveTarget += metric.moveTarget;
				tempMetric.currentPosition += metric.currentPosition;
				tempMetric.postMoveMeanError += metric.postMoveMeanError;
				tempMetric.postMoveErrorVariance += metric.postMoveErrorVariance;
				tempMetric.postMoveMaxPositiveError += metric.postMoveMaxPositiveError;
				tempMetric.postMoveMaxNegativeError += metric.postMoveMaxNegativeError;
				tempMetric.overallPostMoveMeanError += metric.overallPostMoveMeanError;
				tempMetric.overallPostMoveErrorVariance += metric.overallPostMoveErrorVariance;
				tempMetric.overallPostMoveMaxPositiveError += metric.overallPostMoveErrorVariance;
				tempMetric.overallPostMoveMaxNegativeError += metric.overallPostMoveMaxNegativeError;

				tempMetric.failedToleranceMoveCompleteFlag += metric.failedToleranceMoveCompleteFlag;
				tempMetric.badMotorParametersFlag += metric.badMotorParametersFlag;
				tempMetric.postMoveCompleteMeanPwm += metric.postMoveCompleteMeanPwm;
				tempMetric.postMoveCompletePwmVariance += metric.postMoveCompletePwmVariance;
				tempMetric.unSCurveFreezeTimeMillis += metric.unSCurveFreezeTimeMillis;
				tempMetric.postMoveIsrCnt += metric.postMoveIsrCnt;
				tempMetric.inDeadBand += metric.inDeadBand;
				tempMetric.inSoftBand += metric.inSoftBand;
				tempMetric.inHardBand += metric.inHardBand;
				tempMetric.motorRunningTimeMillis += metric.motorRunningTimeMillis;
				tempMetric.motorRunningPwmSum += metric.motorRunningPwmSum;

				version2Metrics.put(tempMetric.sourceMotor, tempMetric);
			}
			version2Count++;
		}

	}

	@Override
	public String toCsv() {
		StringBuilder csv = new StringBuilder();
		for(CanMetric metric: version1Metrics.values()){
			csv.append(metric.toCsv().trim());
			csv.append(", " + version1Count + "\r\n");
		}

		for(CanMetric metric: version2Metrics.values()){
			csv.append(metric.toCsv().trim());
			csv.append(", " + version2Count + "\r\n");
		}
		return csv.toString();
	}

	@Override
	public String csvHeaders() {
		CanMetric metric = new CanMetric();
		return metric.csvHeaders().trim() + ", count\r\n";
	}

}
