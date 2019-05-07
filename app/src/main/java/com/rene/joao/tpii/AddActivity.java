package com.rene.joao.tpii;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {
    private TextView nameGetter;
    private TextView contentGetter;
    private CheckBox leafGetter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        nameGetter = findViewById(R.id.namegetterID);
        contentGetter = findViewById(R.id.contentgetterID);
        leafGetter = findViewById(R.id.leafgetterID);
        final Button buttonAddNode = findViewById(R.id.buttonAddNodeID);
        buttonAddNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedAddNode();
            }
        });
        final Button buttonAddVoltar = findViewById(R.id.buttonVoltarAddID);
        buttonAddVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Add button click
    public void clickedAddNode(){
        String name = nameGetter.getText().toString();
        String content = contentGetter.getText().toString();
        int leaf;
        if(leafGetter.isChecked())
            leaf=1;
        else
            leaf=0;
        if(!name.isEmpty()) {
            String sqlexec = "INSERT INTO nosArvore( paiID, leaf, name, content) VALUES(" + MainActivity.noPai + "," + leaf + ",\'" + name + "\',\'" + content + "\')";
            MainActivity.bancoDados.execSQL(sqlexec);
            finish();
        }
        else {
            Toast.makeText(this,"Nao eh possivel inserir um no sem nome",Toast.LENGTH_LONG).show();
        }
    }
}
