package me.nickvo;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class ParallelSum {
    static long[] arrayA;
    static int nThreads, N;
    static long totalSum = 0;

    public static void main(String[] args) throws InterruptedException {
        /*Get user input from command line*/
        if (args.length > 0) {
            nThreads = Integer.parseInt(args[0]);
            N = Integer.parseInt(args[1]);
        } else {
            System.out.println("Usage: java className <number of Threads> < N >");
            System.exit(1);
        }

        for (int i = 0; i < nThreads; i++) {
            sum();
        }
    }

    private static void sum() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        /*Generate array*/
        arrayA = new long[N];
        for (int i = 0; i < N; i++) {
            arrayA[i] = i + 1;
        }
        /* Main thread prints initial array if array length < 20*/
        if (N < 20) {
            System.out.println(Thread.currentThread().getName() + ": " + Arrays.toString(arrayA));
        }
        /* Create array to hold team of threads */
        Thread[] workers = new Thread[nThreads];
        /* Amount of work each thread will do */
        long work = arrayA.length / nThreads;
        long remainder = N % nThreads;

        for (int i = 0; i < nThreads; i++) {
            /* Starting and ending index each thread will work on */
            long low = work * i; // Begin
            long high = low + work; // Begin + work

            if (nThreads - 1 == i) {
                high += remainder;
            }

            Runnable obj = new Worker(low, high);
            workers[i] = new Thread(obj);
            workers[i].start();
        }

        for (int i = 0; i < nThreads; i++) {
            workers[i].join();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println(Thread.currentThread().getName() + ": Sequential Sum " + seqSum()
                + ", Parallel Sum " + totalSum + " Num threads: " + nThreads + " Array size: "
                + arrayA.length + " elapsed: " + elapsedTime + "ms");
    }

    /**
     * Worker class that implements Runnable
     */
    public static class Worker implements Runnable {
        private final long low;
        private final long high;

        public Worker(long low, long high) {
            this.low = low;
            this.high = high;
        }

        static ReentrantLock mutex = new ReentrantLock();

        /* Thread work */
        @Override
        public void run() {
            mutex.lock();
            long localSum = 0;
            try {
                for (int i = (int) low; i < high; i++) {
                    localSum += arrayA[i];
                }
                totalSum += localSum;
            } finally {
                mutex.unlock();
            }
        }
    }

    /*Sequential Sum */
    public static int seqSum() {
        int sum = 0;
        for (int i = 0; i < N; i++) {
            sum += arrayA[i];
        }
        return sum;
    }

}
