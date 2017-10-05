package com.mosi.tdpsync.ui;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.mosi.tdpsync.R;
import com.mosi.tdpsync.item.item_pedido;
import com.mosi.tdpsync.sqlite.BaseDatosPedidos;
import com.mosi.tdpsync.sqlite.ContratoPedidos;
import com.mosi.tdpsync.sync.SyncAdapter;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import com.mosi.tdpsync.utils.SwipeListViewTouchListener;

public class Toma_De_Pedido extends AppCompatActivity {

    double v1=0,v2=0,v3=0,v4=0;
    TextView compania,cliente,correlativo,twv_total;
    String codigo_cia,descripcion_cia,codigo_cte,descripcion_cte,correlativo_gnr,total="0.00",
            cusruta,cusdir,invptmcod,identificador_movil,invptddispi,invptdcia,
            invptmmed,invptmiva;
    ListView listado;
    String usuariologueado;
    item_pedido adapter;
    List<String> item = null;
    Dialog dialog;
    FloatingActionButton add_producto;
    ArrayList<AdaptadorListView>items_pedidos;
    private ProgressDialog pdialog = null;
    LogIn login = new LogIn();
    String font_path = "fonts/LucidaGrande.ttf";
    Typeface TF;



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toma__pedido);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        Log.i("Toma Pedido","Iniciando pantalla de toma de pedido....");

        compania = (TextView) findViewById(R.id.twv_compania_toma);
        cliente = (TextView) findViewById(R.id.twv_cliente_toma);
        correlativo = (TextView) findViewById(R.id.twv_correlativo_toma);
        listado = (ListView) findViewById(R.id.lwv_pedidos_principal);
        twv_total = (TextView)findViewById(R.id.twv_total);
        add_producto = (FloatingActionButton)findViewById(R.id.fab);

        TF = Typeface.createFromAsset(getAssets(),font_path);

        compania.setTypeface(TF);
        cliente.setTypeface(TF);
        correlativo.setTypeface(TF);
        twv_total.setTypeface(TF);

        codigo_cia = getIntent().getStringExtra("cia");
        descripcion_cia = getIntent().getStringExtra("cianombre");

        codigo_cte = getIntent().getStringExtra("cte");
        descripcion_cte = getIntent().getStringExtra("ctenombre");

        correlativo_gnr = getIntent().getStringExtra("correlativo");
        cusruta = getIntent().getStringExtra("cusruta");
        cusdir = getIntent().getStringExtra("cusdir");

        usuariologueado = getIntent().getStringExtra("usuario");
        Log.i("Toma Pedido","Iniciando pantalla de toma de pedido....2");
        identificador_movil = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        try {
            ObtenerItemsPedido(identificador_movil+"-"+correlativo_gnr);
        }catch (Exception e){
            e.printStackTrace();
        }

        compania.setText("Compañia: " + codigo_cia + " " + descripcion_cia);
        cliente.setText("Cliente: " + codigo_cte + " " + descripcion_cte);
        correlativo.setText("Correlativo: " + correlativo_gnr);
        Log.i("Toma Pedido","Iniciando pantalla de toma de pedido....3");

        add_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view,"Mostrando Menú",Snackbar.LENGTH_SHORT)
                        .setAction("Action",null).show();*/
                cantidadProductos();
                //reviertecorrelativo();
                Intent mainIntent = new Intent().setClass(
                        Toma_De_Pedido.this, insert_pedidos.class);

                mainIntent.putExtra("cia" , codigo_cia);
                mainIntent.putExtra("cianombre" , descripcion_cia);
                mainIntent.putExtra("cte" , codigo_cte);
                mainIntent.putExtra("ctenombre" , descripcion_cte);
                mainIntent.putExtra("usuario" , usuariologueado);
                mainIntent.putExtra("correlativo" , correlativo_gnr);
                mainIntent.putExtra("cusruta" , cusruta);
                mainIntent.putExtra("cusdir" , cusdir);

                startActivity(mainIntent);

            }
        });

        listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                AdaptadorListView ItemElegido = (AdaptadorListView)adapterView.getItemAtPosition(i);
                final String producto = ItemElegido.getDescripcion();
                final String monto = ItemElegido.getMonto();
                final String unidades_vta = ItemElegido.getCantidadvendida();
                final String codigo_producto = ItemElegido.getProductos();
                final String descripcionp = ItemElegido.getDescripcion();

                PopupMenu popup = new PopupMenu(Toma_De_Pedido.this, view);
                popup.getMenuInflater().inflate(R.menu.main_menu_listview_poupmenu,popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.menu_eliminar:
                                Eliminar(producto,monto);
                                break;
                            case R.id.menu_modificar:
                                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Toma_De_Pedido.this);
                                View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_update_box, null);
                                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Toma_De_Pedido.this,R.style.MyDialogTheme);
                                alertDialogBuilderUserInput.setView(mView);

                                final EditText edt_dialog_cantidad = (EditText) mView.findViewById(R.id.edt_dialog_cantidad);



                                alertDialogBuilderUserInput
                                        .setCancelable(false)
                                        .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                                final String edt_dialog_cantidad_str = edt_dialog_cantidad.getText().toString();
                                                final int edt_dialog_cantidad_int = Integer.parseInt(edt_dialog_cantidad_str);

                                                String select = ContratoPedidos.Invptdtab.INVPTDCOD+"=?";
                                                String where[]={codigo_producto};
                                                ContentResolver exec = getContentResolver();
                                                Cursor c = exec.query(ContratoPedidos.Invptdtab.URI_CONTENIDO,null,select,where,null);

                                                if (c.moveToFirst()){
                                                    invptdcia = c.getString(1);
                                                    invptddispi = c.getString(3);

                                                    System.out.println(" INVPTDISPI "+invptddispi);
                                                }
                                                int disponible = Integer.parseInt(invptddispi);
                                                int unidades_vta_int = Integer.parseInt(unidades_vta);
                                                int diferencia = 0;

                                                System.out.println("------------> disponible "+disponible + "  unidades vta "+ unidades_vta_int + "<-----------");

                                                if (edt_dialog_cantidad_int > unidades_vta_int){
                                                    diferencia = edt_dialog_cantidad_int - unidades_vta_int;

                                                    if (diferencia > disponible){
                                                        Toast alerta1 = Toast.makeText(getApplicationContext(), "No tienes disponibilidad para agregar mas a este producto "+disponible, Toast.LENGTH_LONG);
                                                        alerta1.show();
                                                    }else{
                                                        diferencia = edt_dialog_cantidad_int - unidades_vta_int;
                                                        disponible  = disponible - diferencia;
                                                        ContentValues nuevo = new ContentValues();
                                                        nuevo.put(ContratoPedidos.Invptdtab.INVPTDDISPI,disponible);

                                                        String cuando = ContratoPedidos.Invptdtab.INVPTDCOD + " = " + codigo_producto + " AND "
                                                                + ContratoPedidos.Invptdtab.INVPTDCIA + " = " + invptdcia;
                                                        System.out.println("mensaje 0");
                                                        int response_invptdtab = getContentResolver().update(ContratoPedidos.CONTENT_URI_INVPTD,nuevo,cuando,null);
                                                        System.out.println("mensaje 1");
                                                        if (response_invptdtab==1){
                                                            ContentValues nuevo_ped = new ContentValues();
                                                            nuevo_ped.put(ContratoPedidos.PedidosColumnas.UNIDADES_VTA,edt_dialog_cantidad.getText().toString());

                                                            String cuando_ped = ContratoPedidos.PedidosColumnas.PRODUCTO + " = " + codigo_producto + " AND "
                                                                    + ContratoPedidos.PedidosColumnas.NO_PED_MOVIL + " = " + correlativo_gnr;
                                                            int response_ped = 0;
                                                            try {
                                                                // response_ped = getContentResolver().update(ContratoPedidos.CONTENT_URI_PED, nuevo_ped, cuando_ped, null);
                                                                response_ped = getContentResolver().update(ContratoPedidos.CONTENT_URI_PED,nuevo_ped,cuando_ped,null);
                                                                System.out.println("RESPONSE PEDIDOS "+ response_ped);
                                                            }catch (Exception e ){
                                                                e.printStackTrace();
                                                            }
                                                            if(response_ped==1){
                                                                try {
                                                                    ObtenerItemsPedido(identificador_movil+"-"+correlativo_gnr);
                                                                }catch (Exception e){
                                                                    e.printStackTrace();
                                                                }
                                                                Toast alerta1 = Toast.makeText(getApplicationContext(), "El producto ha sido actualizado.", Toast.LENGTH_LONG);
                                                                alerta1.show();
                                                            }else{
                                                                Toast alerta1 = Toast.makeText(getApplicationContext(), "A ocurrido un error actualizando el producto intentalo más tarde.", Toast.LENGTH_LONG);
                                                                alerta1.show();
                                                            }
                                                        }else{
                                                            Toast alerta1 = Toast.makeText(getApplicationContext(), "A ocurrido un error por favor intentalo mas tarde.", Toast.LENGTH_LONG);
                                                            alerta1.show();
                                                        }
                                                    }
                                                }else{
                                                    diferencia = unidades_vta_int - edt_dialog_cantidad_int;
                                                    disponible = disponible + diferencia;

                                                    ContentValues nuevo = new ContentValues();
                                                    nuevo.put(ContratoPedidos.Invptdtab.INVPTDDISPI,disponible);

                                                    String cuando = ContratoPedidos.Invptdtab.INVPTDCOD + " = " + codigo_producto + " AND "
                                                            + ContratoPedidos.Invptdtab.INVPTDCIA + " = " + invptdcia;
                                                    System.out.println("mensaje 0");
                                                    int response_invptdtab = getContentResolver().update(ContratoPedidos.CONTENT_URI_INVPTD,nuevo,cuando,null);
                                                    System.out.println("mensaje 1");
                                                    if (response_invptdtab==1){
                                                        ContentValues nuevo_ped = new ContentValues();
                                                        nuevo_ped.put(ContratoPedidos.PedidosColumnas.UNIDADES_VTA,edt_dialog_cantidad.getText().toString());

                                                        String cuando_ped = ContratoPedidos.PedidosColumnas.PRODUCTO + " = " + codigo_producto + " AND "
                                                                + ContratoPedidos.PedidosColumnas.NO_PED_MOVIL + " = " + correlativo_gnr;
                                                        int response_ped = 0;
                                                        try {
                                                            // response_ped = getContentResolver().update(ContratoPedidos.CONTENT_URI_PED, nuevo_ped, cuando_ped, null);
                                                            response_ped = getContentResolver().update(ContratoPedidos.CONTENT_URI_PED,nuevo_ped,cuando_ped,null);
                                                            System.out.println("RESPONSE PEDIDOS "+ response_ped);
                                                        }catch (Exception e ){
                                                            e.printStackTrace();
                                                        }
                                                        if(response_ped==1){
                                                            try {
                                                                ObtenerItemsPedido(identificador_movil+"-"+correlativo_gnr);
                                                            }catch (Exception e){
                                                                e.printStackTrace();
                                                            }
                                                            Toast alerta1 = Toast.makeText(getApplicationContext(), "El producto ha sido actualizado.", Toast.LENGTH_LONG);
                                                            alerta1.show();
                                                        }else{
                                                            Toast alerta1 = Toast.makeText(getApplicationContext(), "A ocurrido un error actualizando el producto intentalo más tarde.", Toast.LENGTH_LONG);
                                                            alerta1.show();
                                                        }
                                                    }
                                                }

                                            }
                                        })
                                        .setNegativeButton("Cancelar",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialogBox, int id) {
                                                        dialogBox.cancel();
                                                    }
                                                });

                                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                                alertDialogAndroid.show();
                                break;
                            case R.id.menu_detalle:

                                String select = ContratoPedidos.Invptmtab.INVPTMCOD + "=?"  ;
                                String where[]={codigo_producto};
                                ContentResolver exec = getContentResolver();
                                Cursor y = exec.query(ContratoPedidos.Invptmtab.URI_CONTENIDO,null,select,where,null);
                                assert y != null;
                                if (y.moveToFirst()){
                                    invptmmed = y.getString(4);
                                    invptmiva = y.getString(6);
                                }
                                String informacion =  producto +"\n"+"   Medida: "+invptmmed+ "\n"+"   Iva: "+invptmiva ;

                                AlertDialog.Builder builder = new AlertDialog.Builder(Toma_De_Pedido.this,R.style.MyDialogTheme);
                                builder.setTitle("Informacion de Producto")
                                        .setMessage(informacion)
                                        .setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                builder.create();
                                builder.show();
                                break;
                            case R.id.Imagenproducto:
                                if (codigo_producto.equals("66001")){
                                    Intent i = new Intent(Toma_De_Pedido.this,ImagenProducto.class);
                                    i.putExtra("nombrep",descripcionp);
                                    i.putExtra("codigop",codigo_producto);
                                    i.putExtra("imagen","@mipmap/producto1");
                                    startActivity(i);
                                } else if (codigo_producto.equals("65001")){
                                    Intent i = new Intent(Toma_De_Pedido.this,ImagenProducto.class);
                                    i.putExtra("nombrep",descripcionp);
                                    i.putExtra("codigop",codigo_producto);
                                    i.putExtra("imagen","@mipmap/producto5");
                                    startActivity(i);
                                }else if (codigo_producto.equals("64001")){
                                    Intent i = new Intent(Toma_De_Pedido.this,ImagenProducto.class);
                                    i.putExtra("nombrep",descripcionp);
                                    i.putExtra("codigop",codigo_producto);
                                    i.putExtra("imagen","@mipmap/producto4");
                                    startActivity(i);
                                }else{
                                    Toast fallo_cancelacion = Toast.makeText(getApplicationContext(),"Imagen del producto no disponible", Toast.LENGTH_LONG);
                                    fallo_cancelacion.show();
                                }

                                /*1574
2294
2303*/
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
                //return true;
            }
        });

        SwipeListViewTouchListener touchListener = new SwipeListViewTouchListener(listado, new SwipeListViewTouchListener.OnSwipeCallback() {
        @Override
        public void onSwipeLeft(ListView listView, int[] reverseSortedPositions) {

            AdaptadorListView ItemElegido = (AdaptadorListView)listado.getItemAtPosition(reverseSortedPositions[0]);
            final String producto = ItemElegido.getDescripcion();
            final String monto = ItemElegido.getMonto();

            Eliminar(producto,monto);
            try {
                ObtenerItemsPedido(identificador_movil+"-"+correlativo_gnr);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onSwipeRight(ListView listView, int[] reverseSortedPositions) {

        }
    },true,false);

        listado.setOnTouchListener(touchListener);
        listado.setOnScrollListener(touchListener.makeScrollListener());

}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_floating, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.cancel:
                if (cantidadProductos()<=0){
                   // reviertecorrelativo();
                    cancelacionPedido();
                }else{
                    cancelacionPedido();
                }
                return true;
            case R.id.finalizar_pedido:
                if (cantidadProductos()<=0){
                  //  reviertecorrelativo();
                    finalizarPedido();
                }else{
                    finalizarPedido();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void Customedialog(){

        dialog = new Dialog(Toma_De_Pedido.this);
        // it remove the dialog title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set the laytout in the dialog
        dialog.setContentView(R.layout.floating_menu);
        // set the background partial transparent
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams param = window.getAttributes();
        // set the layout at right bottom
        param.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        // it dismiss the dialog when click outside the dialog frame
        dialog.setCanceledOnTouchOutside(true);

        View buscar =(View) dialog.findViewById(R.id.demo4);
        View demodialog =(View) dialog.findViewById(R.id.cross);
        View finalizar_pedidos =(View) dialog.findViewById(R.id.demo5);
        View cancelarpedido =(View) dialog.findViewById(R.id.cancelar_pedido);
        View cerrarsesion =(View) dialog.findViewById(R.id.out);


        // it call when click on the item whose id is demo1.
        cerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Toma_De_Pedido.this,LogIn.class);
                startActivity(i);
                finish();
                System.exit(0);

            }
        });

        demodialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        cancelarpedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String where = ContratoPedidos.PedidosColumnas.ID_PEDIDO + " = ?";
                String[] value = {correlativo_gnr};
                int response = getContentResolver().delete(ContratoPedidos.CONTENT_URI_PED,where,value);

                if (response == 1){
                    Intent mainIntent = new Intent().setClass(
                            Toma_De_Pedido.this, Compania_Cliente.class);
                    startActivity(mainIntent);
                    finish();
                }else{
                    Toast cancelar = Toast.makeText(getApplicationContext(),"El pedido no. "+correlativo_gnr+ " no puede ser eliminado", Toast.LENGTH_LONG);
                    cancelar.show();
                    //cancelar.setGravity(Gravity.CENTER| Gravity.CENTER_VERTICAL, 0, 0);

                    dialog.dismiss();
                }



            }
        });
        /*
        *
        * */
        finalizar_pedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mainIntent = new Intent().setClass(
                        Toma_De_Pedido.this, insert_pedidos.class);

                mainIntent.putExtra("cia" , codigo_cia);
                mainIntent.putExtra("cianombre" , descripcion_cia);
                mainIntent.putExtra("cte" , codigo_cte);
                mainIntent.putExtra("ctenombre" , descripcion_cte);
                mainIntent.putExtra("usuario" , usuariologueado);
                mainIntent.putExtra("correlativo" , correlativo_gnr);
                mainIntent.putExtra("cusruta" , cusruta);
                mainIntent.putExtra("cusdir" , cusdir);

                startActivity(mainIntent);

            }
        });

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mainIntent = new Intent().setClass(
                        Toma_De_Pedido.this, Buscar_pedidos.class);


                startActivity(mainIntent);

            }
        });

        dialog.show();
    }

    public void ObtenerItemsPedido(String corr){

         items_pedidos = ObtenerItems(corr);
        adapter = new item_pedido(this,items_pedidos);
        listado.setAdapter(adapter);
        Calcula_Total();
    }

    public ArrayList<AdaptadorListView>ObtenerItems(String correlativo_pedido){
        final ArrayList<AdaptadorListView> items = new ArrayList<>();

        String seleccion = ContratoPedidos.Pedidos.ID_PEDIDO+"=?";
        String variable[]={correlativo_pedido};
        String descripcion_producto = null;
        ContentResolver exec = getContentResolver();
        Cursor c = exec.query(ContratoPedidos.Pedidos.URI_CONTENIDO,null,seleccion,variable,null);
        item =  new ArrayList<String>();

        DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
        simbolo.setDecimalSeparator('.');
        simbolo.setGroupingSeparator(',');
        DecimalFormat decimales = new DecimalFormat("###,###.###",simbolo);

        System.out.println("Cursor x " + c);

        if(c.moveToFirst()){
            do{
                String c5 = c.getString(5);
                String c6 = c.getString(6);
                String c10 = c.getString(10);
                String c11 = c.getString(11);
                String c12 = c.getString(12);

                String selection = ContratoPedidos.Invptmtab.INVPTMCOD+"=?";
                String condicion[] ={c5};
                Cursor descripcion = exec.query(ContratoPedidos.Invptmtab.URI_CONTENIDO,null,selection,condicion,null);

                System.out.println("valores de c6 " + c6 + " c10 " + c10);


                if(descripcion.moveToNext()){

                    descripcion_producto = descripcion.getString(3);

                }

                v1 = Double.parseDouble(c6);
                v2 = Double.parseDouble(c10);
                v3 = (v1*v2);


                v4 =(v4+v3);

                total = "0.00";

                total = decimales.format(v4);
                System.out.println("TOTAL: " + total);

                System.out.println(" bonificaciones "+ c12 );
                double precio = Double.parseDouble(c6);


                try {
                   // System.out.println("descripcion "+ descripcion_producto + " monto "+ decimales.format(v3)+ " bonificaciones "+ c11 + " cantidad "+ c10 + " precio "+c6);
                    items.add(new AdaptadorListView(1,descripcion_producto,decimales.format(v3),c11,c10,decimales.format(precio),c5));
                }catch (Exception e ){
                    System.out.println("ERROR AL AGREGAR A LA LISTA");
                    e.printStackTrace();
                }


            }while(c.moveToNext());

        }

        if(total==null || total == "" || total==" "){
            total = "0.00";
        }
        Calcula_Total();        //twv_total.setText(total);


        System.out.println("ITEMS "+ items);

        return items;

    }

    public void Eliminar (final String producto,String monto) {

        DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
        simbolo.setDecimalSeparator('.');
        simbolo.setGroupingSeparator(',');
        final DecimalFormat decimales = new DecimalFormat("###,###.###",simbolo);

        String total = twv_total.getText().toString();
        System.out.println("total total " + total);
        total = total.replace(",", "");
        monto = monto.replace(",", "");
        final double a, b;
        a = Double.parseDouble(total);
        b = Double.parseDouble(monto);


        final AlertDialog.Builder builder = new AlertDialog.Builder(Toma_De_Pedido.this,R.style.MyDialogTheme);


        builder.setMessage("El elemento se eliminara.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
        ContentResolver exec = getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        String selectcodigo = ContratoPedidos.Invptmtab.INVPTMDESC + "=?";
        String[] wheres = {producto};
        Cursor ccodigo = exec.query(ContratoPedidos.Invptmtab.URI_CONTENIDO, null, selectcodigo, wheres, null);
        if (ccodigo.moveToNext()) {
            invptmcod = ccodigo.getString(1);
            String cc1 = ccodigo.getString(2);


            invptmcod = cc1.toString();
        }

        try {
            exec.applyBatch(ContratoPedidos.AUTORIDAD, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

        String where = ContratoPedidos.PedidosColumnas.ID_PEDIDO + " = ?  AND "
                + ContratoPedidos.PedidosColumnas.PRODUCTO + " = ?";
        String[] value = {identificador_movil + "-" + correlativo_gnr, invptmcod};
        int response = getContentResolver().delete(ContratoPedidos.CONTENT_URI_PED, where, value);
        System.out.println("RESPONSE DELETE " + response);
        String refresh_total = String.valueOf(decimales.format(a - b));

        if (response == 1) {
            Toast exito = Toast.makeText(getApplicationContext(), "Producto Eliminado.", Toast.LENGTH_LONG);
            exito.show();
            try {
                ObtenerItemsPedido(identificador_movil + "-" + correlativo_gnr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Calcula_Total();
        } else {
            Toast fallo = Toast.makeText(getApplicationContext(), "Ocurrio un problema con tu solicitud  \n Intentalo mas tarde...", Toast.LENGTH_LONG);
            fallo.show();
            try {
                ObtenerItemsPedido(identificador_movil + "-" + correlativo_gnr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
        builder.show();

    }

    private class BackGroundRefresh extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            SyncAdapter.sincronizarAhora(Toma_De_Pedido.this,false);
            SyncAdapter.sincronizarAhora(Toma_De_Pedido.this, true);
            try{
                Thread.sleep(35000);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid){
            pdialog.dismiss();
            Toast sync = Toast.makeText(getApplicationContext(),"Sincronizacion Finalizada", Toast.LENGTH_LONG);
            sync.show();
        }
    }

    public void mensaje_error (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        builder.setTitle("Importante");
        builder.setMessage("No cuentas con una conexion a internet por favor intenta más tarde.");
        builder.setPositiveButton("Reintentar",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface builder, int id){

                if (login.executeCommand()==true){
                    Toma_De_Pedido.this.pdialog = ProgressDialog.show(Toma_De_Pedido.this,"Procesando","Realizando Sincronizacion con MYSQL...",true,false);
                    new BackGroundRefresh().execute();
                    //SyncAdapter.sincronizarAhora(this,false);
                }else{
                    mensaje_error();
                }
            }
        });
        builder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface builder, int id){

              //  System.exit(0);

            }
        });
        builder.create();
        builder.show();


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

    public void Calcula_Total(){

        DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
        simbolo.setDecimalSeparator('.');
        simbolo.setGroupingSeparator(',');
        final DecimalFormat decimales = new DecimalFormat("###,###.###",simbolo);

        SQLiteDatabase db;
        BaseDatosPedidos dba = new BaseDatosPedidos(Toma_De_Pedido.this);

        db = dba.getWritableDatabase();
        double to_decimal = 0.00;
        Cursor precio_pt = db.rawQuery("SELECT SUM(precio_pt*unidades_vta) from pedido_movil where no_ped_movil= "+ correlativo_gnr,null);
        if (precio_pt.moveToFirst()){
            String v1 = precio_pt.getString(0);
            if(v1 == null){
                v1 = "0.00";
            }

            System.out.println("VALOR DEL TOTAL / PRECIO "+v1 );
            to_decimal = Double.parseDouble(v1);
            String total = String.valueOf(decimales.format(to_decimal));
            if(total.equals("0")){
                total = "0.00";
            }
            System.out.println("VALOR DEL TOTAL / TOTAL "+total );
            twv_total.setText(total);
        }


    }

    public int cantidadProductos(){
        ContentResolver exec = getContentResolver();

        String slct = ContratoPedidos.Pedidos.NO_PED_MOVIL+"=?";
        String value[]={correlativo_gnr};
        Cursor existentes = exec.query(ContratoPedidos.Pedidos.URI_CONTENIDO,null,slct,value,null);

        int c = existentes.getCount();
        System.out.println("Productos existentes "+ c);

        return c;
    }

    public void reviertecorrelativo(){

        System.out.println("correlativo antiguo " +correlativo_gnr);
        int  r_corr = Integer.parseInt(correlativo_gnr)-1;
        System.out.println("corrlativo -1 " + r_corr);

        String n_corr = String.valueOf(r_corr);

        ContentValues newValue = new ContentValues();
        newValue.put(ContratoPedidos.TalonariosColumnas.TALCOR,n_corr);

        getContentResolver().update(ContratoPedidos.CONTENT_URI_TAL,newValue,null,null);
    }

    public void finalizarPedido(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Toma_De_Pedido.this,R.style.MyDialogTheme);

        builder.setTitle("Pedido Finalizado")
                .setMessage("El pedido ha sido guardado.\n ¿Que deseas hacer?")
                .setPositiveButton("Nuevo Pedido",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent().setClass(
                                        Toma_De_Pedido.this, Compania_Cliente.class);
                                i.putExtra("usuario" , usuariologueado);
                                startActivity(i);
                                finish();
                                // System.exit(0);
                            }
                        })
                .setNegativeButton("Volver al menú",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                       /* Intent i = new Intent(Toma_De_Pedido.this,Menu_Principal.class);
                                        startActivity(i);
//
                                        finish();*/
                                startActivity(new Intent(getBaseContext(), Menu_Principal.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                                finish();

                            }
                        });
        builder.create();
        builder.show();
    }

    public void cancelacionPedido(){
        ContentValues newValue = new ContentValues();
        newValue.put(ContratoPedidos.PedidosColumnas.REG_ANULADO,"Anulado");

        String cuando = ContratoPedidos.PedidosColumnas.NO_PED_MOVIL + " = " + correlativo_gnr;

        int afectados = getContentResolver().update(ContratoPedidos.CONTENT_URI_PED,newValue,cuando,null);
        System.out.println("AFECTADOS " +afectados);

        if (afectados>=0){
            AlertDialog.Builder builder_cancel = new AlertDialog.Builder(Toma_De_Pedido.this,R.style.MyDialogTheme);

            builder_cancel.setTitle("Cancelacion de Pedido")
                    .setMessage("El pedido ha sido cancelado.\n ¿Que deseas hacer?")
                    .setPositiveButton("Nuevo Pedido",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                  //  reviertecorrelativo();
                                    Intent i = new Intent().setClass(
                                            Toma_De_Pedido.this, Compania_Cliente.class);
                                    i.putExtra("usuario" , usuariologueado);
                                    startActivity(i);
                                    finish();
                                }
                            })
                    .setNegativeButton("Volver al menú",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                          /* Intent i = new Intent(Toma_De_Pedido.this,Menu_Principal.class);
                                           startActivity(i);
                                           finish();
                                           System.exit(0);*/
                                   // reviertecorrelativo();
                                    startActivity(new Intent(getBaseContext(), Menu_Principal.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                                    finish();
                                }
                            });
            builder_cancel.create();
            builder_cancel.show();
        }else{
            Toast fallo_cancelacion = Toast.makeText(getApplicationContext(),"El Pedido "+ correlativo_gnr + " no pude ser cancelado", Toast.LENGTH_LONG);
            fallo_cancelacion.show();
        }
    }

}

