package com.sefah.timemeasurement;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * @author Sebastian Fahrenholz
 */
public class TimeDurationInformation {

	private Map<String, TimeInformation> instanceMap = new LinkedHashMap<>();
	private int maxIDValueLength;
	private int outdatedAfterDays;
	private long creationTime;
	private long lastUsedTime;

	TimeDurationInformation(final int markOutdatedAfterDays) {
		this.creationTime = System.nanoTime();
		this.outdatedAfterDays = markOutdatedAfterDays;
	}

	public void start(final String idValue) {
		if (!instanceMap.containsKey(idValue)) {
			final TimeInformation timeInformation = new TimeInformation();
			timeInformation.start();
			instanceMap.put(idValue, timeInformation);

			checkIDValueLength(idValue);
			updateLastUsedTime();
		}
	}

	private void checkIDValueLength(final String idValue) {
		maxIDValueLength = idValue.length() > maxIDValueLength ? idValue.length() : maxIDValueLength;
	}

	public void stop(final String idValue) {
		if (instanceMap.containsKey(idValue)) {
			instanceMap.get(idValue).end();
			updateLastUsedTime();
		}
	}

	private void updateLastUsedTime() {
		lastUsedTime = System.nanoTime();
	}

	public void clear(final String idValue) {
		if (instanceMap.containsKey(idValue)) {
			instanceMap.remove(idValue);
		}
	}

	public String getTimeStatistics() {
		final StringBuilder sb = new StringBuilder();

		for (final Entry<String, TimeInformation> instance : instanceMap.entrySet()) {
			final String key = pad(instance.getKey());
			final String runningTime = instance.getValue().getRunningTime();

			sb.append(key);
			sb.append("\t");
			sb.append(runningTime);
			sb.append("\n");
		}

		return sb.toString();
	}

	private String pad(final String str) {
		final StringBuilder padded = new StringBuilder(str);
		while (padded.length() < maxIDValueLength) {
			padded.append(' ');
		}
		return padded.toString();
	}

	public boolean isOutdated() {
		return (lastUsedTime - creationTime) > TimeUnit.DAYS.toNanos(outdatedAfterDays);
	}
}
