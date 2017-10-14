package com.sefah.timemeasurement;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.sefah.timemeasurement.exception.InstanceNameAlreadyKnownException;

/**
 * Using:
 * <p>
 * You can create an instance with
 * <p>
 * DurationWatcher.createInstance("Name"); or
 * DurationWatcher.getInstance("Name");
 * <p>
 * With
 * <p>
 * DurationWatcher.getInstance("Name").start("Phase")
 * <p>
 * For the TimerID "Phase" it save the current time in nano seconds. If no timer
 * with the id already known, it do nothing. If you start the same id more then
 * one, the other starts are ignore.
 * <p>
 * DurationWatcher.getInstance("Name").stop("Phase")
 * <p>
 * Stopped the timer with the id "Phase". If no timer with the id known, it do
 * nothing. If you stop the same id more then one, the other stops are ignore.
 * <p>
 * DurationWatcher.getInstance("Name").getTimeStatistics()
 * <p>
 * Gets for all timerIDs the running time in milliseconds.
 * 
 * Example TimeDurationWatcher.initalization(1, 1);
 * TimeDurationWatcher.createInstance("Test");
 * 
 * TimeDurationWatcher.getInstance("Test").start("FirstRun");
 * TimeDurationWatcher.getInstance("Test").stop("FirstRun");
 * TimeDurationWatcher.getInstance("Test").getTimeStatistics();
 * 
 * TimeDurationWatcher.getConfiguration();
 * 
 * @author Sebastian Fahrenholz
 */
public class TimeDurationWatcher {

	private static Map<String, TimeDurationInformation> instanceMap = new LinkedHashMap<>();
	private static Timer cleanUpTimer = new Timer("DurationWatcher-CleanUp");
	private static int cleanUpRunningIntervalDays = 3;
	private static int markOutdatedAfterDays = 3;

	private TimeDurationWatcher() {
		// EMPTY
	}

	/**
	 * Allowed to configured the running interval for the cleanUp Thread <param>cleanUpIntervalDays</param>
	 * and the value, after with days the the timerInstance are outdated about <param>outdatedAfterDays</param>.
	 * <p>The default values are:
	 * <br>- cleanUpIntervalDays: 3 Days
	 * <br>- outdatedAfterDays: 3 Days
	 */
	public static void initalization(final int cleanUpIntervalDays, final int outdatedAfterDays) {
		cleanUpRunningIntervalDays = cleanUpIntervalDays;
		markOutdatedAfterDays = outdatedAfterDays;
		
		createCleanUpTimer();
	}

	/**
	 * Create an instance with the <code>instanceName</code>.
	 * 
	 * @throws InstanceNameAlreadyKnownException
	 */
	public static void createInstance(final String instanceName) throws InstanceNameAlreadyKnownException {
		if (instanceMap.containsKey(instanceName)) {
			throw new InstanceNameAlreadyKnownException("The instanceName '" + instanceName + "' is already known.");
		}

		instanceMap.put(instanceName, new TimeDurationInformation(markOutdatedAfterDays));
	}

	/**
	 * Create an instance with the <code>instanceName</code> and return the
	 * DurationInformation-Object
	 * 
	 * @throws InstanceNameAlreadyKnownException
	 */
	public static TimeDurationInformation getInstance(final String instanceName)
			throws InstanceNameAlreadyKnownException {
		if (!instanceMap.containsKey(instanceName)) {
			createInstance(instanceName);
		}

		return instanceMap.get(instanceName);
	}

	/**
	 * Clear the instance with the <code>instanceName</code> if exists.
	 */
	public static void clearInstance(final String instanceName) {
		if (!instanceMap.containsKey(instanceName)) {
			instanceMap.remove(instanceName);
		}
	}

	public static String getConfiguration() {
		final StringBuilder sb = new StringBuilder();
		sb.append("cleanUpRunningIntervalDays=").append(cleanUpRunningIntervalDays).append("\n");
		sb.append("markOutdatedAfterDays=").append(markOutdatedAfterDays).append("\n");

		return sb.toString();
	}

	public static void destroy() {
		cleanUpTimer.cancel();
		cleanUpTimer = null;
		instanceMap = new LinkedHashMap<>();
	}

	private static void createCleanUpTimer() {
		cleanUpTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				for (final Entry<String, TimeDurationInformation> instance : instanceMap.entrySet()) {
					if (instance.getValue().isOutdated()) {
						clearInstance(instance.getKey());
					}
				}
			}
		}, 0, TimeUnit.DAYS.toMillis(cleanUpRunningIntervalDays));
	}
}