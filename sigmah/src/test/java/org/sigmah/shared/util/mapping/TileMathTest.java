package org.sigmah.shared.util.mapping;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.Closeable;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.sigmah.shared.report.content.LatLng;
import org.sigmah.shared.report.content.Point;

public class TileMathTest {

	@Test
	public void inverse() {
		LatLng latlng = new LatLng(15, 30);
		Point px = TileMath.fromLatLngToPixel(latlng, 6);
		
		LatLng inverse = TileMath.inverse(px, 6);
		
		assertThat("longitude", inverse.getLng(), equalTo(latlng.getLng()));
		assertThat("latitude", inverse.getLat(), closeTo(latlng.getLat(), 0.0001));
	}

	private Matcher<Double> closeTo(final double x, final double epsilon) {
		return new TypeSafeMatcher<Double>() {

			@Override
			public void describeTo(Description d) {
				d.appendText("within ").appendValue(d)
					.appendText(" of ").appendValue(x);
			}

			@Override
			public boolean matchesSafely(Double item) {
				return Math.abs(item - x) < epsilon;
			}
			
		};
	}
	
}
