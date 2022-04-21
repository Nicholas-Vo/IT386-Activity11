package me.nickvo;

import java.util.concurrent.locks.ReentrantLock;

class Locker {
    static ReentrantLock mutex = new ReentrantLock();

    public void method() {
        mutex.lock();

        try {
            System.out.println("");
        } finally {
            mutex.unlock();
        }
    }
}
