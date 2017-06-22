package com.zsbreak.InvertibleBloomFilter;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class InvertibleBloomFilter implements Serializable {
	private int numberOfAddedElements;
	private int k = 3; // number of hash functions
	private int ibfSize = 1000; //the size of IBF is d*1.5(d,size of the set difference) that are required to successfully decode the IBF
	private Cell[] cells;

	public Cell[] getCells() {
		return cells;
	}

	static final Charset charset = Charset.forName("UTF-8");

	static final String hashName = "MD5";
	static final MessageDigest digestFunction;
	static { // The digest method is reused between instances
		MessageDigest tmp;
		try {
			tmp = java.security.MessageDigest.getInstance(hashName);
		} catch (NoSuchAlgorithmException e) {
			tmp = null;
		}
		digestFunction = tmp;
	}

	public InvertibleBloomFilter(int ibfSize) {
		this.ibfSize = ibfSize;
		this.cells = new Cell[ibfSize]; // how to create a array of the inner class when the cell is a inner class ?
		for(int i=0;i<cells.length;i++)
			cells[i]=new Cell();
	}

	public void add(int id) {
		byte[] sid = String.valueOf(id).getBytes(charset);
		int[] hashes = genHashes(sid, k);
		for (int hash : hashes) {
			int idx = Math.abs(hash % ibfSize);
			cells[idx].add(id, genIdHash(sid));
		}
		numberOfAddedElements++;
	}

	public boolean contains(int id) {
		byte[] sid = String.valueOf(id).getBytes(charset);
		int[] hashes = genHashes(sid, k);
		for (int hash : hashes) {
			Cell cell = cells[Math.abs(hash % ibfSize)];
			if (cell == null || cell.getCount() == 0) {
				return false;
			}
		}
		return true;
	}

	public Cell[] subtract(Cell[] b2) {
		Cell[] b1 = this.cells;
		Cell[] res = new Cell[b2.length];
		for (int i = 0; i < res.length; i++) {
			if (res[i] == null)
				res[i] = new Cell();
			res[i].setIdSum(b1[i].getIdSum() ^ b2[i].getIdSum());
			res[i].setHashSum(b1[i].getHashSum() ^ b2[i].getHashSum());
			res[i].setCount(b1[i].getCount() - b2[i].getCount());
		}
		return res;
	}

	public List[] decode(Cell[] b3) {
		List<Integer> addtional = new ArrayList<Integer>();
		List<Integer> miss = new ArrayList<Integer>();
		Queue<Integer> pureIdxList = new LinkedList<Integer>();

		for (int i = 0; i < ibfSize; i++) {
			if (b3[i].isPure())
				pureIdxList.add(i);
		}

		while (!pureIdxList.isEmpty()) {
			int i = pureIdxList.poll();
			if (!b3[i].isPure())
				continue;
			int s = b3[i].getIdSum();
			int c = b3[i].getCount();
			if (c > 0)
				addtional.add(s);
			else
				miss.add(s);
			byte[] sb = String.valueOf(s).getBytes(charset);
			for (int hash : genHashes(sb, k)) {
				int j = Math.abs(hash % ibfSize);
				b3[j].setIdSum(b3[j].getIdSum() ^ s);
				b3[j].setHashSum(b3[j].getHashSum() ^ genIdHash(sb));
				b3[j].setCount(b3[j].getCount() - c);
				if (b3[j].isPure())
					pureIdxList.add(j);
			}
		}

		for (int i = 0; i < b3.length; i++) {
			if (b3[i].getIdSum() != 0 || b3[i].getHashSum() != 0 || b3[i].getCount() != 0)
				return null;
		}
		return new List[] { addtional, miss };
	}

	public static int genIdHash(byte[] data) {
		int result = 0;
		byte salt = 125;
		byte[] digest;
		synchronized (digestFunction) {
			digestFunction.update(salt);
			digest = digestFunction.digest(data);
		}

		int h = 0;
		for (int j = 0; j < 4; j++) {
			h <<= 8;
			h |= ((int) digest[j]) & 0xFF;
		}
		result = h;
		return result;
	}

	public static int genHash(byte[] data) {
		return genHashes(data, 1)[0];
	}

	public static int[] genHashes(byte[] data, int hashes) {
		int[] result = new int[hashes];

		int k = 0;
		byte salt = 0;
		while (k < hashes) {
			byte[] digest;
			synchronized (digestFunction) {
				digestFunction.update(salt);
				salt++;
				digest = digestFunction.digest(data);
			}

			for (int i = 0; i < digest.length / 4 && k < hashes; i++) {
				int h = 0;
				for (int j = (i * 4); j < (i * 4) + 4; j++) {
					h <<= 8;
					h |= ((int) digest[j]) & 0xFF;
				}
				result[k] = h;
				k++;
			}
		}
		return result;
	}

}
