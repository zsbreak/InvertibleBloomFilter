package com.zsbreak.InvertibleBloomFilter;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class StrataEstimator {

	private int L = 32;// Is U the hash range? the ith partition covers
						// 1/2^(i+1) of U
	private InvertibleBloomFilter[] ibfs = new InvertibleBloomFilter[L];

	public StrataEstimator() {
		for (int i = 0; i < ibfs.length; i++)
			ibfs[i] = new InvertibleBloomFilter(100);// ?? how to determine the
														// approximate size of
														// the ibfs[i]?
	}

	public InvertibleBloomFilter[] encode(int[] s) {
		for (int element : s) {
			int i = trailingZeros(element);
			ibfs[i].add(element);
		}
		return ibfs;
	}

	public int decode(StrataEstimator se2) {
		InvertibleBloomFilter[] ibfs2 = se2.ibfs;
		int count = 0;
		for (int i = ibfs.length - 1; i >= -1; i--) {
			if (i < 0)
				return count * (int) Math.pow(2, i + 1);
			Cell[] subResult = ibfs[i].subtract(ibfs2[i].getCells());
			List[] decResult = ibfs[i].decode(subResult);
			if (decResult == null)
				return count * (int) Math.pow(2, i + 1);
			count += decResult[0].size() + decResult[1].size();
		}
		return count;
	}

	public int trailingZeros(int num) {
		int res = 0;
		try {
			res = Integer.numberOfTrailingZeros(InvertibleBloomFilter.genHash(String.valueOf(num).getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return res;
	}

	public InvertibleBloomFilter[] getIbfs() {
		return ibfs;
	}

}
