/**
 * 
 */
package com.sefah.timemeasurement.object;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.sefah.timemeasurement.object.TimeDurationInformation;

/**
 * @author Sebastian Fahrenholz
 *
 */
public class TimeDurationInformationTest {
	
	@Test
	public void testDefaultInitalzation() throws Exception {
		TimeDurationInformation object = new TimeDurationInformation(1);
		
		assertThat(object.getTimeStatistics(), CoreMatchers.is(""));
		assertThat(object.isOutdated(), CoreMatchers.is(false));
		object.clear("");
		object.stop("");
		object.start("");
	}

	@Test
	public void testWorkflow() throws Exception {
		TimeDurationInformation object = new TimeDurationInformation(1);
		
		object.start("TestCase1");
		object.start("TestCase2");
		
		TimeUnit.MILLISECONDS.sleep(10);
		object.start("TestCase1.1");
		TimeUnit.MILLISECONDS.sleep(10);
		object.stop("TestCase1.1");
		object.stop("TestCase1.2");
		object.start("TestCase1.1.1");
		TimeUnit.MILLISECONDS.sleep(100);
		object.stop("TestCase2");
		object.stop("TestCase1.1.1");
		object.stop("TestCase1");
		
		assertThat(object.getTimeStatistics(), CoreMatchers.notNullValue());
		assertThat(object.getTimeStatistics(), CoreMatchers.containsString("TestCase1"));
		assertThat(object.getTimeStatistics(), CoreMatchers.not(CoreMatchers.containsString("<Timer was not stopped.>")));
		assertThat(object.getTimeStatistics(), CoreMatchers.containsString("TestCase2"));
		assertThat(object.getTimeStatistics(), CoreMatchers.containsString("TestCase1.1"));
		assertThat(object.getTimeStatistics(), CoreMatchers.containsString("TestCase1.1.1"));
		
	}
}
