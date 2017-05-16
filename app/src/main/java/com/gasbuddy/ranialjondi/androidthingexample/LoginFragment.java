package com.gasbuddy.ranialjondi.androidthingexample;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ranialjondi on 5/16/17.
 */

public class LoginFragment extends Fragment{

    View mView;

    public static String FRAGMENT_TAG = "LoginFragment";

    public MainActivity mainActivity;

    public static LoginFragment getInstance(Context context) {
        return new LoginFragment();
    }

    public LoginFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_main, container, false);
        initTitleScreen();
        initLoginDialog();
        return mView;
    }

    public void initTitleScreen() {

    }



}
