package com.mosi.tdpsync.ui;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mosi.tdpsync.R;
import com.mosi.tdpsync.sqlite.ContratoPedidos;
import com.mosi.tdpsync.sync.SyncAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LogIn extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText edt_usuario,edt_password;
    Button iniciar_sesion;
    TextView twv_id_dispositivo,twv_mosi;
    public AdaptadorDePedidos adaptador;
     ProgressDialog pdialog = null;
    String col1,col2,identificador_movil;
    ImageButton img_bnt_sync;
    String font_path = "fonts/LucidaGrande.ttf";
    Typeface TF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();

        TF = Typeface.createFromAsset(getAssets(),font_path);
        Log.i("Log In","Iniciando aplicacion....");

        final ContentResolver r = getContentResolver();
        Log.i("Log In","Iniciando aplicacion....2");
        Log.d("Pedidos", "Pedidos");

        DatabaseUtils.dumpCursor(r.query(ContratoPedidos.Pedidos.URI_CONTENIDO, null, null, null, null));

        img_bnt_sync = (ImageButton)findViewById(R.id.img_bnt_sync);
        edt_usuario =  (EditText)findViewById(R.id.usuario);
        edt_password = (EditText)findViewById(R.id.password);
        twv_id_dispositivo = (TextView)findViewById(R.id.twv_id_dispositivo);
        twv_mosi = (TextView)findViewById(R.id.twv_mosi);

        Log.d("Log In","Iniciando aplicacion.... 3 ");
        iniciar_sesion = (Button)findViewById(R.id.bnt_login);
        adaptador = new AdaptadorDePedidos(this);

        Log.i("Log In","Iniciando aplicacion.... 4 ");

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation_button);
        img_bnt_sync.setAnimation(animation);

        /////CODIGO PARA CAMBIAR LA TIPOGRAFIA
        edt_usuario.setTypeface(TF);
        edt_password.setTypeface(TF);
        iniciar_sesion.setTypeface(TF);
        twv_id_dispositivo.setTypeface(TF);
        twv_mosi.setTypeface(TF);
        //////
        identificador_movil = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        twv_id_dispositivo.setText("ID: "+identificador_movil);

        verifica_talonarios();

        Log.i("Log In","Iniciando aplicacion.... 5 ");

        img_bnt_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (executeCommand()){
                    pdialog = ProgressDialog.show(LogIn.this,"Procesando Sincronizacion","Realizando Sincronizacion con el Servidor...",true,false);
                    new BackGroundRefresh().execute();
                }else{
                    mensaje_error();
                }
            }
        });

        edt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if ( i == EditorInfo.IME_ACTION_DONE){
                    logIn();
                }

                return false;
            }
        });

        iniciar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        try {
            if (id == R.id.action_sync) {
                if (executeCommand()){
                    this.pdialog = ProgressDialog.show(this,"Procesando Sincronizacion","Realizando Sincronizacion con el Servidor...",true,false);
                    new BackGroundRefresh().execute();
                }else{
                    mensaje_error();
                }

                //SyncAdapter.sincronizarAhora(this,false);

                return true;
            }
        }catch (Exception e ){
            e.printStackTrace();
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //emptyView.setText("Cargando datos...");
        // Consultar todos los registros
        Log.i("LOG IN", "on create loader");
        return new android.support.v4.content.CursorLoader(this,ContratoPedidos.CONTENT_URI_PED,null,null,null,null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        Log.i("LOG IN", "on loader FINISHED");

        adaptador.swapCursor(data);
        Log.i("LOG IN", "on loader FINISHED 2" );
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        Log.i("LOG IN", "on loader reset");

        adaptador.swapCursor(null);
    }

    private class BackGroundRefresh extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            try{
                
                SyncAdapter.sincronizarAhora(LogIn.this,false);
                SyncAdapter.sincronizarAhora(LogIn.this, true);
                Thread.sleep(30000);
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
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
            System.out.println(" Exception:"+ignore);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(" Exception:"+e);
        }
        return false;


    }

    public void logIn(){
        final ContentResolver r = getContentResolver();

        if (edt_usuario.getText().toString().equals("") || edt_usuario.getText().toString().equals(" ") || edt_password.getText().toString().equals("") || edt_password.getText().toString().equals(" ")){

            Toast campos_vacios = Toast.makeText(getApplicationContext(),"Debes ingresar un usuario y contraseña para poder iniciar sesion... \n Vuelve a intentarlo...", Toast.LENGTH_LONG);
            campos_vacios.show();
        }else{
            String usuario = edt_usuario.getText().toString();
            String password = edt_password.getText().toString();
            System.out.println("Tomando valores de la caja de texto "+ usuario +" " + password);
            String select = ContratoPedidos.Usuarios.NOMBRE +"=?";
            String[]where= {usuario};
            Cursor c = r.query(ContratoPedidos.Usuarios.URI_CONTENIDO,null,select,where,null);

            while(c.moveToNext()){

                col1 = c.getString(1);
                col2 = c.getString(2);

                System.out.println("String 1 " + col1 + " String 2 "+ col2);

            }

            if(edt_password.getText().toString().equals(col2) && edt_usuario.getText().toString().equals(col1)){
                Intent mainIntent = new Intent().setClass(
                        LogIn.this, Menu_Principal.class);
                mainIntent.putExtra("usuario" , usuario);

                        /*int response = getContentResolver().delete(ContratoPedidos.CONTENT_URI_TAL,null,null);
                        System.out.println("VALOR DE RESPONSE  ELIMINAR "+  response);*/

                Log.d("Pedidos", "Pedidos");
                DatabaseUtils.dumpCursor(r.query(ContratoPedidos.Pedidos.URI_CONTENIDO, null, null, null, null));

                Log.d("Talonarios", "talonarios");
                DatabaseUtils.dumpCursor(r.query(ContratoPedidos.Talonarios.URI_CONTENIDO, null, null, null, null));
                startActivity(mainIntent);
                finish();

            }else{

                Toast toast11 = Toast.makeText(getApplicationContext(),"Nombre de usuario y password incorrectos, Por favor verifique y vuelva a intentarlo", Toast.LENGTH_LONG);
                toast11.show();

            }

        }

    }

    public void mensaje_error (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        builder.setTitle("Importante");
        builder.setMessage("No cuentas con una conexion a internet por favor intenta más tarde.");
        builder.setPositiveButton("Reintentar",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface builder, int id){

                if (executeCommand()){
                    LogIn.this.pdialog = ProgressDialog.show(LogIn.this,"Procesando","Realizando Sincronizacion con MYSQL...",true,false);
                    new BackGroundRefresh().execute();
                }else{
                    mensaje_error();
                }
            }
        });
        builder.setNegativeButton("Salir",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface builder, int id){

                System.exit(0);

            }
        });
        builder.create();
        builder.show();


    }

    public void verifica_talonarios(){
        ContentResolver r = getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        String columnas =ContratoPedidos.Talonarios.TALDES  + "=?";
        String[]where= {"PEDIDOS MOVILES"};
        Cursor c = r.query(ContratoPedidos.Talonarios.URI_CONTENIDO,null,columnas,where,null);

        if(c.getCount()==0){
             ops.add(ContentProviderOperation.newInsert(ContratoPedidos.Talonarios.URI_CONTENIDO)
                .withValue(ContratoPedidos.TalonariosColumnas.TALCOD, 1)
                .withValue(ContratoPedidos.TalonariosColumnas.TALCIA, 1)
                .withValue(ContratoPedidos.TalonariosColumnas.TALDES, "PEDIDOS MOVILES")
                .withValue(ContratoPedidos.TalonariosColumnas.TALCOR, 1)
                .withValue(ContratoPedidos.TalonariosColumnas.ESTADO, 1)
                .withValue(ContratoPedidos.TalonariosColumnas.PENDIENTE_INSERCION, 1)
                .build());

        try {
            r.applyBatch(ContratoPedidos.AUTORIDAD, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
        }




    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
           /* AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
            builder.setTitle("Salir");
            builder.setMessage("¿Deseas salir de la aplicacion?");
            builder.setPositiveButton("Si",new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface builder, int id){
                    finish();*/
                    System.exit(0);
            /*    }
            });
            builder.setNegativeButton("No",new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface builder, int id){



                }
            });
            builder.create();
            builder.show();*/
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}