package com.gasbuddy.ranialjondi.androidthingexample;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by ranialjondi on 5/16/17.
 */

public class LoginFragment extends Fragment{

    View mView;

    public static String FRAGMENT_TAG = "LoginFragment";

    public MainActivity mainActivity;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    public LoginFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_login, container, false);
        initTitleScreen();
        return mView;
    }

    public void initTitleScreen() {
        //fragment login
        ((Button) mView.findViewById(R.id.btn_connect)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ((MainActivity) getActivity()).startLoginProcess();
            }
        });
    }



}
