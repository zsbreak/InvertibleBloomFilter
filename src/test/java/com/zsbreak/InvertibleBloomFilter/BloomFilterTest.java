package com.zsbreak.InvertibleBloomFilter;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;

import junit.framework.TestCase;


public class BloomFilterTest  extends TestCase{
	static Random r = new Random();
	InvertibleBloomFilter ibf = new InvertibleBloomFilter(2000);

	@Test
	public void testCreateHash_String() throws Exception {
		System.out.println("test create hc hash");

		String val = UUID.randomUUID().toString();
		int result1 = ibf.genIdHash(val.getBytes("UTF-8"));
		int result2 = ibf.genIdHash(val.getBytes("UTF-8"));
		assertEquals(result2, result1);
		System.out.println(result1);
	}

	@Test
	public void testAdd() throws Exception {
		System.out.println("add");
		for (int i = 0; i < 100; i++) {
			int val = r.nextInt(9999);
			ibf.add(val);
			assert (ibf.contains(val));
		}
	}

	@Test
	public void testPure() throws Exception {
		int val = r.nextInt(9999);
		ibf.add(val);
		int count = 0; 
		for (Cell cell : ibf.getCells()) {
			if(cell!=null&&cell.isPure())
				count++;
		}
		System.out.println(count);
	}

	@Test
	public void testDecode() throws Exception {

		InvertibleBloomFilter b1 = new InvertibleBloomFilter(100);
		InvertibleBloomFilter b2 = new InvertibleBloomFilter(100);

		for (int i = 0; i < 50000; i++) {
			int val = r.nextInt(99999999);
			b1.add(val);
			b2.add(val);
		}

		int b1sb2[] = new int[10];
		int b2sb1[] = new int[20];

		for (int i = 0; i < b1sb2.length; i++) {
			int val = r.nextInt(99999999);
			b1.add(val);
			b1sb2[i]=val;
		}
		for (int i = 0; i < b2sb1.length; i++) {
			int val = r.nextInt(99999999);
			b2.add(val);
			b2sb1[i]=val;
		}

		Arrays.sort(b1sb2);
		Arrays.sort(b2sb1);

		Cell[] res = b1.subtract(b2.getCells());
		List[] diff = b1.decode(res);
		assert (diff != null);
		if (diff[0].size() != b1sb2.length)
			System.out.println("error b1sb2");
		if (diff[1].size() != b2sb1.length)
			System.out.println("error b2sb1");
		
		Collections.sort(diff[0]);
		Collections.sort(diff[1]);

		System.out.println("===========");
		
		for (int i = 0; i < diff[0].size(); i++) {
			System.out.println(b1sb2[i]+","+diff[0].get(i));
		}
		System.out.println("..........");
		for (int i = 0; i < diff[1].size(); i++) {
			System.out.println(b2sb1[i]+","+diff[1].get(i));
		}
	}

}
