package com.example.demo.config;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicLong;

import com.example.demo.DemoApplication;

public class ForkJoinWorkerFactory implements ForkJoinWorkerThreadFactory {

	private final AtomicLong workerIndex = new AtomicLong();

	private final String poolname;

	public ForkJoinWorkerFactory(String workerPrefix) {
		this.poolname = workerPrefix;
	}

	@Override
	public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
		final DemoForkJoinWorkerThread worker = new DemoForkJoinWorkerThread(pool);
		worker.setName(poolname + worker.getPoolIndex() + "-" + workerIndex.incrementAndGet());
		return worker;
	}

	private static class DemoForkJoinWorkerThread extends ForkJoinWorkerThread {

		private DemoForkJoinWorkerThread(final ForkJoinPool pool) {
			super(pool);
			setContextClassLoader(DemoApplication.class.getClassLoader());
		}
	}
}
