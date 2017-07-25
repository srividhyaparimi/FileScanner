package com.example.srividhya.filescanner;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by srividhya on 7/24/2017.
 */

public class Scanner extends Service implements Runnable{

    public static final String TAG        = "com.example.srividhya.filescanner.scanner:";
    public static final String  EXTRA_TAG  = "update";
    private static final File EXTERNAL   = Environment.getExternalStorageDirectory();
    private static final int    NOT_ID = 99;

    private Handler mHandler;
    private Thread threadScan;
    private BroadcastReceiver mReceiver;

    private Map<String,Integer> extMap;
    private Set<Map.Entry<String,Integer>> entrySet;
    private List<Map.Entry<String,Integer>> sorted;


    private volatile boolean isScanning = false;
    private volatile boolean isDone     = false;
    private volatile boolean isPaused   = false;

    private volatile int  scannedFiles    = 0;
    private volatile long scannedBytesSoFar    = 0;


    private int scanFrequency = 24;
    private volatile int freqCheck   = 0;

    private Update currentStatus = new Update();


    public Scanner() {}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler = Handler.getInstance();

        extMap = new HashMap<>();

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (intent.getAction()){
                    case MainActivity.TAG+".START":

                        startScan();
                        break;
                    case MainActivity.TAG+".PAUSE":

                        pauseScan();
                        break;
                    case MainActivity.TAG+".UPDATE":

                        sendUpdate();
                        break;
                    case MainActivity.TAG+".STOP":

                        stopScan();
                        break;
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.TAG+".START");
        intentFilter.addAction(MainActivity.TAG+".PAUSE");
        intentFilter.addAction(MainActivity.TAG+".UPDATE");
        intentFilter.addAction(MainActivity.TAG+".STOP");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,intentFilter);
        return Service.START_NOT_STICKY;
    }



    private void startScan(){
        isPaused = false;
        if((!isScanning && !isDone) || (!isScanning && isDone)){
            mHandler.initialize(NOT_ID,"Scanned: "+currentStatus.totalMBSize + "MBs",this);
            reStart();
        }

    }

    private void pauseScan(){

        isPaused = true;
    }

    private void stopScan(){
        isScanning = false;
        isDone     = true;
        mHandler.updateMessage(NOT_ID, "Scanning Stopped!", this);
    }

    private void sendUpdate(){

        Intent intent = new Intent(TAG + ".UPDATE");
        intent.putExtra(EXTRA_TAG, currentStatus);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        mHandler.updateMessage(NOT_ID,"Scanned: "+currentStatus.totalMBSize + "MBs",this);

    }

    private void reStart(){
        isScanning = true;
        isDone = false;
        currentStatus = new Update();
        scannedBytesSoFar = 0;
        scannedFiles = 0;
        extMap = new HashMap<>();
        threadScan = new Thread(this);
        threadScan.start();
    }


    @Override
    public void run() {
        scan(EXTERNAL);


        isDone = true;
        isScanning = false;
        currentStatus.isScan = 1;
        sendUpdate();
        mHandler.updateMessage(NOT_ID, "Scanning Done!", this);
    }

    private void scan(File directory){
        if(isScanning){
            File[] listFile = directory.listFiles();

            if (listFile != null) {
                int i = 0;
                    while (i<listFile.length){

                    if (listFile[i].isDirectory()) {
                        scan(listFile[i]);
                    } else {
                        while (isPaused){
                            try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
                        }
                        freqCheck++;
                        scannedFiles++;
                        if(freqCheck==scanFrequency){
                            sendUpdate();
                            freqCheck=0;
                        }
                        process(listFile[i].getName(), getFileExtension(listFile[i].getName()), listFile[i].length());

                    }

                    i++;
                }


            }
        }
    }


    private void process(String name,String ext,long size){

        filterFileData(name,ext,size);
        filterExtData(name,ext,size);


        double avgD = ((double)scannedBytesSoFar / (double)scannedFiles)/1000000;

        currentStatus.avgSizeofFile = avgD;

        currentStatus.totalMBSize = (int)(((double)scannedBytesSoFar/(double)1000000));



        scannedBytesSoFar+=size;

    }

    private void filterFileData(String name,String ext,long size){
        int sizeIndex = fitLong(currentStatus.largeToptenFilesizes, size);
        retract(currentStatus.largeToptenFilenames,name,sizeIndex);
    }

    private void filterExtData(String name,String ext,long size){
        if(!extMap.containsKey(ext)){
            extMap.put(ext, 1);
        }
        else{
            extMap.put(ext, extMap.get(ext) + 1);
        }

        entrySet = extMap.entrySet();
        sorted = new ArrayList<>(entrySet);

        Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> a,
                               Map.Entry<String, Integer> b) {
                return b.getValue() - a.getValue();
            }
        });

        int len = sorted.size()>5 ? 5:sorted.size();
        for(int i = 0; i <len;i++){
            currentStatus.mostRecentFiveExtensions[i]=sorted.get(i).getKey();
        }
    }

    private int fitLong(long[] ary,long val){
        boolean isBigger = false;
        int retract = ary.length-1;
        if(ary[retract]<val){
            int i=ary.length-1;
            while (i>=0){
                if(ary[i]<val){
                    retract=i;
                    isBigger = true;
                }
                i--;
            }
        }
        if(isBigger) {
            long next = ary[retract];
            ary[retract] = val;
            int i = retract + 1;
            while(i < ary.length)  {
                ary[i] = next;
                if (i + 1 < ary.length) {
                    next = ary[i + 1];
                }
                i++;
            }
        }

        return isBigger ? retract : -1;
    }

    private void retract(String[] strings,String string,int index){
        if(index == -1){return;}
        String next = strings[index];
        strings[index] = string;
        for(int i = index+1;i<strings.length;i++){
            strings[i] = next;
            if(i+1<strings.length){next=strings[i+1];}

        }
    }

    private String getFileExtension(String file){
        int dot = 0;
        for(int i = 0;i<file.length()-1;i++){
            if(file.charAt(file.length()-(1+i)) == '.'){
                dot = i;
                break;
            }
        }
        return file.substring(file.length()-(dot+1),file.length());
    }


    @Override
    public void onDestroy() {
        isPaused   = false;
        isScanning = false;
        super.onDestroy();
    }
}