package com.sms.deliamao.stockmarketsearch;

import android.os.Handler;
import android.support.v4.util.ArrayMap;

/**
 * Created by wenjieli on 5/4/16.
 */
public class TaskScheduler extends Handler {
    private ArrayMap<Runnable,Runnable> tasks = new ArrayMap<>();
    public void scheduleAtFixedRate(final Runnable task,long delay,final long period) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                task.run();
                postDelayed(this, period);
            }
        };
        tasks.put(task, runnable);
        postDelayed(runnable, delay);
    }

    public void scheduleAtFixedRate(final Runnable task,final long period) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                task.run();
                postDelayed(this, period);
            }
        };
        tasks.put(task, runnable);
        runnable.run();
    }
    public void stop(Runnable task) {
        Runnable removed = tasks.remove(task);
        if (removed!=null) removeCallbacks(removed);
    }

}
