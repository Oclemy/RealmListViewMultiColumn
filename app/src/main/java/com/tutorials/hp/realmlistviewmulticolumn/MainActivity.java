package com.tutorials.hp.realmlistviewmulticolumn;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.tutorials.hp.realmlistviewmulticolumn.m_Realm.RealmHelper;
import com.tutorials.hp.realmlistviewmulticolumn.m_Realm.Spacecraft;
import com.tutorials.hp.realmlistviewmulticolumn.m_UI.CustomAdapter;

import io.realm.Realm;
import io.realm.RealmChangeListener;


public class MainActivity extends AppCompatActivity {

    Realm realm;
    RealmChangeListener realmChangeListener;
    CustomAdapter adapter;
    ListView lv;
    EditText nameEditTxt,propEditTxt,descEditTxt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //REFRERENCE LV
        lv= (ListView) findViewById(R.id.lv);

        //SETUP REALM
        realm=Realm.getDefaultInstance();
        final RealmHelper helper=new RealmHelper(realm);

        //RETRIEVE
        helper.retrieveFromDB();

        //setup adapter
        adapter=new CustomAdapter(this,helper.justRefresh());
        lv.setAdapter(adapter);

        //HANDLE DATA CHANGE EVENTS AND REFESH
        realmChangeListener=new RealmChangeListener() {
            @Override
            public void onChange() {
                //RERFRESH
                adapter=new CustomAdapter(MainActivity.this,helper.justRefresh());
                lv.setAdapter(adapter);
            }
        };

        //ADD CHANGE LIST TO REALM
        realm.addChangeListener(realmChangeListener);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //SHOW DIALOG
                 displayInputDialog();
            }
        });
    }

    private void displayInputDialog()
    {
        Dialog d=new Dialog(this);
        d.setTitle("Save To Realm DB");
        d.setContentView(R.layout.input_dialog);

        //EDITtXTS
        nameEditTxt= (EditText) d.findViewById(R.id.nameEditText);
        propEditTxt= (EditText) d.findViewById(R.id.propellantEditText);
        descEditTxt= (EditText) d.findViewById(R.id.descEditText);
        Button saveBtn= (Button) d.findViewById(R.id.saveBtn);

        //SAVE
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GET DATA
                String name=nameEditTxt.getText().toString();
                String propellant=propEditTxt.getText().toString();
                String desc=descEditTxt.getText().toString();

                //ASSIGN THEM TO SPACECRAFT
                Spacecraft s=new Spacecraft();
                s.setName(name);
                s.setPropellant(propellant);
                s.setDescription(desc);

                //VALIDATE
                if(name != null && name.length()>0)
                {
                    //SAVE
                    RealmHelper helper=new RealmHelper(realm);
                    if(helper.save(s))
                    {
                        nameEditTxt.setText("");
                        propEditTxt.setText("");
                        descEditTxt.setText("");

                    }else {

                        Toast.makeText(MainActivity.this,"Input is not correct",Toast.LENGTH_SHORT).show();

                    }
                }else {

                    Toast.makeText(MainActivity.this,"Name cannot be empty",Toast.LENGTH_SHORT).show();

                }

            }
        });


        d.show();
    }

     //release res


    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.removeChangeListener(realmChangeListener);
        realm.close();
    }
}

















