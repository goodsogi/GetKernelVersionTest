package com.commax.androidbuildinfotest;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String FILENAME_PROC_VERSION = "/proc/version";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Build 정보
//        Log.i(LOG_TAG, "Model: " + Build.MODEL);
          Log.i(LOG_TAG, "Boot Loader: " + Build.BOOTLOADER);
//        Log.i(LOG_TAG, "Board: " + Build.BOARD);
//        Log.i(LOG_TAG, "Brand: " + Build.BRAND);
//        Log.i(LOG_TAG, "Device: " + Build.DEVICE);
//        Log.i(LOG_TAG, "Display: " + Build.DISPLAY);
//        Log.i(LOG_TAG, "Finger Print: " + Build.FINGERPRINT);
//        Log.i(LOG_TAG, "Radio Version: " + Build.getRadioVersion());
//        Log.i(LOG_TAG, "Hardware: " + Build.HARDWARE);
//        Log.i(LOG_TAG, "Host: " + Build.HOST);
//        Log.i(LOG_TAG, "Id: " + Build.ID);
//        Log.i(LOG_TAG, "Manufacture: " + Build.MANUFACTURER);
//        Log.i(LOG_TAG, "Model: " + Build.MODEL);
//        Log.i(LOG_TAG, "Product: " + Build.PRODUCT);
//        Log.i(LOG_TAG, "Serial: " + Build.SERIAL);
//        Log.i(LOG_TAG, "Tags: " + Build.TAGS);
//        Log.i(LOG_TAG, "Type: " + Build.TYPE);
//        Log.i(LOG_TAG, "Unknown: " + Build.UNKNOWN);
//        Log.i(LOG_TAG, "User: " + Build.USER);


        Log.i(LOG_TAG, "Kernel version: " + getFormattedKernelVersion());

    }


    public static String getFormattedKernelVersion() {
        try {
            return formatKernelVersion(readLine(FILENAME_PROC_VERSION));

        } catch (IOException e) {
            Log.e(LOG_TAG,
                    "IO Exception when getting kernel version for Device Info screen",
                    e);

            return "Unavailable";
        }
    }

    private static String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    public static String formatKernelVersion(String rawKernelVersion) {
        // Example (see tests for more):
        // Linux version 3.0.31-g6fb96c9 (android-build@xxx.xxx.xxx.xxx.com) \
        //     (gcc version 4.6.x-xxx 20120106 (prerelease) (GCC) ) #1 SMP PREEMPT \
        //     Thu Jun 28 11:02:39 PDT 2012

        final String PROC_VERSION_REGEX =
                "Linux version (\\S+) " + /* group 1: "3.0.31-g6fb96c9" */
                        "\\((\\S+?)\\) " +        /* group 2: "x@y.com" (kernel builder) */
                        "(?:\\(gcc.+? \\)) " +    /* ignore: GCC version information */
                        "(#\\d+) " +              /* group 3: "#1" */
                        "(?:.*?)?" +              /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
                        "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /* group 4: "Thu Jun 28 11:02:39 PDT 2012" */

        Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(rawKernelVersion);
        if (!m.matches()) {
            Log.e(LOG_TAG, "Regex did not match on /proc/version: " + rawKernelVersion);
            return "Unavailable";
        } else if (m.groupCount() < 4) {
            Log.e(LOG_TAG, "Regex match on /proc/version only returned " + m.groupCount()
                    + " groups");
            return "Unavailable";
        }
        return m.group(1) + "\n" +                 // 3.0.31-g6fb96c9
                m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
                m.group(4);                            // Thu Jun 28 11:02:39 PDT 2012
    }
}
