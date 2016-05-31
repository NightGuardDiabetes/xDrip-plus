
package com.eveningoutpost.dexdrip.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.eveningoutpost.dexdrip.Models.UserError;
import com.eveningoutpost.dexdrip.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by jamorham on 22/04/2016.
 */
public class InstallPebbleTrendWatchFace extends InstallPebbleWatchFace {

    private static String TAG = "InstallPebbleTrendWatchFace";


    @Override
    protected String getTag()
    {
        return TAG;
    }


    protected InputStream openRawResource()
    {
        return getResources().openRawResource(R.raw.xdrip_pebble_trend);
    }

    protected String getOutputFilename()
    {
        return "xDrip-plus-pebble-trend-auto-install.pbw";
    }





}
