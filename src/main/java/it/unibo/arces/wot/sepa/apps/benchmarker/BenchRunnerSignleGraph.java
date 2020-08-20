package it.unibo.arces.wot.sepa.apps.benchmarker;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Thread)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS) 
@Timeout(time = 1, timeUnit = TimeUnit.HOURS)
public class BenchRunnerSignleGraph {

	private BenchmarkerSingleGraph b2;

	@Setup
	public void init2() {
		b2= new BenchmarkerSingleGraph(20,5);
		System.out.println("Benchmarker singleGraph init-success: "+ b2.init());
	}
	
	@Benchmark
	public void test2() {
		b2.runTest();
	}
    public static void main(String[] args) throws RunnerException {

    	      Options opt = new OptionsBuilder()
    	                .include(BenchRunnerSignleGraph.class.getSimpleName())
    	                .forks(1)
    	                .build();

    	        new Runner(opt).run();
  
  
    }
}
