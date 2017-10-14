package com.sefah.timemeasurement;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class TimeInformationTest {
	
	@Test
	public void testObject() throws Exception {
		TimeInformation timer = new TimeInformation();
		assertThat(timer.getStartTime(), CoreMatchers.is(-1l));
		assertThat(timer.getEndTime(), CoreMatchers.is(-1l));
		assertThat(timer.getRunningTime(), CoreMatchers.is("<Timer was not started.>"));
	}
	

	@Test
	public void testWorkingObject() throws Exception {
		TimeInformation timer = new TimeInformation();
		assertThat(timer.getStartTime(), CoreMatchers.is(-1l));
		assertThat(timer.getEndTime(), CoreMatchers.is(-1l));
		
		timer.start();
		
		TimeUnit.MILLISECONDS.sleep(10);
		
		timer.end();
		
		assertThat(timer.getRunningTime(), CoreMatchers.is(CoreMatchers.notNullValue()));
		assertThat(timer.getRunningTime(), CoreMatchers.containsString("ms"));
	}

}
