package com.example.srividhya.filescanner;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by srividhya on 7/24/2017.
 */

public class MainActivity extends AppCompatActivity{
    public static final String TAG = "com.example.srividhya.filescanner.mainactivity:";
    private TextView perView;
    private TextView avgView;
    private DataFileFragment filedataFragment;
    private ExtDataFragment dataextFragment;
    private BroadcastReceiver updateReceiver;
    private Update mostrecentfile;
    private Button startscanbutton;
    private Button sharefilebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isScannerActive()) {
            startService(new Intent(this, Scanner.class));
        }

        //Start button

        startscanbutton = (Button) findViewById(R.id.startbutton);
        startscanbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startScan();

            }
        });

        // Pause Button
        Button pauseButton = (Button) findViewById(R.id.pausebutton);
        pauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                pauseScan();

            }
        });
        // Share Button
        sharefilebutton = (Button) findViewById(R.id.sharebutton);
        pauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                shareResults();

            }
        });
        sharefilebutton.setVisibility(View.INVISIBLE);
        perView = (TextView) findViewById(R.id.perc_view);
        avgView = (TextView) findViewById(R.id.avg_view);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Scanner.TAG + ".UPDATE");
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mostrecentfile = intent.getParcelableExtra(Scanner.EXTRA_TAG);
                refreshUI();
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver, intentFilter);
        filedataFragment = (DataFileFragment) getSupportFragmentManager().findFragmentById(R.id.datafilefragment);
        dataextFragment = (ExtDataFragment) getSupportFragmentManager().findFragmentById(R.id.extDataList);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Scanner.EXTRA_TAG, mostrecentfile);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mostrecentfile = savedInstanceState.getParcelable(Scanner.EXTRA_TAG);
        if (mostrecentfile != null) {
            refreshUI();
        }
    }
    public void startScan() {
        Intent intent = new Intent(TAG + ".START");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void pauseScan() {
        Intent intent = new Intent(TAG + ".PAUSE");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void requestUpdate() {
        Intent intent = new Intent(TAG + ".UPDATE");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void stopScan() {
        Intent intent = new Intent(TAG + ".STOP");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void shareResults() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.addingreceipent)});
        i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.storefiles));
        i.putExtra(Intent.EXTRA_TEXT, mostrecentfile.totalMBSize + "MBs of data has been scanned.");

        startActivity(Intent.createChooser(i, "Send mail..."));

    }

    private boolean isScannerActive() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (Scanner.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void refreshUI() {
        //test
        filedataFragment.updateData(mostrecentfile);
        filedataFragment.updateData(mostrecentfile);

        if (mostrecentfile.isScan == 1) {
            sharefilebutton.setVisibility(View.VISIBLE);
        } else {
            sharefilebutton.setVisibility(View.INVISIBLE);
        }
        perView.setText("Total:" + mostrecentfile.totalMBSize + "MBs");
        avgView.setText("Avg:" + (float) mostrecentfile.avgSizeofFile + "MBs");

    }
    @Override
    public void onBackPressed() {
        stopScan();
        finish();
    }
// on
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}