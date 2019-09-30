package com.example.backgroundeditor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



public class ConfigActivity extends AppCompatActivity {

    private Button btn_guardarconf,btn_recargarfondos;
    private EditText inputruta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        btn_guardarconf = findViewById(R.id.btn_guardarconf);
        btn_recargarfondos = findViewById(R.id.btn_recargarfondos);
        btn_guardarconf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarConf();
            }
        });

        final Button back = findViewById(R.id.button_back);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                onBackPressed();

            }
        });



    }


    private void guardarConf() {


        inputruta = findViewById(R.id.editTextConfig);
        String ruta = inputruta.getText().toString();


        if(ruta.compareTo("")!=0) {

            SharedPrefManager.getInstance(ConfigActivity.this)
                    .guardarConfig(ruta);
            Toast.makeText(ConfigActivity.this, "Ruta guardada con éxito!!!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(ConfigActivity.this, "Debe ingresar la ruta del servidor", Toast.LENGTH_SHORT).show();
        }
    }












}
