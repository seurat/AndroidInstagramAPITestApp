package com.gasbuddy.ranialjondi.androidthingexample;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ranialjondi on 5/16/17.
 */

public class PhotoCloseUpActivity extends Activity {

    protected View mView;

    public static PhotoCloseUpActivity newInstance() {
        return new PhotoCloseUpActivity();
    }

    public PhotoCloseUpActivity() {
        super();
        // Required empty public constructor
    }

    public static String FRAGMENT_TAG = "PhotoCloseUpActivity";

}

