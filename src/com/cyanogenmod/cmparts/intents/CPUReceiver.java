/*
 * Copyright (C) 2011 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyanogenmod.cmparts.intents;

import com.cyanogenmod.cmparts.activities.CPUActivity;
import com.cyanogenmod.cmparts.activities.PerformanceSettingsActivity;

import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.SharedPreferences;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class CPUReceiver extends BroadcastReceiver {

    private static final String TAG = "CPUSettings";

    private static final String CPU_SETTINGS_PROP = "sys.cpufreq.restored";
    private static final String KSM_SETTINGS_PROP = "sys.ksm.restored";

    @Override
    public void onReceive(Context ctx, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED) &&
                intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED) &&
                   !intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
                setChargeCPU(ctx);
        } else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED) &&
                intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED) &&
                   intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
                setBatlowCPU(ctx);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) &&
                   intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED) &&
                   !intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
                setScreenOffCPU(ctx, true);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON) &&
                   intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED) &&
                   !intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
                setScreenOffCPU(ctx, false);
        } else if (SystemProperties.getBoolean(CPU_SETTINGS_PROP, false) == false
                && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SystemProperties.set(CPU_SETTINGS_PROP, "true");
            configureCPU(ctx);
        } else {
            SystemProperties.set(CPU_SETTINGS_PROP, "false");
        }

    }

    private void setScreenOffCPU(Context ctx, boolean screenOff) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String maxFrequency = prefs.getString(CPUActivity.MAX_FREQ_PREF, null);
        String maxSoFrequency = prefs.getString(CPUActivity.SO_MAX_FREQ_PREF, null);
        if (maxSoFrequency == null || maxFrequency == null) {
            Log.i(TAG, "Screen off or normal max CPU freq not saved. No change.");
        } else {
            if (screenOff) {
                CPUActivity.writeOneLine(CPUActivity.FREQ_MAX_FILE, maxSoFrequency);
                Log.i(TAG, "Screen off max CPU freq set");
            } else {
                CPUActivity.writeOneLine(CPUActivity.FREQ_MAX_FILE, maxFrequency);
                Log.i(TAG, "Normal (screen on) max CPU freq restored");
            }
        }
    }

    private boolean setCarDockCPU(Context ctx, boolean carDock) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String maxFrequency = prefs.getString(CPUActivity.MAX_FREQ_PREF, null);
        String maxCdFrequency = prefs.getString(CPUActivity.CD_MAX_FREQ_PREF, null);
        if (maxCdFrequency == null || maxFrequency == null) {
            Log.i(TAG, "CarDock or normal max CPU frequency not saved. No change.");
            return false;
        } else {
            if (carDock) {
                CPUActivity.writeOneLine(CPUActivity.FREQ_MAX_FILE, maxCdFrequency);
                Log.i(TAG, "CarDock max CPU freq set");
            } else {
                CPUActivity.writeOneLine(CPUActivity.FREQ_MAX_FILE, maxFrequency);
                Log.i(TAG, "Normal max CPU freq restored");
            }
        }
        return true;
    }

    private void setChargeCPU(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String maxChFrequency = prefs.getString(CPUActivity.CH_MAX_FREQ_PREF, null);
            if (maxChFrequency != null) {
                CPUActivity.writeOneLine(CPUActivity.FREQ_MAX_FILE, maxChFrequency);
                Log.i(TAG, "Charging on  max CPU freq set");
            }
    }

    private void setBatlowCPU(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String maxBatFrequency = prefs.getString(CPUActivity.BAT_MAX_FREQ_PREF, null);
            if (maxBatFrequency != null) {
                CPUActivity.writeOneLine(CPUActivity.FREQ_MAX_FILE, maxBatFrequency);
                Log.i(TAG, "Low battery on  max CPU freq set");
            }
    }

    private void configureCPU(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        if (prefs.getBoolean(CPUActivity.SOB_PREF, false) == false) {
            Log.i(TAG, "Restore disabled by user preference.");
            return;
        }

        String governor = prefs.getString(CPUActivity.GOV_PREF, null);
        String minFrequency = prefs.getString(CPUActivity.MIN_FREQ_PREF, null);
        String maxFrequency = prefs.getString(CPUActivity.MAX_FREQ_PREF, null);
        String availableFrequenciesLine = CPUActivity.readOneLine(CPUActivity.FREQ_LIST_FILE);
        String availableGovernorsLine = CPUActivity.readOneLine(CPUActivity.GOVERNORS_LIST_FILE);
        boolean noSettings = ((availableGovernorsLine == null) || (governor == null)) && 
                             ((availableFrequenciesLine == null) || ((minFrequency == null) && (maxFrequency == null)));
        List<String> frequencies = null;
        List<String> governors = null;
        
        if (noSettings) {
            Log.d(TAG, "No settings saved. Nothing to restore.");
        } else {
            if (availableGovernorsLine != null){
                governors = Arrays.asList(availableGovernorsLine.split(" "));  
            }
            if (availableFrequenciesLine != null){
                frequencies = Arrays.asList(availableFrequenciesLine.split(" "));  
            }
            if (governor != null && governors != null && governors.contains(governor)) {
                CPUActivity.writeOneLine(CPUActivity.GOVERNOR, governor);
            }
            if (maxFrequency != null && frequencies != null && frequencies.contains(maxFrequency)) {
                CPUActivity.writeOneLine(CPUActivity.FREQ_MAX_FILE, maxFrequency);
            }
            if (minFrequency != null && frequencies != null && frequencies.contains(minFrequency)) {
                CPUActivity.writeOneLine(CPUActivity.FREQ_MIN_FILE, minFrequency);
            }
            Log.d(TAG, "CPU settings restored.");
        }
    }

}
