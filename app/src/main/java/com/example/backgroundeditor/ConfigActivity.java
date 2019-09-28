package com.example.backgroundeditor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {

    private Button btn_guardarconf;
    private EditText inputruta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        btn_guardarconf = findViewById(R.id.btn_guardarconf);
        btn_guardarconf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarConf();
            }
        });

    }


    private void guardarConf() {


        inputruta = findViewById(R.id.editTextConfig);
        String ruta = inputruta.getText().toString();
        SharedPrefManager.getInstance(ConfigActivity.this)
                .guardarConfig(ruta);



    }

}
