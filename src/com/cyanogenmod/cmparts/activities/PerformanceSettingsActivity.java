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

package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;
import com.cyanogenmod.cmparts.activities.CPUActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Performance Settings
 */
public class PerformanceSettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private static final String COMPCACHE_PREF = "pref_compcache_size";

    private static final String COMPCACHE_PERSIST_PROP = "persist.service.compcache";

    private static final String COMPCACHE_DEFAULT = SystemProperties.get("ro.compcache.default");

    private static final String GENERAL_CATEGORY = "general_category";

    private static final String JIT_PREF = "pref_jit_mode";

    private static final String JIT_ENABLED = "int:jit";

    private static final String JIT_DISABLED = "int:fast";

    private static final String JIT_PERSIST_PROP = "persist.sys.jit-mode";

    private static final String JIT_PROP = "dalvik.vm.execution-mode";

    private static final String HEAPSIZE_PREF = "pref_heapsize";

    private static final String HEAPSIZE_PROP = "dalvik.vm.heapsize";

    private static final String HEAPSIZE_PERSIST_PROP = "persist.sys.vm.heapsize";

    private static final String HEAPSIZE_DEFAULT = "16m";

    private static final String USE_DITHERING_PREF = "pref_use_dithering";

    private static final String USE_DITHERING_PERSIST_PROP = "persist.sys.use_dithering";
    
    private static final String USE_DITHERING_DEFAULT = "1";

    private static final String USE_16BPP_ALPHA_PREF = "pref_use_16bpp_alpha";

    private static final String USE_16BPP_ALPHA_PROP = "persist.sys.use_16bpp_alpha";

    private static final String SCROLLINGCACHE_PREF = "pref_scrollingcache";

    private static final String SCROLLINGCACHE_PERSIST_PROP = "persist.sys.scrollingcache";

    private static final String SCROLLINGCACHE_DEFAULT = "1";

    private static final String PURGEABLE_ASSETS_PREF = "pref_purgeable_assets";

    private static final String PURGEABLE_ASSETS_PERSIST_PROP = "persist.sys.purgeable_assets";

    private static final String PURGEABLE_ASSETS_DEFAULT = "0";

    private static final String DISABLE_BOOTANIMATION_PREF = "pref_disable_bootanimation";

    private static final String DISABLE_BOOTANIMATION_PERSIST_PROP = "persist.sys.nobootanimation";

    private static final String DISABLE_BOOTANIMATION_DEFAULT = "0";

    private static final String GMAPS_HACK_PREF = "pref_gmaps_hack";

    private static final String GMAPS_HACK_PERSIST_PROP = "persist.sys.gmaps_hack";

    private static final String GMAPS_HACK_DEFAULT = "1";

    private static final String LOCK_HOME_PREF = "pref_lock_home";

    private static final String LOCK_MMS_PREF = "pref_lock_mms";

    private CheckBoxPreference mGmapsHackPref;

    public static final String SDCARD_RUN_FILE = "/sys/devices/virtual/bdi/179:0/read_ahead_kb";

    public static final String SDCARD_PREF = "pref_sdcard";

    public static final String SDCARD_PROP = "sdcardread";

    public static final String SDCARD_PREF_DEFAULT = "2048";

    private static final int LOCK_HOME_DEFAULT = 1;

    private static final int LOCK_MMS_DEFAULT = 1;

    public static final String KSM_PREF = "pref_ksm";

    public static final String KSM_RUN_FILE = "/sys/kernel/mm/ksm/run";

    public static final String KSM_PREF_DISABLED = "0";

    public static final String KSM_PREF_ENABLED = "1";

    public static final String KSM_SLEEP_RUN_FILE = "/sys/kernel/mm/ksm/sleep_millisecs";

    public static final String KSM_SLEEP_PREF = "pref_ksm_sleep";

    public static final String KSM_SLEEP_PROP = "ksm_sleep_time";

    public static final String KSM_SLEEP_PREF_DEFAULT = "2000";

    public static final String KSM_SCAN_RUN_FILE = "/sys/kernel/mm/ksm/pages_to_scan";

    public static final String KSM_SCAN_PREF = "pref_ksm_scan";

    public static final String KSM_SCAN_PROP = "ksm_scan_time";

    public static final String KSM_SCAN_PREF_DEFAULT = "128";

    private static final String IOSCHED_PREF = "pref_iosched";

    private static final String IOSCHED_PROP = "iosched";

    private static final String IOSCHED_PERSIST_PROP = "persist.sys.ioscheduler";

    private static final String IOSCHED_DEFAULT = "sio";

    public static final String LOWMEMKILL_RUN_FILE = "/sys/module/lowmemorykiller/parameters/minfree";

    public static final String LOWMEMKILL_PROP = "lowmemkill";

    public static final String LOWMEMKILL_PREF = "pref_lowmemkill";

    public static final String LOWMEMKILL_PREF_DEFAULT = "2560,4096,6144,10240,11264,12288";

    private ListPreference mCompcachePref;

    private CheckBoxPreference mJitPref;

    private CheckBoxPreference mUseDitheringPref;

    private CheckBoxPreference mUse16bppAlphaPref;

    private ListPreference mScrollingCachePref;

    private ListPreference mSdReadAheadPref;

    private CheckBoxPreference mPurgeableAssetsPref;

    private CheckBoxPreference mDisableBootanimPref;

    private CheckBoxPreference mLockHomePref;

    private CheckBoxPreference mLockMmsPref;

    private ListPreference mHeapsizePref;

    private CheckBoxPreference mKSMPref;

    private ListPreference mKSMSleepPref;

    private ListPreference mKSMScanPref;

    private ListPreference mIoSchedPref;

    private ListPreference mLowMemKillPref;

    private AlertDialog alertDialog;

    private int swapAvailable = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.performance_settings_title_subhead);
        addPreferencesFromResource(R.xml.performance_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        
        PreferenceCategory generalCategory = (PreferenceCategory)prefSet.findPreference(GENERAL_CATEGORY);

        mCompcachePref = (ListPreference) prefSet.findPreference(COMPCACHE_PREF);
        if (isSwapAvailable()) {
	    if (SystemProperties.get(COMPCACHE_PERSIST_PROP) == "1")
                SystemProperties.set(COMPCACHE_PERSIST_PROP, COMPCACHE_DEFAULT);
            mCompcachePref.setValue(SystemProperties.get(COMPCACHE_PERSIST_PROP, COMPCACHE_DEFAULT));
            mCompcachePref.setOnPreferenceChangeListener(this);
        } else {
            generalCategory.removePreference(mCompcachePref);
        }

        mJitPref = (CheckBoxPreference) prefSet.findPreference(JIT_PREF);
        String jitMode = SystemProperties.get(JIT_PERSIST_PROP,
                SystemProperties.get(JIT_PROP, JIT_ENABLED));
        mJitPref.setChecked(JIT_ENABLED.equals(jitMode));

        mUseDitheringPref = (CheckBoxPreference) prefSet.findPreference(USE_DITHERING_PREF);
        String useDithering = SystemProperties.get(USE_DITHERING_PERSIST_PROP, USE_DITHERING_DEFAULT);
        mUseDitheringPref.setChecked("1".equals(useDithering));

        mUse16bppAlphaPref = (CheckBoxPreference) prefSet.findPreference(USE_16BPP_ALPHA_PREF);
        String use16bppAlpha = SystemProperties.get(USE_16BPP_ALPHA_PROP, "0");
        mUse16bppAlphaPref.setChecked("1".equals(use16bppAlpha));

        mScrollingCachePref = (ListPreference) prefSet.findPreference(SCROLLINGCACHE_PREF);
        mScrollingCachePref.setValue(SystemProperties.get(SCROLLINGCACHE_PERSIST_PROP,
                SystemProperties.get(SCROLLINGCACHE_PERSIST_PROP, SCROLLINGCACHE_DEFAULT)));
        mScrollingCachePref.setOnPreferenceChangeListener(this);

        mPurgeableAssetsPref = (CheckBoxPreference) prefSet.findPreference(PURGEABLE_ASSETS_PREF);
        String purgeableAssets = SystemProperties.get(PURGEABLE_ASSETS_PERSIST_PROP, PURGEABLE_ASSETS_DEFAULT);
        mPurgeableAssetsPref.setChecked("1".equals(purgeableAssets));

        mHeapsizePref = (ListPreference) prefSet.findPreference(HEAPSIZE_PREF);
        mHeapsizePref.setValue(SystemProperties.get(HEAPSIZE_PERSIST_PROP,
                SystemProperties.get(HEAPSIZE_PROP, HEAPSIZE_DEFAULT)));
        mHeapsizePref.setOnPreferenceChangeListener(this);

        mKSMPref = (CheckBoxPreference) prefSet.findPreference(KSM_PREF);
        if (CPUActivity.fileExists(KSM_RUN_FILE)) {
            mKSMPref.setChecked(KSM_PREF_ENABLED.equals(CPUActivity.readOneLine(KSM_RUN_FILE)));
        } else {
            prefSet.removePreference(mKSMPref);
        }

        mKSMSleepPref = (ListPreference) prefSet.findPreference(KSM_SLEEP_PREF);
        if (CPUActivity.fileExists(KSM_SLEEP_RUN_FILE)) {
            mKSMSleepPref.setValue(SystemProperties.get(KSM_SLEEP_PREF,
                   SystemProperties.get(KSM_SLEEP_PROP, KSM_SLEEP_PREF_DEFAULT)));
            mKSMSleepPref.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mKSMSleepPref);
        } 

        mKSMScanPref = (ListPreference) prefSet.findPreference(KSM_SCAN_PREF);
        if (CPUActivity.fileExists(KSM_SCAN_RUN_FILE)) {
            mKSMScanPref.setValue(SystemProperties.get(KSM_SCAN_PREF,
                  SystemProperties.get(KSM_SCAN_PROP, KSM_SCAN_PREF_DEFAULT)));
            mKSMScanPref.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mKSMScanPref);
        } 

        mDisableBootanimPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_BOOTANIMATION_PREF);
        String disableBootanimation = SystemProperties.get(DISABLE_BOOTANIMATION_PERSIST_PROP, DISABLE_BOOTANIMATION_DEFAULT);
        mDisableBootanimPref.setChecked("1".equals(disableBootanimation));

        mLockHomePref = (CheckBoxPreference) prefSet.findPreference(LOCK_HOME_PREF);
        mLockHomePref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCK_HOME_IN_MEMORY, LOCK_HOME_DEFAULT) == 1);

        mLockMmsPref = (CheckBoxPreference) prefSet.findPreference(LOCK_MMS_PREF);
        mLockMmsPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCK_MMS_IN_MEMORY, LOCK_MMS_DEFAULT) == 1);

        mGmapsHackPref = (CheckBoxPreference) prefSet.findPreference(GMAPS_HACK_PREF);
        String gmapshack = SystemProperties.get(GMAPS_HACK_PERSIST_PROP, GMAPS_HACK_DEFAULT);
        mGmapsHackPref.setChecked("1".equals(gmapshack));

        mLowMemKillPref = (ListPreference) prefSet.findPreference(LOWMEMKILL_PREF);
        if (CPUActivity.fileExists(LOWMEMKILL_RUN_FILE)) {
            mLowMemKillPref.setValue(SystemProperties.get(LOWMEMKILL_PREF,
                    SystemProperties.get(LOWMEMKILL_PROP, LOWMEMKILL_PREF_DEFAULT)));
            mLowMemKillPref.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mLowMemKillPref);
        }

        mIoSchedPref = (ListPreference) prefSet.findPreference(IOSCHED_PREF);
        mIoSchedPref.setValue(SystemProperties.get(IOSCHED_PERSIST_PROP,
                SystemProperties.get(IOSCHED_PROP, IOSCHED_DEFAULT)));
        mIoSchedPref.setOnPreferenceChangeListener(this);

        mSdReadAheadPref = (ListPreference) prefSet.findPreference(SDCARD_PREF);

        if (CPUActivity.fileExists(SDCARD_RUN_FILE)) {
            mSdReadAheadPref.setValue(SystemProperties.get(SDCARD_PREF,
                     SystemProperties.get(SDCARD_PROP, SDCARD_PREF_DEFAULT)));
            mSdReadAheadPref.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mSdReadAheadPref);
        }

        // Set up the warning
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.performance_settings_warning_title);
        alertDialog.setMessage(getResources().getString(R.string.performance_settings_warning));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                getResources().getString(com.android.internal.R.string.ok),
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        alertDialog.show();
    }
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
	if (preference == mJitPref) {
            SystemProperties.set(JIT_PERSIST_PROP,
                    mJitPref.isChecked() ? JIT_ENABLED : JIT_DISABLED);
            return true;
        }

        if (preference == mUseDitheringPref) {
            SystemProperties.set(USE_DITHERING_PERSIST_PROP,
                    mUseDitheringPref.isChecked() ? "1" : "0");
            return true;
        }

        if (preference == mUse16bppAlphaPref) {
            SystemProperties.set(USE_16BPP_ALPHA_PROP,
                    mUse16bppAlphaPref.isChecked() ? "1" : "0");
            return true;
        }

        if (preference == mPurgeableAssetsPref) {
            SystemProperties.set(PURGEABLE_ASSETS_PERSIST_PROP,
                    mPurgeableAssetsPref.isChecked() ? "1" : "0");
            return true;
        }

        if (preference == mKSMPref) {
            CPUActivity.writeOneLine(KSM_RUN_FILE, mKSMPref.isChecked() ? "1" : "0");
            return true;
        }

        if (preference == mDisableBootanimPref) {
            SystemProperties.set(DISABLE_BOOTANIMATION_PERSIST_PROP,
                    mDisableBootanimPref.isChecked() ? "1" : "0");
            return true;
        }

        if (preference == mLockHomePref) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCK_HOME_IN_MEMORY, mLockHomePref.isChecked() ? 1 : 0);
            return true;
        }

        if (preference == mLockMmsPref) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCK_MMS_IN_MEMORY, mLockMmsPref.isChecked() ? 1 : 0);
            return true;
        }

        if (preference == mGmapsHackPref) {
            SystemProperties.set(GMAPS_HACK_PERSIST_PROP,
                    mGmapsHackPref.isChecked() ? "1" : "0");
            return true;
        }

        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mScrollingCachePref) {
            if (newValue != null) {
                SystemProperties.set(SCROLLINGCACHE_PERSIST_PROP, (String)newValue);
                return true;
            }
        }

        if (preference == mHeapsizePref) {
            if (newValue != null) {
                SystemProperties.set(HEAPSIZE_PERSIST_PROP, (String)newValue);
                return true;
            }
        }

        if (preference == mCompcachePref) {
            if (newValue != null) {
                SystemProperties.set(COMPCACHE_PERSIST_PROP, (String)newValue);
                return true;
	    }
        }

        if (preference == mLowMemKillPref) {
            if (newValue != null) {
                SystemProperties.set(LOWMEMKILL_PROP, (String)newValue);
                CPUActivity.writeOneLine(LOWMEMKILL_RUN_FILE, (String)newValue);
                return true;
            }
        }

        if (preference == mIoSchedPref) {
            if (newValue != null) {
                SystemProperties.set(IOSCHED_PERSIST_PROP, (String)newValue);
                return true;
            }
        }

        if (preference == mSdReadAheadPref) {
            if (newValue != null) {
                SystemProperties.set(SDCARD_PROP, (String)newValue);
                CPUActivity.writeOneLine(SDCARD_RUN_FILE, (String)newValue);
                return true;
            }
        }

        if (preference == mKSMSleepPref) {
            if (newValue != null) {
                SystemProperties.set(KSM_SLEEP_PROP, (String)newValue);
                CPUActivity.writeOneLine(KSM_SLEEP_RUN_FILE, (String)newValue);
                return true;
            }
        }

        if (preference == mKSMScanPref) {
            if (newValue != null) {
                SystemProperties.set(KSM_SCAN_PROP, (String)newValue);
                CPUActivity.writeOneLine(KSM_SCAN_RUN_FILE, (String)newValue);
                return true;
            }
        }
        return false;
    }

    /**
     * Check if swap support is available on the system
     */
    private boolean isSwapAvailable() {
        if (swapAvailable < 0) {
            swapAvailable = new File("/proc/swaps").exists() ? 1 : 0;
        }
        return swapAvailable > 0;
    }

}
