package com.mosi.tdpsync.ui;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mosi.tdpsync.R;
import com.mosi.tdpsync.utils.ValidaEspacio;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 3500;
    private static Instrumentation instrumentation;
    ValidaEspacio v = new ValidaEspacio();
    float disponible,Adescargar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        disponible = v.getMegabytesAvailable();
        Adescargar = v.verificador();
        if (disponible>Adescargar){
            Toast existe = Toast.makeText(getApplicationContext(), "No cuentas con espacio para una sincronizacion", Toast.LENGTH_LONG);
            existe.show();
        }



        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent i = new Intent().setClass(
                        MainActivity.this, LogIn.class);
                startActivity(i);
                finish();
            }
        };

        Timer timer = new Timer();
        timer.schedule(task,SPLASH_SCREEN_DELAY);
    }





}