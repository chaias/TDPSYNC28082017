package com.mosi.tdpsync.ui;

import android.annotation.TargetApi;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mosi.tdpsync.R;
import com.mosi.tdpsync.sqlite.BaseDatosPedidos;
import com.mosi.tdpsync.sqlite.ContratoPedidos;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Compania_Cliente extends AppCompatActivity {

    AutoCompleteTextView edt_auto_compania,edt_auto_cliente;
    String col1,cuscod,cusname,cusruta,cusdir,col2,usuariologueado,col3,col4;
    Button bnt_siguiente;
    private static final String TAG = Compania_Cliente.class.getSimpleName();
    //Adaptadores
    SimpleCursorAdapter MyAdapter,AdapterClientes;
    Spinner sp_correlativo;
    List<String> item = null;
    ArrayList<AdaptadorDePedidos> items_agregar;
    BaseDatosPedidos database = new BaseDatosPedidos(this);
    //Strings para la tabla de correlativos

    String columna0,columna1,columna2,columna3,columna4,codigo,compania,ccodigo,cliente;
    String font_path = "fonts/LucidaGrande.ttf";
    Typeface TF;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compania__cliente);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        edt_auto_compania = (AutoCompleteTextView)findViewById(R.id.auto_compania);
        edt_auto_cliente  = (AutoCompleteTextView)findViewById(R.id.auto_cliente);
        bnt_siguiente = (Button)findViewById(R.id.bnt_continuar);
        sp_correlativo = (Spinner)findViewById(R.id.sp_correlativo);
        TF = Typeface.createFromAsset(getAssets(),font_path);

        edt_auto_compania.setTypeface(TF);
        edt_auto_cliente.setTypeface(TF);
        bnt_siguiente.setTypeface(TF);

        usuariologueado = getIntent().getStringExtra("usuario");

        final ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        final ContentResolver r = getContentResolver();


        final String [] myData = database.allData();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, myData);


        edt_auto_compania.setAdapter(adapter);




        edt_auto_cliente.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, final boolean hasFocus) {

                if(hasFocus==true){
                    try{
                        boolean valida_cia = existeCompania(edt_auto_compania.getText().toString());

                        if (valida_cia == false){

                            Toast existe = Toast.makeText(getApplicationContext(),"La compañia que usted ingreso no existe\n por favor seleccione una opcion de las sugerencias.", Toast.LENGTH_LONG);
                            existe.show();
                        }else{
                            System.out.println("NOMBRE DE COMPAÑIA SELECCIONADO "+ edt_auto_compania.getText().toString());
                            String select1 = ContratoPedidos.Ciatab.CIANOMBRE +"=? ";
                            String[]where1= {edt_auto_compania.getText().toString()};
                            Cursor c1 = r.query(ContratoPedidos.Ciatab.URI_CONTENIDO,null,select1,where1,null);

                            if (c1.moveToNext()){
                                String d = c1.getString(0);
                                codigo = c1.getString(1);
                                compania = c1.getString(2);


                                System.out.println("Valores codigo: "+codigo +" compania: "+compania + " ID " + d);

                            }

                                //cargadordeclientes(codigo);
                                final String [] myDataClient = database.allDataCliente(codigo);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Compania_Cliente.this,
                                        android.R.layout.simple_dropdown_item_1line, myDataClient);

                                edt_auto_cliente.setAdapter(adapter);

                                PoblarSponner(codigo);



                            // String[] columnas ={ContratoPedidos.Custab.CUSNAME, ContratoPedidos.Usuarios.PASSWORD};

                        }


                    }catch(Exception e){
                        Log.e(TAG,e.toString() + "error!!!");
                    }
                }

            }

        });

        edt_auto_cliente.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if ( i == EditorInfo.IME_ACTION_DONE){
                    companiaCliente();
                }

                return false;
            }
        });

        sp_correlativo.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ContentResolver bd = getContentResolver();

                String seleccionado1 = sp_correlativo.getSelectedItem().toString();

                String condicion = ContratoPedidos.Talonarios.TALDES+"=?";
                String v []={seleccionado1} ;
                Cursor ct = bd.query(ContratoPedidos.Talonarios.URI_CONTENIDO,null,condicion,v,null);

                if(ct.moveToNext()){
                    columna0 = ct.getString(0);
                    columna1 = ct.getString(1);
                    columna2 = ct.getString(2);
                    columna3 = ct.getString(3);
                    columna4 = ct.getString(4);

                    System.out.println("Columna 0 "+columna0+"\nColumna 1 " + columna1 +"\nColumna 2 " + columna2 +
                            "\nColumna 3 " + columna3 +"\nColumna 4 " + columna4);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bnt_siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                companiaCliente();

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void cargadordeclientes(final String ciacod){
        final ContentResolver r = getContentResolver();
        AdapterClientes = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
                new String[]{ContratoPedidos.Custab.CUSNAME},new int[]{android.R.id.text1},
                0);

        edt_auto_cliente.setAdapter(AdapterClientes);

        AdapterClientes.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {



                String select = ContratoPedidos.Custab.CUSCIA +"=? ";
                // String[] columnas ={ContratoPedidos.Custab.CUSNAME, ContratoPedidos.Usuarios.PASSWORD};
                String[]where= {ciacod};
                Cursor c = r.query(ContratoPedidos.Custab.URI_CONTENIDO,null,select,where,null);

                if(c.moveToNext()){

                    cuscod      = c.getString(1);
                    cusname     = c.getString(3);

                    System.out.println(cuscod);
                    System.out.println(cusname);

                }
                return c;
            }
        });

        AdapterClientes.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                int index = cur.getColumnIndex(ContratoPedidos.Custab.CUSNAME);
                return cur.getString(index);
            }
        });
    }

    public void companiaCliente(){
        final ContentResolver r = getContentResolver();

        if(edt_auto_compania.getText().toString().equals("")|| edt_auto_cliente.getText().toString().equals("")||edt_auto_compania.getText().toString().equals(" ")
                ||edt_auto_cliente.getText().toString().equals(" ")){
            Toast toast11 = Toast.makeText(getApplicationContext()," Campo(s) Vacio(s) por favor revice los campos", Toast.LENGTH_LONG);
            toast11.show();
            //toast11.setGravity(Gravity.CENTER| Gravity.CENTER_VERTICAL, 0, 0);
        }else{

            boolean valida_cte = existeCliente(codigo,edt_auto_cliente.getText().toString());
            if (valida_cte == false){
                Toast existe = Toast.makeText(getApplicationContext(),"El cliente que usted ingreso no existe\n por favor seleccione una opcion de las sugerencias.", Toast.LENGTH_LONG);
                existe.show();
                edt_auto_cliente.setText("");
            }else{
                String select = ContratoPedidos.Custab.CUSNAME +"=? ";
                String[]wheres= {edt_auto_cliente.getText().toString()};
                Cursor c1 = r.query(ContratoPedidos.Custab.URI_CONTENIDO,null,select,wheres,null);

                if (c1.moveToNext()){
                    String id_ = c1.getString(0);
                    String d = c1.getString(1);
                    ccodigo = c1.getString(2);
                    cliente = c1.getString(3);
                    cusdir = c1.getString(4);
                    cusruta = c1.getString(13);

                    System.out.println("Valores codigo: "+ccodigo +" cliente: "+cliente + " ID "+id_ + " d "+ d);
                }

                //double vcr = Double.parseDouble(columna4);
                int v_correlativo = Integer.parseInt(columna4);
                int f_correlativo = Integer.parseInt(String.valueOf(v_correlativo+1));
                System.out.println("correlativo convertido");


                String corr_final = String.valueOf(f_correlativo);
                System.out.println("Correlativo final "+ corr_final);

                //Codigo el cual actualiza el correlativo
                //////////////////////////////////////////////////////////////////////////////////////////////////////////

                ContentValues newValue = new ContentValues();
                newValue.put(ContratoPedidos.TalonariosColumnas.TALCOR,corr_final);

                    /*String where = ContratoPedidos.TalonariosColumnas._ID + " = " + columna0 + " AND "
                            + ContratoPedidos.TalonariosColumnas.TALCIA + " = " + col1 /*+ " AND "
                            + ContratoPedidos.TalonariosColumnas.TALDES + " = " + sp_correlativo.getSelectedItem().toString()*/;

                getContentResolver().update(ContratoPedidos.CONTENT_URI_TAL,newValue,null,null);

                //////////////////////////////////////////////////////////////////////////////////////////////////////////

                Log.d("Talonarios", "Registros de Talonarios");
                DatabaseUtils.dumpCursor(r.query(ContratoPedidos.Talonarios.URI_CONTENIDO, null, null, null, null));

                Intent mainIntent = new Intent().setClass(
                        Compania_Cliente.this, Toma_De_Pedido.class);
                System.out.println(col1 + " " + col2 + " " +cuscod+ " " +cusname);

                mainIntent.putExtra("cia" , codigo);
                mainIntent.putExtra("cianombre" , compania);
                mainIntent.putExtra("cte" , ccodigo);
                mainIntent.putExtra("ctenombre" , cliente);
                mainIntent.putExtra("usuario" , usuariologueado);
                mainIntent.putExtra("correlativo" , columna4);
                mainIntent.putExtra("cusruta" , cusruta);
                mainIntent.putExtra("cusdir" , cusdir);
                startActivity(mainIntent);
                finish();

            }
        }

    }

    public void PoblarSpinnerCorrelativo(final String cia){


        ContentResolver baseDatos = getContentResolver();
        String where = ContratoPedidos.Talonarios.TALCIA+"=?";
        String valor[]={cia};

        //Creamos el cursor
        Cursor c = baseDatos.query(ContratoPedidos.Talonarios.URI_CONTENIDO,null,where,valor,null);
        //Creamos el adaptador
        SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(this,android.R.layout.simple_spinner_item,c,new String[] {"taldes"},    new int[] {android.R.id.text1});
        //Añadimos el layout para el menú
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Le indicamos al spinner el adaptador a usar
        sp_correlativo.setAdapter(adapter2);


    }

    public void PoblarSponner(final String cia){
        System.out.println("valor de cia para correlativos " + cia);
        String descripcion_producto = null;
        String where = ContratoPedidos.Talonarios.TALCIA+" IS NOT NULL";
        String valor[]={cia};
        items_agregar  = new ArrayList<AdaptadorDePedidos>();
        ContentResolver exec = getContentResolver();
        Cursor c = exec.query(ContratoPedidos.Talonarios.URI_CONTENIDO,null,where,null,null);
        item =  new ArrayList<String>();
        DecimalFormat decimales = new DecimalFormat("0.00");

        if(c.moveToFirst()){
            do{
                String c1 = c.getString(1);
                String c2 = c.getString(2);
                String c3 = c.getString(3);
                String c4 = c.getString(4);

                double v1=0,v2=0,v3=0;
                System.out.println("c1: "+c1 + " c2: " + c2 + " c3: "+c3 + " c4: "+c4);

                item.add(c3);

            }while(c.moveToNext());

        }

        ArrayAdapter<String> adaptador
                = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,item);
        sp_correlativo.setAdapter(adaptador);

    }
    public boolean existeCompania(String compania){
        boolean validador = false;
        ContentResolver r = getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        String columnas =ContratoPedidos.Ciatab.CIANOMBRE  + "=?";
        String[]where= {compania};
        Cursor c = r.query(ContratoPedidos.Ciatab.URI_CONTENIDO,null,columnas,where,null);

        if(c.getCount()==0){
            validador = false;
        }else{
            validador = true;
        }
        return validador;
    }

    public boolean existeCliente(String cia, String cte){
        boolean validador = false;
        ContentResolver r = getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        String columnas =ContratoPedidos.Custab.CUSNAME  + "=? and " + ContratoPedidos.Custab.CUSCIA + " =?";
        String[]where= {cte,cia};
        Cursor c = r.query(ContratoPedidos.Custab.URI_CONTENIDO,null,columnas,where,null);

        if(c.getCount()==0){
            validador = false;
        }else{
            validador = true;
        }

        return validador;
    }

}
