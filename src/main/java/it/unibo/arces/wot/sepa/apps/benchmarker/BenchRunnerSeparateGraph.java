package it.unibo.arces.wot.sepa.apps.benchmarker;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;




@State(Scope.Thread)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS) 
@Timeout(time = 1, timeUnit = TimeUnit.HOURS)
public class BenchRunnerSeparateGraph {
	
	private BenchmarkerSeparateGraph b1;
	
	@Setup
	public void init1() {
		b1= new BenchmarkerSeparateGraph(0,15,40);
		System.out.println("Benchmarker separateGraph init-success: "+ b1.init());
		
	}


	@Benchmark
	public void test1() {
		//System.out.println("Benchmarker run-success: "+ b.runTest());
		b1.runTest();
	}
	
    public static void main(String[] args) throws RunnerException {

    	
    	      Options opt = new OptionsBuilder()
  	                .include(BenchRunnerSeparateGraph.class.getSimpleName())
  	                .forks(1)
  	                .build();

  	        new Runner(opt).run();
  
    }
}
