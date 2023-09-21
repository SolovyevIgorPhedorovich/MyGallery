package com.example.mygallery;

import android.content.Context;

import com.example.mygallery.managers.DataManager;
import com.example.mygallery.managers.DatabaseManager;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class InvalidsPathRemoved extends Thread {
    private List<String> invalidsPath;
    private final DatabaseManager databaseManager;
    private final CountDownLatch adapterLatch;

    public InvalidsPathRemoved(Context context, CountDownLatch adapterLatch) {
        //invalidsPath = DataManager.getInstance(context).getInvalidsPathFile();
        databaseManager = DatabaseManager.getInstance(context);
        this.adapterLatch = adapterLatch;
    }

    @Override
    public void run() {
        try{
            adapterLatch.await();
            databaseManager.removedOldFile(invalidsPath);
            //DataManager.getInstance().clearData(DataManager.INVALIDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
