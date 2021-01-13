package com.herman.diagnosiscuacabatam.ui.send;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.herman.diagnosiscuacabatam.R;

public class ExitFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_exit, container, false);
        getActivity().finishAndRemoveTask();

        return root;
    }
}