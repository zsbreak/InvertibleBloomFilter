# InvertibleBloomFilter
Invertible Bloom Filter-based strata estimator as described by Eppstein et al. in ["What's the Difference? Efficient Set Reconciliation without Prior Context."](https://www.ics.uci.edu/~eppstein/pubs/EppGooUye-SIGCOMM-11.pdf)

No extenal dependencies.

##Configuration
----------------

### estimate the size of set difference
###
		StrataEstimator se1 = new StrataEstimator();
		StrataEstimator se2 = new StrataEstimator();
		//the set 1 named s1,and the set 2 named s2
		se1.encode(s1);
		se2.encode(s2);
		int diff = se1.decode(se2);

### decode the set difference
###
		//we determined 1.5*diff of the IBF's size
		//since approximately 1.5*diff cells are required to successfully decode the IBF.
		InvertibleBloomFilter b1 = new InvertibleBloomFilter((int) (diff * 2));//or 1.5*diff
		InvertibleBloomFilter b2 = new InvertibleBloomFilter((int) (diff * 2));
		for (int i = 0; i < TEST_SIZE; i++) {
			b1.add(s1[i]);
			b2.add(s2[i]);
		}
		// subtract invertible bloom filter
		Cell[] res = b1.subtract(b2.getCells());

		// decode the result of the subtract operation
		long start_decode = System.currentTimeMillis();
		List<Integer>[] decodeResult = b1.decode(res);


### he result of the benchmark test:
###
		=========benchmark start==========
		StrataEstimator.encode(): 1.453s, 688231.2456985547 elements/s
		StrataEstimator.decode(): 0.002s, 5.0E8 elements/s
		the size of the set diffence:20,the result of estimating:20
		==========
		ibf.add(): 0.987s, 1013171.2259371834 elements/s
		ibf.contains(), existing: 0.335s, 2985074.6268656715 elements/s
		ibf.subtract()0.0s, Infinity elements/s
		ibf.decode()0.001s, 1.0E9 elements/s
		=========benchmark end==========
		23583:23583,1337:1337
		23586:23586,8278:8278
		34145:34145,14619:14619
		37002:37002,15403:15403
		64612:64612,19001:19001
		64842:64842,22253:22253
		66591:66591,29445:29445
		73243:73243,56447:56447
		77606:77606,75472:75472
		80984:80984,94830:94830
		decode success
