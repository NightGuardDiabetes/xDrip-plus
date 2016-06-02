package com.eveningoutpost.dexdrip.UtilityModels.pebble;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.eveningoutpost.dexdrip.Models.UserError.Log;
import com.eveningoutpost.dexdrip.UtilityModels.BgGraphBuilder;
import com.eveningoutpost.dexdrip.utils.Preferences;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by THE NIGHTSCOUT PROJECT CONTRIBUTORS (and adapted to fit the needs of this project)
 */
public class PebbleWatchSync extends Service {

    public static final UUID PEBBLEAPP_UUID = UUID.fromString("79f8ecb3-7214-4bfc-b996-cb95148ee6d3");

    private final static String TAG = PebbleWatchSync.class.getSimpleName();

    public static int lastTransactionId;

    public static PebbleDisplayType pebbleDisplayType;
    private static Context context;
    private static BgGraphBuilder bgGraphBuilder;
    private static Map<PebbleDisplayType, PebbleDisplayInterface> pebbleDisplays;


    public static void setPebbleType(int pebbleType) {

        switch (pebbleType) {
            case 2:
                pebbleDisplayType = PebbleDisplayType.Standard;
                break;

            case 3:
                pebbleDisplayType = PebbleDisplayType.Trend;
                break;

            default:
                pebbleDisplayType = PebbleDisplayType.None;
                break;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        bgGraphBuilder = new BgGraphBuilder(context);
        //mBgReading = BgReading.last();

        initPebbleDisplays();

        pebbleDisplayType = getCurrentBroadcastToPebbleSetting();

        init();
    }

    private void initPebbleDisplays() {

        if (pebbleDisplays == null) {
            pebbleDisplays = new HashMap<>();
            pebbleDisplays.put(PebbleDisplayType.None, new PebbleDisplayDummy());
            pebbleDisplays.put(PebbleDisplayType.None, new PebbleDisplayStandard());
            pebbleDisplays.put(PebbleDisplayType.None, new PebbleDisplayTrend());
        }

        for (PebbleDisplayInterface pdi : pebbleDisplays.values()) {
            pdi.initDisplay(context, this, bgGraphBuilder);
        }

    }


    private PebbleDisplayInterface getActivePebbleDisplay() {
        return pebbleDisplays.get(pebbleDisplayType);
    }


    public static PebbleDisplayType getCurrentBroadcastToPebbleSetting() {
        // value is int or boolean (old)

        PebbleDisplayType pebbleDisplayType = PebbleDisplayType.None;

        try {
            String value = PreferenceManager.getDefaultSharedPreferences(context).getString("broadcast_to_pebble", "1");

            // determine
            if (value.equals("2")) {
                pebbleDisplayType = PebbleDisplayType.Standard;
            } else if (value.equals("3")) {
                pebbleDisplayType = PebbleDisplayType.Trend;
            }

        } catch (ClassCastException ex) {
            Boolean value = Preferences.getBooleanPreferenceViaContextWithoutException(context, "broadcast_to_pebble", false);

            if (value)
                pebbleDisplayType = PebbleDisplayType.Standard;

        }

        return pebbleDisplayType;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (getCurrentBroadcastToPebbleSetting() == PebbleDisplayType.None) {
            stopSelf();
            return START_NOT_STICKY;
        }

        Log.i(TAG, "STARTING SERVICE PebbleWatchSync");

        getActivePebbleDisplay().startDeviceCommand();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy called");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected void init() {
        Log.i(TAG, "Initialising...");
        Log.i(TAG, "configuring PebbleDataReceiver");

        PebbleKit.registerReceivedDataHandler(context, new PebbleKit.PebbleDataReceiver(PEBBLEAPP_UUID) {
            @Override
            public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
                getActivePebbleDisplay().receiveData(transactionId, data);
            }
        });

        PebbleKit.registerReceivedAckHandler(context, new PebbleKit.PebbleAckReceiver(PebbleWatchSync.PEBBLEAPP_UUID) {
            @Override
            public void receiveAck(Context context, int transactionId) {
                getActivePebbleDisplay().receiveAck(transactionId);
            }
        });

        PebbleKit.registerReceivedNackHandler(context, new PebbleKit.PebbleNackReceiver(PebbleWatchSync.PEBBLEAPP_UUID) {
            @Override
            public void receiveNack(Context context, int transactionId) {
                getActivePebbleDisplay().receiveNack(transactionId);
            }
        });

    }


}

