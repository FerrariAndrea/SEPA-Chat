package it.unibo.arces.wot.sepa.apps.benchmarker;



public interface IBenchmarker {

	
	boolean init();
	
	boolean runTest();
	
	BenchResult getResult();
	
}
