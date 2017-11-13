package com.sefah.timemeasurement.object;

import java.util.concurrent.TimeUnit;

/**
 * @author Sebastian Fahrenholz
 */
public class TimeInformation {
	private long startTime = -1;
	private long endTime =-1;

	public void start() {
		if (startTime == -1) {
			startTime = System.nanoTime();
		}
	}

	public void end() {
		if (endTime == -1) {
			endTime = System.nanoTime();
		}
	}

	public long getEndTime() {
		return endTime;
	}

	public long getStartTime() {
		return startTime;
	}

	/**
	 * Returns a String with the duration in in milliseconds.
	 * If the timer was not started or stopped, if will return a string.
	 * Can not be <code>null</code>
	 */
	public String getRunningTime() {
		if (startTime == -1) {
			return "<Timer was not started.>";
		}

		if (endTime == -1) {
			return "<Timer was not stopped.>";
		}

		return TimeUnit.NANOSECONDS.toMillis(endTime - startTime) + " ms";
	}
}
