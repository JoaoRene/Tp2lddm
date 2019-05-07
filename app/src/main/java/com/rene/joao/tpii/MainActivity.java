package com.rene.joao.tpii;
import android.support.v4.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, SensorEventListener  {

    // Variables
    private ArrayList<NoArvore> theList;
    public static int noPai;
    public static int noAvo;
    public static NoArvore noFragment;
    public static SQLiteDatabase bancoDados;
    public static MyRecyclerViewAdapter adapter;
    public static FragmentManager fm;
    public Sensor mySensor;
    public SensorManager SM;
    public float acelVal, acelLast, shake;
    public static Button buttonDelete;
    public static Button buttonVoltar;
    public static Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Buttons initialization and click listeners definition
        buttonAdd = findViewById(R.id.buttonAddID);
        buttonVoltar = findViewById(R.id.buttonVoltarID);
        buttonDelete = findViewById(R.id.buttonDeleteID);
        theList = new ArrayList<>();
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedAdd();
            }
        });
        buttonVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedVoltar();
            }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedDelete();
            }
        });

        // Data Base initialization
        try {
            bancoDados=openOrCreateDatabase("appBD", MODE_PRIVATE, null);
            bancoDados.execSQL("DROP TABLE IF EXISTS nosArvore"); // This line is used only for testing, it should be removed.
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS nosArvore( noID INTEGER PRIMARY KEY, paiID INT, leaf INT, name VARCHAR, content VARCHAR, FOREIGN KEY(paiID) REFERENCES NosArvore(noID) ON DELETE CASCADE)");
            // Filling BD for testing.
            Cursor cursor = bancoDados.rawQuery(" SELECT * FROM nosArvore ", null);
            if (!cursor.moveToFirst()){
                bancoDados.execSQL("INSERT INTO nosArvore( paiID, leaf, name, content) VALUES(  -1, 0, 'No1', 'Filho do ROOT' )");
                bancoDados.execSQL("INSERT INTO nosArvore( paiID, leaf, name, content) VALUES(  -1, 0, 'No2', 'Filho do ROOT' )");
                bancoDados.execSQL("INSERT INTO nosArvore( paiID, leaf, name, content) VALUES(  -1, 1, 'Folha1', 'Filho do ROOT' )");
                bancoDados.execSQL("INSERT INTO nosArvore( paiID, leaf, name, content) VALUES(  1, 0, 'No1', 'Filho do 1' )");
                bancoDados.execSQL("INSERT INTO nosArvore( paiID, leaf, name, content) VALUES(  1, 0, 'No2', 'Filho do 1' )");
                bancoDados.execSQL("INSERT INTO nosArvore( paiID, leaf, name, content) VALUES(  1, 1, 'Folha2', 'Filho do 1' )");
                bancoDados.execSQL("INSERT INTO nosArvore( paiID, leaf, name, content) VALUES(  2, 0, 'No1', 'Filho do 2' )");
                bancoDados.execSQL("INSERT INTO nosArvore( paiID, leaf, name, content) VALUES(  2, 0, 'No2', 'Filho do 2' )");
                bancoDados.execSQL("INSERT INTO nosArvore( paiID, leaf, name, content) VALUES(  2, 1, 'Folha3', 'Filho do 2' )");
            }
        } catch (Exception e) {
            Log.e("MYAPPdebug", "exception", e);
        }

        // Sensor Configuration
        SM=(SensorManager) getSystemService(SENSOR_SERVICE);
        mySensor=SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;

        // Recycler View initialization
        noPai=-1;
        noAvo=-1;
        RecyclerView recyclerView = findViewById(R.id.RecyclerViewID);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, theList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        loadTheList();

        // Fragment manager initialization
        fm = getSupportFragmentManager();
    }

    // Accelerometer sensor, a shake deletes the leaf open in fragment.
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        acelLast = acelVal;
        acelVal = (float) Math.sqrt((double) (x*x + y*y + z*z));
        float delta = acelVal - acelLast;
        shake = shake * 0.9f + delta;
        if (shake > 12) {
            if(noFragment!=null) {
                try {
                   String sqlexec = "DELETE FROM nosArvore WHERE noID = "+noFragment.getNoID();
                   bancoDados.execSQL(sqlexec);
                   FragmentTransaction ft = fm.beginTransaction();
                   ft.remove(fm.findFragmentById(R.id.fragment_content));
                   ft.commit();
                   noFragment=null;
                   loadTheList();
                   buttonAdd.setVisibility(View.VISIBLE);
                   buttonDelete.setVisibility(View.VISIBLE);
                   buttonVoltar.setVisibility(View.VISIBLE);
                   Toast.makeText(this, "Folha deletada!", Toast.LENGTH_SHORT).show();
                } catch( Exception e) {
                    Log.e("ToDebug->",e.toString());
                }
            }
            else {
                Toast.makeText(this, "Nao ha folhas abertas...", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    // Method used to update theList and reload the Recycler View
    public void loadTheList(){
        theList.clear();
        Cursor cursor = bancoDados.rawQuery("SELECT * FROM nosArvore WHERE paiID = " + noPai, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int noID = Integer.parseInt(cursor.getString(0));
            int paiID = Integer.parseInt(cursor.getString(1));
            boolean leaf;
            if(Integer.parseInt(cursor.getString(2)) != 0)
                leaf = true;
            else
                leaf = false;
            String name = cursor.getString(3);
            String content = cursor.getString(4);
            NoArvore node = new NoArvore(noID, paiID, leaf, name, content);
            theList.add(node);
            cursor.moveToNext();
        }
        adapter.notifyDataSetChanged();
    }
    // Metodo on Click da recycler
    @Override
    public void onItemClick(View view, int position) {
        if(!theList.get(position).isLeaf()) {
            noAvo=noPai;
            noPai=theList.get(position).getNoID();
            loadTheList();
        }else{
            noFragment = theList.get(position);
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fragment_content, new MainFragment());
            ft.commit();
            buttonAdd.setVisibility(View.INVISIBLE);
            buttonDelete.setVisibility(View.INVISIBLE);
            buttonVoltar.setVisibility(View.INVISIBLE);
        }
    }
    // Buttons on click method
    private void clickedAdd() {
        startActivity(new Intent(this,AddActivity.class));
    }
    private void clickedVoltar() {
        if(noPai != noAvo) {
            noPai=noAvo;
            String sqlexec = "SELECT * FROM nosArvore WHERE noID = "+ noPai;
            Cursor cursor = bancoDados.rawQuery(sqlexec,null);
            if(cursor.moveToFirst())
                noAvo=Integer.parseInt(cursor.getString(1));
            else
                noAvo=-1;
            loadTheList();
        }
    }
    private void clickedDelete() {
        if(noPai!=-1) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Confirmar Remoção");
            alertDialog.setMessage("O elemento aberto contem todos os elementos mostrados na lista. Voce confirma sua remoção?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SIM",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteNo();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NAO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        else
            Toast.makeText(this, "Este elemento é a raiz e não pode ser excluído", Toast.LENGTH_LONG).show();
    }
    // Method to delete a node.
    private void deleteNo(){
        String sqlexec = "DELETE FROM nosArvore WHERE noID = "+noPai;
        bancoDados.execSQL(sqlexec);
        noPai=noAvo;
        if (noPai!=-1) {
            sqlexec = "SELECT * FROM nosArvore WHERE noID = " + noPai;
            Cursor cursor = bancoDados.rawQuery(sqlexec, null);
            cursor.moveToFirst();
            noAvo=Integer.parseInt(cursor.getString(1));
        }
        loadTheList();
    }
    @Override
    protected void onStart() {
        super.onStart();
        this.getDelegate().onStart();
        loadTheList();
        buttonAdd.setVisibility(View.VISIBLE);
        buttonDelete.setVisibility(View.VISIBLE);
        buttonVoltar.setVisibility(View.VISIBLE);
    }
}
