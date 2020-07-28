package it.unibo.arces.wot.sepa.apps.benchmarker;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

//@State(Scope.Thread)
public class BenchmarkerRunner {

	private Benchmarker b;
	@Setup
	public void init() {

		b= new Benchmarker(1,4,5);
		System.out.println("Benchmarker init-success: "+ b.init());
	}

	@Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
	public void test() {
		System.out.println("Benchmarker run-success: "+ b.runTest());
	}
	

}
