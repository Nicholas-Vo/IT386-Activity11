package me.nickvo;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class ParallelSum {
    static int[] arrayA;
    static int nThreads, N;
    static int totalSum = 0;

    public static void main(String[] args) throws InterruptedException {
        /*Get user input from command line*/
        if (args.length > 0) {
            nThreads = Integer.parseInt(args[0]);
            N = Integer.parseInt(args[1]);
        } else {
            System.out.println("Usage: java className <number of Threads> < N >");
            System.exit(1);
        }

//        ReentrantLock mutex = new ReentrantLock();
//        try {
//            mutex.lock();
//            for (int i = 0; i < 30; i++) {
//                sum();
//            }
//        } finally {
//            mutex.unlock();
//        }
        for (int i = 0; i < 30; i++) {
            sum();
        }
    }

    private static void sum() throws InterruptedException {
        /*Generate array*/
        arrayA = new int[N];
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
        int work = arrayA.length / nThreads;
        int remainder = N % nThreads;

        for (int i = 0; i < nThreads; i++) {
            /* Starting and ending index each thread will work on */
            int low = work * i; // Begin
            int high = low + work; // Begin + work

//            if (remainder > 0 && i == nThreads) {
//                high = i + 1 % work + remainder;
//            }

            Runnable obj = new Worker(low, high);
            workers[i] = new Thread(obj);
            workers[i].start();
        }

        for (int i = 0; i < nThreads; i++) {
            workers[i].join();
        }

        //long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println(Thread.currentThread().getName() + ": Sequential Sum " + seqSum()
                + ", Parallel Sum " + totalSum + " Num threads: " + nThreads + " Array size: " + arrayA.length);
    }

    /**
     * Worker class that implements Runnable
     */
    public static class Worker implements Runnable {
        private final int low;
        private final int high;
        private int localSum;

        public Worker(int low, int high) {
            this.low = low;
            this.high = high;
        }

        static ReentrantLock mutex = new ReentrantLock();

        /* Thread work */
        @Override
        public void run() {
            mutex.lock();

            try {
                for (int i = low; i < high; i++) {
                    totalSum += arrayA[i];
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
