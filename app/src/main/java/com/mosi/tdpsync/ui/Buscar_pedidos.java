package com.mosi.tdpsync.ui;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mosi.tdpsync.R;
import com.mosi.tdpsync.item.item_pedido;
import com.mosi.tdpsync.sqlite.BaseDatosPedidos;
import com.mosi.tdpsync.sqlite.ContratoPedidos;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class Buscar_pedidos extends AppCompatActivity {

    EditText edt_buscar_pedido;
    double v1 = 0, v2 = 0, v3 = 0, v4 = 0;
    Button bnt_buscar_pedido, bnt_informacion;
    ListView listado;
    CheckBox cHKbx_sincronizado, cHKbx_anulado;
    List<String> item = null;
    item_pedido adapter;
    TextView txt_correlativo,twv_cliente;
    String total, compania, cliente, cia, cte, ultimoCorr;
    final ArrayList<AdaptadorListView> items = new ArrayList<>();
    ArrayList<AdaptadorDePedidos> items_agregar;
    ArrayList<AdaptadorListView> items_pedidos;
    String font_path = "fonts/LucidaGrande.ttf";
    Typeface TF;
    String pureba;
    //C:\Proyectos Android Studio\TDPSYNC\app\build\outputs\apk
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_pedidos);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        edt_buscar_pedido = (EditText) findViewById(R.id.edt_correlativo_buscar);
        listado = (ListView) findViewById(R.id.lwv_recuperacion);
        txt_correlativo = (TextView) findViewById(R.id.txt_no_correlativo);
        twv_cliente = (TextView) findViewById(R.id.twv_cliente);
        cHKbx_sincronizado = (CheckBox) findViewById(R.id.chkbx_sincronizado);
        cHKbx_anulado = (CheckBox) findViewById(R.id.chkbx_anulado);


        TF = Typeface.createFromAsset(getAssets(), font_path);

        edt_buscar_pedido.setTypeface(TF);
        twv_cliente.setTypeface(TF);
        txt_correlativo.setTypeface(TF);
        cHKbx_anulado.setTypeface(TF);
        cHKbx_sincronizado.setTypeface(TF);


        ultimoCorr = ultimoCorrelativo();
        int xs = Integer.parseInt(ultimoCorr);
        ultimoCorr = String.valueOf(xs - 1);
        txt_correlativo.setText("Ultimo Correlativo: " + ultimoCorr);




        edt_buscar_pedido.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if ( i == EditorInfo.IME_ACTION_SEARCH){
                    String valor_caja = edt_buscar_pedido.getText().toString();
                    try {
                        boolean valida_pedido = existePedido(valor_caja);
                        System.out.println("valida_pedido = " + valida_pedido);

                        if (valida_pedido!=true) {
                            Toast existe = Toast.makeText(getApplicationContext(), "El pedido que usted esta buscando no existe \n por favor intente con otro pedido.", Toast.LENGTH_LONG);
                            existe.show();
                        } else {
                            ObtenerItemsPedido(valor_caja);
                            twv_cliente.setText("CLIENTE: "+RecuperaCte() + "\nTOTAL: "+Calcula_Total(edt_buscar_pedido.getText().toString()));
                            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                            inputMethodManager.hideSoftInputFromWindow(edt_buscar_pedido.getWindowToken(), 0);
                            System.out.println("Valida Pedido");
                        }
                    } catch (Exception Exe) {
                        Exe.printStackTrace();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.busquedamenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_information:

                String p = edt_buscar_pedido.getText().toString();
                System.out.println("VALOR DE CAMPO DE BUSQUEDA " + p);
                if (p.equals("") || p.equals(" ") || p == null || p.equals("")) {
                    Toast Pedido = Toast.makeText(getApplicationContext(), "Debes ingresar el correlativo de el pedido que deseas buscar", Toast.LENGTH_LONG);
                    Pedido.show();
                } else {
                    String compania = RecuperaCia();
                    String cliente = RecuperaCte();


                    String p_total = Calcula_Total(edt_buscar_pedido.getText().toString());
                    System.out.println("valor del total " + p_total);
                    String informacion = "Compañia: " + compania + "\n \n" + "Cliente: " + cliente + "\n \n" + "Total del Pedido: " + p_total;

                    AlertDialog.Builder builder = new AlertDialog.Builder(Buscar_pedidos.this, R.style.MyDialogTheme);
                    builder.setTitle("Informacion de pedido buscado")
                            .setMessage(informacion)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                    builder.create();
                    builder.show();
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String RecuperaCia() {
        String select = ContratoPedidos.Pedidos.NO_PED_MOVIL + "=?";
        String where[] = {edt_buscar_pedido.getText().toString()};
        ContentResolver exec = getContentResolver();
        Cursor c = exec.query(ContratoPedidos.Pedidos.URI_CONTENIDO, null, select, where, null);
        if (c.moveToFirst()) {
            compania = c.getString(1);
            cliente = c.getString(2);
        }

        String selectcia = ContratoPedidos.Ciatab.CIACOD + "=?";
        String wherecia[] = {compania};
        Cursor x = exec.query(ContratoPedidos.Ciatab.URI_CONTENIDO, null, selectcia, wherecia, null);
        if (x.moveToFirst()) {
            cia = x.getString(2);
        }

        return cia;
    }

    public String RecuperaCte() {
        String select = ContratoPedidos.Pedidos.NO_PED_MOVIL + "=?";
        String where[] = {edt_buscar_pedido.getText().toString()};
        ContentResolver exec = getContentResolver();
        Cursor c = exec.query(ContratoPedidos.Pedidos.URI_CONTENIDO, null, select, where, null);
        if (c.moveToFirst()) {

            cliente = c.getString(2);
        }
        String selectcte = ContratoPedidos.Custab.CUSCOD + "=?";
        String wherecte[] = {cliente};
        Cursor y = exec.query(ContratoPedidos.Custab.URI_CONTENIDO, null, selectcte, wherecte, null);
        if (y.moveToFirst()) {
            cte = y.getString(3);
        }
        return cte;
    }

    public void BuscarPedido(String correlativo){
        String descripcion_producto = null;
        items_agregar  = new ArrayList<AdaptadorDePedidos>();
        ContentResolver exec = getContentResolver();
        String seleccion = ContratoPedidos.Pedidos.ID_PEDIDO+"=?";
        String valor[]={correlativo};
        Cursor c = exec.query(ContratoPedidos.Pedidos.URI_CONTENIDO,null,seleccion,valor,null);
        item =  new ArrayList<String>();
        DecimalFormat decimales = new DecimalFormat("0.00");

        if(c.moveToFirst()){
            do{
                String c1 = c.getString(1);
                String c2 = c.getString(2);
                String c3 = c.getString(3);
                String c4 = c.getString(4);
                String c5 = c.getString(5);
                String c6 = c.getString(6);
                String c7 = c.getString(7);
                String c8 = c.getString(8);
                String c9 = c.getString(9);
                String c10 = c.getString(10);
                String c11 = c.getString(11);
                String c12 = c.getString(12);
                String c13 = c.getString(13);
                String c14 = c.getString(14);
                String c15 = c.getString(15);
                String c16 = c.getString(16);
                String c17 = c.getString(17);
                String c18 = c.getString(18);
                String c19 = c.getString(19);
                String c20 = c.getString(20);
                String c21 = c.getString(21);
                String c22 = c.getString(22);

                String selection = ContratoPedidos.Invptmtab.INVPTMCOD+"=?";
                String condicion[] ={c5};
                Cursor descripcion = exec.query(ContratoPedidos.Invptmtab.URI_CONTENIDO,null,selection,condicion,null);


                if(descripcion.moveToNext()){
                    String e1 = descripcion.getString(1);
                    String e2 = descripcion.getString(2);
                    descripcion_producto = descripcion.getString(3);
                    String e4 = descripcion.getString(4);
                    String e5 = descripcion.getString(5);

                    System.out.println("e1 " + e1 + "\n"+
                            "e2 " + e2 + "\n"+
                            "e3 " + descripcion_producto + "\n"+
                            "e4 " + e4 + "\n"+
                            "e5 " + e5 + "\n");
                }
                double v1=0,v2=0,v3=0;

                v1 = Double.parseDouble(c6);
                v2 = Double.parseDouble(c10);
                v3 = (v1*v2);
                String monto = Double.toString(v3);

                item.add("Q. " +decimales.format(v3)+ "\n "+
                        descripcion_producto+" \n"+
                        "Codigo: "+c5+"   Precio: "+ c6+ "  Cantidad: " +c10 );
                items_agregar.add(new AdaptadorDePedidos(1,monto,c5,descripcion_producto,c10,monto,c6));

            }while(c.moveToNext());

        }

        ArrayAdapter<String> adaptador
                = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,item);
        listado.setAdapter(adaptador);



    }

    public void ObtenerItemsPedido(String corr){

        System.out.println("Obtener Items Pedido      mensaje 1");
        items_pedidos = ObtenerItems(corr);
        System.out.println("Obtener Items Pedido      mensaje 2 " + items_pedidos);
        adapter = new item_pedido(this,items_pedidos);
        System.out.println("Obtener Items Pedido      mensaje 3 " + adapter);
        listado.setAdapter(adapter);
    }

    public ArrayList<AdaptadorListView>ObtenerItems(String correlativo_pedido){

        items.clear();
        String seleccion = ContratoPedidos.Pedidos.NO_PED_MOVIL+"=?";
        String variable[]={correlativo_pedido};
        String descripcion_producto = null;
        ContentResolver exec = getContentResolver();
        Cursor c = exec.query(ContratoPedidos.Pedidos.URI_CONTENIDO,null,seleccion,variable,null);
        item =  new ArrayList<String>();
        DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
        simbolo.setDecimalSeparator('.');
        simbolo.setGroupingSeparator(',');
        DecimalFormat decimales = new DecimalFormat("###,###.####",simbolo);
        System.out.println("Cursor x " + c);

        if(c.moveToFirst()){
            do{
                String c1 = c.getString(1);
                String c2 = c.getString(2);
                String c3 = c.getString(3);
                String c4 = c.getString(4);
                String c5 = c.getString(5);
                String c6 = c.getString(6);
                String c7 = c.getString(7);
                String c8 = c.getString(8);
                String c9 = c.getString(9);
                String c10 = c.getString(10);
                String c11 = c.getString(11);
                String c12 = c.getString(12);
                String c13 = c.getString(13);
                String c14 = c.getString(14);
                String c15 = c.getString(15);
                String c16 = c.getString(16);
                String c17 = c.getString(17);
                String c18 = c.getString(18);
                String c19 = c.getString(19);
                String c20 = c.getString(20);
                String c21 = c.getString(21);
                String c22 = c.getString(22);
                String c23 = c.getString(23);
                String c24 = c.getString(24);
                String c25 = c.getString(25);

                String selection = ContratoPedidos.Invptmtab.INVPTMCOD+"=?";
                String condicion[] ={c5};
                Cursor descripcion = exec.query(ContratoPedidos.Invptmtab.URI_CONTENIDO,null,selection,condicion,null);


                if(descripcion.moveToNext()){
                    String e1 = descripcion.getString(1);
                    String e2 = descripcion.getString(2);
                    descripcion_producto = descripcion.getString(3);
                    String e4 = descripcion.getString(4);
                    String e5 = descripcion.getString(5);

                    System.out.println("e1 " + e1 + "\n"+
                            "e2 " + e2 + "\n"+
                            "e3 " + descripcion_producto + "\n"+
                            "e4 " + e4 + "\n"+
                            "e5 " + e5 + "\n"+
                            "c12 " + c12 + "\n");
                }

                v1 = Double.parseDouble(c6);
                v2 = Double.parseDouble(c10);
                v3 = (v1*v2);


                v4 =(v4+v3);

                total = "0.00";

                total = decimales.format(v4);
                System.out.println("TOTAL: " + total);
                System.out.println(" anulado "+ c18 );

                try {
                    System.out.println("descripcion "+ descripcion_producto + " monto "+ decimales.format(v3)+ " bonificaciones "+ c11 + " cantidad "+ c10 + " precio "+c6);

                    items.add(new AdaptadorListView(1,descripcion_producto,decimales.format(v3),c11,c10,c6,c5));



                }catch (Exception e ){
                    e.printStackTrace();
                }


            }while(c.moveToNext());

        }

        SQLiteDatabase db;
        BaseDatosPedidos dba = new BaseDatosPedidos(Buscar_pedidos.this);
        db = dba.getWritableDatabase();

        Cursor s_A = db.rawQuery("SELECT DISTINCT reg_anulado,pendiente_insercion from pedido_movil where no_ped_movil = "+correlativo_pedido,null);
        String anulado = null,sincronizado = null;
        if (s_A.moveToFirst()){
            anulado = s_A.getString(0);
            sincronizado = s_A.getString(1);
            System.out.println(anulado + " bdm");
        }
        if(anulado == null){
            anulado= "no anulado";
        }

        if (anulado.equals("Anulado")){
            cHKbx_anulado.setChecked(true);
            Toast anulado_t = Toast.makeText(getApplicationContext(),"Este pedido ha sido anulado", Toast.LENGTH_LONG);
            anulado_t.show();
        }else{
            cHKbx_anulado.setChecked(false);
        }

        if (sincronizado.equals("1")){
            cHKbx_sincronizado.setChecked(false);
            Toast sincronizado_t = Toast.makeText(getApplicationContext(),"Este pedido no ha sido sincronizado", Toast.LENGTH_LONG);
            sincronizado_t.show();
        }else{
            cHKbx_sincronizado.setChecked(true);
            Toast sincronizado_t = Toast.makeText(getApplicationContext(),"Este pedido ha sido sincronizado", Toast.LENGTH_LONG);
            sincronizado_t.show();
        }


        String slct = ContratoPedidos.Pedidos.NO_PED_MOVIL+"=?";
        String value[]={correlativo_pedido};
        Cursor c_anulado = exec.query(ContratoPedidos.Pedidos.URI_CONTENIDO,null,slct,value,null);

        /*String slcts = ContratoPedidos.Pedidos.NO_PED_MOVIL+"=?";
        String values[]={correlativo_pedido};
        Cursor c_sincronizado = exec.query(ContratoPedidos.Pedidos.URI_CONTENIDO,null,slcts,values,null);

        System.out.println("c_anulado "+ c_anulado.getCount());
        System.out.println("c_sincronizado "+ c_sincronizado.getCount());

        if (c_anulado.getCount()<=0){
            cHKbx_anulado.setChecked(true);
            Toast anulado_t = Toast.makeText(getApplicationContext(),"Este pedido ha sido anulado", Toast.LENGTH_LONG);
            anulado_t.show();
        }else{
            cHKbx_anulado.setChecked(false);
        }

        if (c_sincronizado.getCount()<=0){
            cHKbx_sincronizado.setChecked(false);
            Toast sincronizado_t = Toast.makeText(getApplicationContext(),"Este pedido no ha sido sincronizado", Toast.LENGTH_LONG);
            sincronizado_t.show();
        }else{
            cHKbx_sincronizado.setChecked(true);
            Toast sincronizado_t = Toast.makeText(getApplicationContext(),"Este pedido ha sido sincronizado", Toast.LENGTH_LONG);
            sincronizado_t.show();
        }*/


        if(total==null){
            total = "0.00";
        }
        System.out.println("ITEMS "+ items);

        return items;

    }
// alt + 123 para sacar  -> {
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public String ultimoCorrelativo(){
        ContentResolver exec = getContentResolver();
        //         Cursor c = exec.query(ContratoPedidos.Pedidos.URI_CONTENIDO,null,seleccion,variable,null);
        Cursor c_correlativo = exec.query(ContratoPedidos.Talonarios.URI_CONTENIDO,null,null,null,null,null);
        String ultimoCorrelativo = "";
        if (c_correlativo.moveToNext()){
            ultimoCorrelativo = c_correlativo.getString(4);
        }
        return ultimoCorrelativo;
    }

    public String Calcula_Total(String correlativo){
        String total = "";
        DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
        simbolo.setDecimalSeparator('.');
        simbolo.setGroupingSeparator(',');
        final DecimalFormat decimales = new DecimalFormat("###,###.###",simbolo);

        SQLiteDatabase db;
        BaseDatosPedidos dba = new BaseDatosPedidos(Buscar_pedidos.this);

        db = dba.getWritableDatabase();
        double to_decimal = 0.00;
        Cursor precio_pt = db.rawQuery("SELECT SUM(precio_pt*unidades_vta) from pedido_movil where no_ped_movil= "+ correlativo,null);
        if (precio_pt.moveToFirst()){
            String v1 = precio_pt.getString(0);
            if(v1 == null){
                v1 = "0.00";
            }

            System.out.println("VALOR DEL TOTAL / PRECIO "+v1 );
            to_decimal = Double.parseDouble(v1);
             total = String.valueOf(decimales.format(to_decimal));
            if(total.equals("0")){
                total = "0.00";
            }
            System.out.println("VALOR DEL TOTAL / TOTAL "+total );
        }
        return total;
    }

    public boolean existePedido(String pedido){
        boolean validador = false;
        ContentResolver r = getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        System.out.println("No pedidod " +pedido);
        String columnas =ContratoPedidos.Pedidos.NO_PED_MOVIL  + "=?";
        String[]where= {pedido};

        Cursor c = r.query(ContratoPedidos.Pedidos.URI_CONTENIDO,null,columnas,where,null);

        if(c.getCount()==0){
            validador = false;
        }else{
            validador = true;
        }
            return validador;
    }

}
