package com.gasbuddy.ranialjondi.androidthingexample;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ranialjondi on 5/16/17.
 */

public class GalleryFragment extends Fragment {

    View mView;
    MainActivity mainActivity;

    public static String FRAGMENT_TAG = "GalleryFragment";

    public static GalleryFragment newInstance() {

        return new GalleryFragment();
    }

    public GalleryFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_user, container, false);

        return mView;
    }
    }
