package com.zsbreak.InvertibleBloomFilter;


import java.util.Random;

import org.junit.Test;

import junit.framework.TestCase;

public class StrataEstimatorTest  extends TestCase {
	static Random r = new Random();

	@Test
	public void testDecode() throws Exception {

		final int TEST_SIZE = 10000;
		final int DIFF_SIZE = 10;
		int s1[] = new int[TEST_SIZE];
		int s2[] = new int[TEST_SIZE];
	
		for (int i = 0; i < TEST_SIZE - DIFF_SIZE; i++) {
			int val = r.nextInt(99999999);
			s1[i] = val;
			s2[i] = val;
		}

		for (int i = TEST_SIZE - DIFF_SIZE; i < TEST_SIZE; i++) {
			int val = r.nextInt(99999999);
			s1[i] = val;
		}

		for (int i = TEST_SIZE - DIFF_SIZE; i < TEST_SIZE; i++) {
			int val = r.nextInt(99999999);
			s2[i] = val;
		}

		StrataEstimator se1 = new StrataEstimator();
		StrataEstimator se2 = new StrataEstimator();
		se1.encode(s1);
		se2.encode(s2);
		assert(DIFF_SIZE*2==se1.decode(se2));
		
	}

}
