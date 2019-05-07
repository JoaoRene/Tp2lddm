package com.rene.joao.tpii;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView namefrag = (TextView) getView().findViewById(R.id.fragnomeID);
        TextView contentfrag = (TextView) getView().findViewById(R.id.fragcontentID);
        Button voltarfrag = (Button) getView().findViewById(R.id.fragVoltarID);
        namefrag.setText(MainActivity.noFragment.getName());
        contentfrag.setText(MainActivity.noFragment.getContent());

        // Listener for voltar button
        voltarfrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.noFragment=null;
                FragmentTransaction ft = MainActivity.fm.beginTransaction();
                ft.remove(MainFragment.this);
                ft.commit();
                MainActivity.buttonAdd.setVisibility(View.VISIBLE);
                MainActivity.buttonDelete.setVisibility(View.VISIBLE);
                MainActivity.buttonVoltar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.bringToFront();
    }
}