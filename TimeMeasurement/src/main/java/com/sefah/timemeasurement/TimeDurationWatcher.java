package com.sefah.timemeasurement;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.sefah.timemeasurement.exception.InstanceNameAlreadyKnownException;
import com.sefah.timemeasurement.object.TimeDurationInformation;

/**
 * Using:
 * You can create an instance with
 * <p>
 * TimeDurationWatcher.createInstance("Name"); or
 * TimeDurationWatcher.getInstance("Name");
 * <p>
 * With
 * <p>
 * For the TimerID "Phase" it save the current time in nano seconds. If no timer
 * with the id already known, it do nothing. If you start the same id more then
 * one, the other starts are ignore.
 * <p>
 * TimeDurationWatcher.getInstance("Name").start("Phase") <br />
 * TimeDurationWatcher.getInstance("Name").stop("Phase")
 * <p>
 * Stopped the timer with the id "Phase". If no timer with the id known, it do
 * nothing. If you stop the same id more then one, the other stops are ignore.
 * <p>
 * TimeDurationWatcher.getInstance("Name").getTimeStatistics()
 * <p>
 * Gets for all timerIDs the running time in milliseconds.
 * 
 * Example TimeDurationWatcher.initalization(1, 1); <br />
 * TimeDurationWatcher.createInstance("Test"); <br />
 * 
 * TimeDurationWatcher.getInstance("Test").start("FirstRun"); <br />
 * TimeDurationWatcher.getInstance("Test").stop("FirstRun"); <br />
 * TimeDurationWatcher.getInstance("Test").getTimeStatistics(); <br />
 * 
 * TimeDurationWatcher.getConfiguration(); <br />
 * 
 * @author Sebastian Fahrenholz
 */
public class TimeDurationWatcher {

	private static final String CFG_MARK_OUTDATED_AFTER_DAYS = "markOutdatedAfterDays=";
	private static final String CFG_CLEAN_UP_RUNNING_INTERVAL_DAYS = "cleanUpRunningIntervalDays=";

	private static Map<String, TimeDurationInformation> instanceMap = new LinkedHashMap<>();
	private static Timer cleanUpTimer = new Timer("DurationWatcher-CleanUp");
	private static int cleanUpRunningIntervalDays;
	private static int markOutdatedAfterDays;

	private TimeDurationWatcher() {
		// NO SONAR
	}
	
	/**
	 * Allowed to configured the running interval for the cleanUp Thread <param>cleanUpIntervalDays</param>
	 * and the value, after with days the the timerInstance are outdated about <param>outdatedAfterDays</param>.
	 * <p>The default values are:
	 * <br>- cleanUpIntervalDays: 3 Days
	 * <br>- outdatedAfterDays: 3 Days
	 */
	public static void initalization() {
		initalization(3, 3);
	}

	/**
	 * Allowed to configured the running interval for the cleanUp Thread <param>cleanUpIntervalDays</param>
	 * and the value, after with days the the timerInstance are outdated about <param>outdatedAfterDays</param>.
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
		sb.append(CFG_CLEAN_UP_RUNNING_INTERVAL_DAYS).append(cleanUpRunningIntervalDays).append("\n");
		sb.append(CFG_MARK_OUTDATED_AFTER_DAYS).append(markOutdatedAfterDays).append("\n");

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