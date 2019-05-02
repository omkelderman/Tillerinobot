package org.tillerino.ppaddict.util;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.MDC;

import org.tillerino.ppaddict.util.MdcUtils.MdcAttributes;
import org.tillerino.ppaddict.util.MdcUtils.MdcSnapshot;

public class MdcUtilsTest {

	@Test
	public void keyIsAddedAndRemoved() throws Exception {
		try (AutoCloseable with = MdcUtils.with("foo", "baz")) {
			assertThat(MDC.get("foo")).isEqualTo("baz");
		}
		assertThat(MDC.get("foo")).isNull();
	}

	@Test
	public void keyIsAddedAndRestored() throws Exception {
		MDC.put("foo", "bar");
		try (AutoCloseable with = MdcUtils.with("foo", "baz")) {
			assertThat(MDC.get("foo")).isEqualTo("baz");
		}
		assertThat(MDC.get("foo")).isEqualTo("bar");
	}

	@Test
	public void chained() throws Exception {
		MDC.put("foo", "bar");
		MDC.put("foos", "bars");
		try (MdcAttributes with = MdcUtils.with("foo", "baz")) {
			with.add("foos", "bazs");
			assertThat(MDC.get("foo")).isEqualTo("baz");
			assertThat(MDC.get("foos")).isEqualTo("bazs");
		}
		assertThat(MDC.get("foo")).isEqualTo("bar");
		assertThat(MDC.get("foos")).isEqualTo("bars");
	}

	@Test
	public void snapShot() throws Exception {
		MDC.put("foo", "bar");
		MDC.put("foos", "bars");
		MdcSnapshot snapshot = MdcUtils.getSnapshot();
		MDC.put("foo", "baz");
		MDC.remove("foos");
		try (MdcAttributes with = snapshot.apply()) {
			assertThat(MDC.get("foo")).isEqualTo("bar");
			assertThat(MDC.get("foos")).isEqualTo("bars");
		}
		assertThat(MDC.get("foo")).isEqualTo("baz");
		assertThat(MDC.get("foos")).isNull();
	}

}
