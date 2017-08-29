package com.mosi.tdpsync.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mosi.tdpsync.R;
import com.mosi.tdpsync.sync.SyncAdapter;

import java.io.IOException;

public class Menu_Principal extends AppCompatActivity {

    String usuariologueado;
    public ProgressDialog pdialog = null;
    Button bnt_pedido_nuevo,bnt_buscar_pedido,bnt_sincronizar,bnt_consultaprecios;
    ImageButton img_bnt_salir;
    String font_path = "fonts/LucidaGrande.ttf";
    Typeface TF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__principal);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();

        TF = Typeface.createFromAsset(getAssets(),font_path);

        bnt_pedido_nuevo = (Button)findViewById(R.id.bnt_pedido_nvo);
        bnt_buscar_pedido = (Button)findViewById(R.id.bnt_buscar_pedido);
        bnt_sincronizar = (Button)findViewById(R.id.bnt_sincronizar);
        bnt_consultaprecios = (Button)findViewById(R.id.bnt_consultaprecios);
        img_bnt_salir = (ImageButton)findViewById(R.id.img_salir);

        usuariologueado = getIntent().getStringExtra("usuario");

        bnt_pedido_nuevo.setTypeface(TF);
        bnt_buscar_pedido.setTypeface(TF);
        bnt_sincronizar.setTypeface(TF);
        bnt_consultaprecios.setTypeface(TF);


        bnt_pedido_nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent().setClass(
                        Menu_Principal.this, Compania_Cliente.class);
                mainIntent.putExtra("usuario" , usuariologueado);
                startActivity(mainIntent);

            }
        });

        bnt_buscar_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar snackbar = Snackbar.make(view,"Busqueda de Pedido",Snackbar.LENGTH_LONG);
                snackbar.show();*/
                Intent mainIntent = new Intent().setClass(
                        Menu_Principal.this, Buscar_pedidos.class);
                startActivity(mainIntent);
            }
        });

        bnt_sincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (executeCommand()){
                    //this.pdialog = ProgressDialog.show(this,"Procesando Sincronizacion","Realizando Sincronizacion con el Servidor...",true,false);
                    Menu_Principal.this.pdialog = ProgressDialog.show(Menu_Principal.this,"Procesando Sincronizacion","Realizando Sincronizacion con el servidor...",true,false);
                    new BackGroundRefresh().execute();
                }else{
                    mensaje_error();
                }
                /*if (executeCommand()){
                    this.pdialog = ProgressDialog.show(this,"Procesando Sincronizacion","Realizando Sincronizacion con el Servidor...",true,false);
                    new BackGroundRefresh().execute();
                }else{
                    mensaje_error();
                }*/
            }
        });
        img_bnt_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Menu_Principal.this,LogIn.class);
                startActivity(i);
                finish();
                //System.exit(0);
            }
        });

        bnt_consultaprecios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent().setClass(
                        Menu_Principal.this, ConsultaPrecio.class);
                mainIntent.putExtra("usuario" , usuariologueado);
                startActivity(mainIntent);
            }
        });

    }

    private class BackGroundRefresh extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
//pdqkgf29ue

            //codigo uber de Luis Rosal
                //


            //43906777
            try{
                SyncAdapter.sincronizarAhora(Menu_Principal.this,true);
                SyncAdapter.sincronizarAhora(Menu_Principal.this, false);
                Thread.sleep(35000);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid){
            pdialog.dismiss();
            Toast sync = Toast.makeText(getApplicationContext(),"Sincronizacion con el servidor finalizada.", Toast.LENGTH_LONG);
            sync.show();
        }
    }

    public boolean executeCommand(){
        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 -w 1 www.youtube.com");
            int mExitValue = mIpAddrProcess.waitFor();
            System.out.println(" mExitValue "+ mExitValue);
            switch (mExitValue) {
                case 0:
                    return true;
                default:
                    return false;
            }
        }
        catch (InterruptedException | IOException ignore)
        {
            ignore.printStackTrace();
            System.out.println(" Exception:"+ignore);
        }
        return false;


    }

    public void mensaje_error (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        builder.setTitle("Importante");
        builder.setMessage("No cuentas con una conexion a internet por favor intenta más tarde.");
        builder.setPositiveButton("Reintentar",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface builder, int id){

                if (executeCommand()){
                    Menu_Principal.this.pdialog = ProgressDialog.show(Menu_Principal.this,"Procesando","Realizando Sincronizacion con MYSQL...",true,false);
                    new BackGroundRefresh().execute();
                }else{
                    mensaje_error();
                }
            }
        });
        builder.setNegativeButton("Salir",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface builder, int id){
                System.exit(0);
                finish();
            }
        });
        builder.create();
        builder.show();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

