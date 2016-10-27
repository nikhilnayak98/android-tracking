/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Nikhil Nayak <nikhilnayak98@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.pasupatigroup.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class ShortcutActivity extends Activity {

    public static final String EXTRA_ACTION = "shortcutAction";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkShortcutAction(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkShortcutAction(intent);
    }

    private void checkShortcutAction(Intent intent) {
        if (intent.hasExtra(EXTRA_ACTION)) {
            boolean start = intent.getBooleanExtra(EXTRA_ACTION, false);
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit().putBoolean(MainActivity.KEY_STATUS, start).commit();
            if (start) {
                startService(new Intent(this, TrackingService.class));
                Toast.makeText(this, R.string.status_service_create, Toast.LENGTH_SHORT).show();
            } else {
                stopService(new Intent(this, TrackingService.class));
                Toast.makeText(this, R.string.status_service_destroy, Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

}
