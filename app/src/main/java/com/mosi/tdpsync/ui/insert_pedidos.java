package com.mosi.tdpsync.ui;


import android.annotation.TargetApi;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;

import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;

import android.content.pm.ActivityInfo;
import android.database.Cursor;

import android.graphics.Typeface;
import android.os.Build;
import android.os.RemoteException;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mosi.tdpsync.R;
import com.mosi.tdpsync.sqlite.BaseDatosPedidos;
import com.mosi.tdpsync.sqlite.ContratoPedidos;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class insert_pedidos extends AppCompatActivity {

    AutoCompleteTextView producto;
    EditText bonificaciones, cantidad;
    String product, bonuses, quantity, codigo_cia, codigo_cte, col2, usuariologueado, descripcion_cia, descripcion_cte,
            codigo_de_producto, correlativo,v0c,v1c,v2c,v3c,cusruta,cusdir,identificador_movil;
    Button insertar;
    SimpleCursorAdapter MyAdapter;
    BaseDatosPedidos database = new BaseDatosPedidos(this);
    String font_path = "fonts/LucidaGrande.ttf";
    Typeface TF;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_pedidos);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        producto = (AutoCompleteTextView) findViewById(R.id.auto_edt_producto);
        bonificaciones = (EditText) findViewById(R.id.edt_bonificaciones);
        cantidad = (EditText) findViewById(R.id.edt_cantidad);
        insertar = (Button) findViewById(R.id.bnt_inserta);
        TF = Typeface.createFromAsset(getAssets(),font_path);


        codigo_cia = getIntent().getStringExtra("cia");
        descripcion_cia = getIntent().getStringExtra("cianombre");
        codigo_cte = getIntent().getStringExtra("cte");
        descripcion_cte = getIntent().getStringExtra("ctenombre");
        usuariologueado = getIntent().getStringExtra("usuario");
        correlativo = getIntent().getStringExtra("correlativo");
        cusruta = getIntent().getStringExtra("cusruta");
        cusdir = getIntent().getStringExtra("cusdir");

        identificador_movil = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        final String [] myData = database.allDataProducts(codigo_cia);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, myData);

        producto.setAdapter(adapter);

        cantidad.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if ( i == EditorInfo.IME_ACTION_DONE){
                    AgregarProductos();
                }

                return false;
            }
        });

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgregarProductos();
            }
        });


    }

    private void AgregarProductos(){

        product = producto.getText().toString();
        bonuses = bonificaciones.getText().toString();
        quantity = cantidad.getText().toString();
        if(bonuses.equals("")||bonuses.equals(" ")||bonuses.equals(null)){
            bonuses = "0";
        }

          if (producto.getText().toString().equals("") || cantidad.getText().toString().toString().equals("")
                  || producto.getText().toString().equals(" ") || cantidad.getText().toString().equals(" ")){

          Toast mensaje_error_insert = Toast.makeText(getApplicationContext(),"Campo(s) vacio(s) por verifica y el vuelve a intentarlo", Toast.LENGTH_LONG);
          mensaje_error_insert.show();

      }else{
          ContentResolver rexec = getContentResolver();
          ArrayList<ContentProviderOperation> ops = new ArrayList<>();

          String codigo_producto = null;

          String selectcodigo = ContratoPedidos.Invptmtab.INVPTMDESC + "=?";
          String[] where = {product};
          Cursor ccodigo = rexec.query(ContratoPedidos.Invptmtab.URI_CONTENIDO, null, selectcodigo, where, null);
          if (ccodigo.moveToNext()) {
              codigo_producto = ccodigo.getString(1);
              String cc1 = ccodigo.getString(2);
              String cc2 = ccodigo.getString(3);
              String cc3 = ccodigo.getString(4);
              String cc4 = ccodigo.getString(5);
             /* System.out.println("CODIGO DE PRODUCTO "+ codigo_producto +"\n"+
                      "cc1 " + cc1 +"\n"+"cc2 " + cc2 +"\n"+"cc3 " + cc3 +"\n"+"cc4 " + cc4 +"\n");*/
              codigo_de_producto = cc1.toString();
          }

              System.out.println("CODIGO DE PRODUCTO " +codigo_de_producto);
          String invptddispi = ContratoPedidos.Invptdtab.INVPTDCOD + "=?";
          String[] where_producto ={codigo_de_producto};
          Cursor c_disponible = rexec.query(ContratoPedidos.Invptdtab.URI_CONTENIDO,null,invptddispi,where_producto,null);
          if (c_disponible.moveToNext()){
               v0c = c_disponible.getString(0);
               v1c = c_disponible.getString(1);
               v2c = c_disponible.getString(2);
               v3c = c_disponible.getString(3);

              System.out.println("v1c "+ v1c + " v2c "+ v2c + " v3c "+v3c);

          }



          String v1 = null, v2 = null, v3 = null, v4 = null, v5 = null, v6 = null;
          String selectprec = ContratoPedidos.Pr1tab.PR1COD + "=?";


          String[] whereprec = {codigo_de_producto};
          Cursor cprecio = rexec.query(ContratoPedidos.Pr1tab.URI_CONTENIDO, null, selectprec, whereprec, null);
          if (cprecio.moveToNext()) {

              v1 = cprecio.getString(1);
              v2 = cprecio.getString(2);
              v3 = cprecio.getString(3);
              v4 = cprecio.getString(4);
              v5 = cprecio.getString(5);
              v6 = cprecio.getString(6);

          }
          DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
          Date date = new Date();

          DateFormat fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          String fechaconvertida = fechaHora.format(date);
          System.out.println(fechaconvertida);
          //finaliza la obtencion de la fecha del sistema

          int icantidad = Integer.parseInt(quantity);
              if (v3c==null){
                  v3c = "0";
              }
          int idisponible = Integer.parseInt(v3c);

          ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
              String codigo = ContratoPedidos.Pedidos.PRODUCTO + "=? AND "
                      + ContratoPedidos.Pedidos.NO_PED_MOVIL + " =?";
              String[] where_codigo ={codigo_de_producto,correlativo};
              Cursor codigo_existe = rexec.query(ContratoPedidos.Pedidos.URI_CONTENIDO,null,codigo,where_codigo,null);
              if (idisponible == 0) {
                  Toast precio = Toast.makeText(getApplicationContext(),"Precio de producto no encontrado", Toast.LENGTH_LONG);
                  precio.show();
              }else{
                  if (codigo_existe.getCount()<=0){

                      if (icantidad <= idisponible){
                          String disminuye_disponible = String.valueOf(idisponible - icantidad);

                          ContentValues newValue = new ContentValues();
                          newValue.put(ContratoPedidos.InvptdtabColumnas.INVPTDDISPI,disminuye_disponible);

                          String cuando = ContratoPedidos.InvptdtabColumnas._ID + " = " + v0c + " AND "
                                  + ContratoPedidos.InvptdtabColumnas.INVPTDCOD + " = " + v2c;

                          getContentResolver().update(ContratoPedidos.CONTENT_URI_INVPTD,newValue,cuando,null);


                          ops.add(ContentProviderOperation.newInsert(ContratoPedidos.Pedidos.URI_CONTENIDO)
                                  .withValue(ContratoPedidos.PedidosColumnas.COMPANIA, codigo_cia)
                                  .withValue(ContratoPedidos.PedidosColumnas.CLIENTE, codigo_cte)
                                  .withValue(ContratoPedidos.PedidosColumnas.RUTA, identificador_movil)
                                  .withValue(ContratoPedidos.PedidosColumnas.LUGAR_ENTREGA, cusdir)
                                  .withValue(ContratoPedidos.PedidosColumnas.PRODUCTO, codigo_de_producto)
                                  .withValue(ContratoPedidos.PedidosColumnas.PRECIO_PT, v5)
                                  .withValue(ContratoPedidos.PedidosColumnas.TIPO_PEDIDO, "Pedido Movil")
                                  .withValue(ContratoPedidos.PedidosColumnas.NO_PED_MOVIL, correlativo)
                                  .withValue(ContratoPedidos.PedidosColumnas.FECHA, fechaconvertida)
                                  .withValue(ContratoPedidos.PedidosColumnas.UNIDADES_VTA, cantidad.getText().toString())
                                  .withValue(ContratoPedidos.PedidosColumnas.UNIDADES_BONI, bonuses)
                                  .withValue(ContratoPedidos.PedidosColumnas.LATITUD, "000")
                                  .withValue(ContratoPedidos.PedidosColumnas.LONGITUD, "000")
                                  .withValue(ContratoPedidos.PedidosColumnas.ID_PEDIDO, identificador_movil+"-"+correlativo)
                                  .withValue(ContratoPedidos.PedidosColumnas.USUARIO, usuariologueado)
                                  .withValue(ContratoPedidos.PedidosColumnas.ESTADO, 1)
                                  .withValue(ContratoPedidos.PedidosColumnas.PENDIENTE_INSERCION, 1)
                                  .build());

                          try {
                              rexec.applyBatch(ContratoPedidos.AUTORIDAD, ops);
                          } catch (RemoteException e) {
                              e.printStackTrace();
                          } catch (OperationApplicationException e) {
                              e.printStackTrace();
                          }

                          Intent mainIntent = new Intent().setClass(
                                  insert_pedidos.this, Toma_De_Pedido.class);
                          mainIntent.putExtra("cia" , codigo_cia);
                          mainIntent.putExtra("cianombre" , descripcion_cia);
                          mainIntent.putExtra("cte" , codigo_cte);
                          mainIntent.putExtra("ctenombre" , descripcion_cte);
                          mainIntent.putExtra("usuario" , usuariologueado);
                          mainIntent.putExtra("correlativo" ,correlativo);
                          mainIntent.putExtra("cusruta" ,cusruta);
                          mainIntent.putExtra("cusdir" ,cusdir);

                          startActivity(mainIntent);
                          finish();

                      }else{
                          Toast error_disponible = Toast.makeText(getApplicationContext(),"La cantidad ingresada es mayor al disponible. \n Tu dispones de: "+v3c, Toast.LENGTH_LONG);
                          error_disponible.show();
                      }

                  }else{
                      Toast existe = Toast.makeText(getApplicationContext(),"El producto que intentas ingresar ya existe en el pedido", Toast.LENGTH_LONG);
                      existe.show();
                  }
              }




      }
    }
}
