package com.mosi.tdpsync.ui;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;

import com.mosi.tdpsync.R;
import com.mosi.tdpsync.item.item_pedido;
import com.mosi.tdpsync.item.item_precios;
import com.mosi.tdpsync.sqlite.ContratoPedidos;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class ConsultaPrecio extends AppCompatActivity {

    String usuariologueado;
    ArrayList<AdaptadorPrecios>items_precios;
    item_precios adapter;
    ListView listado;
    List<String> item = null;
    String font_path = "fonts/LucidaGrande.ttf";
    Typeface TF;
    EditText edt_filtro_productos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_precio);

        usuariologueado = getIntent().getStringExtra("usuario");
        listado = (ListView)findViewById(R.id.listado_productos);
        edt_filtro_productos = (EditText)findViewById(R.id.edt_filtro_productos);

        TF = Typeface.createFromAsset(getAssets(), font_path);
        edt_filtro_productos.setTypeface(TF);

        ObtenerItemsPrecios();

    }

    public void ObtenerItemsPrecios(){

        items_precios = ObtenerItems();
        adapter = new item_precios(this,items_precios);
        listado.setAdapter(adapter);
    }

    public ArrayList<AdaptadorPrecios> ObtenerItems() {
        final ArrayList<AdaptadorPrecios> items = new ArrayList<>();

        String descripcion_producto = null;
        ContentResolver exec = getContentResolver();
        Cursor c_productos = exec.query(ContratoPedidos.Pr1tab.URI_CONTENIDO, null, null, null, null);
        item = new ArrayList<String>();
        DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
        simbolo.setDecimalSeparator('.');
        simbolo.setGroupingSeparator(',');
        DecimalFormat decimales = new DecimalFormat("###,###.####", simbolo);

        if (c_productos.moveToFirst()) {
            do {
                String pr1cia = c_productos.getString(1);
                String pr1cod = c_productos.getString(2);
                String pr1cat = c_productos.getString(3);
                String pr1fec = c_productos.getString(4);
                String pr1prec = c_productos.getString(5);
                String pr1desc = c_productos.getString(6);

                String selection = ContratoPedidos.Invptmtab.INVPTMCOD + "=?";
                String condicion[] = {pr1cod};
                Cursor descripcion = exec.query(ContratoPedidos.Invptmtab.URI_CONTENIDO, null, selection, condicion, null);


                if (descripcion.moveToNext()) {
                    String e1 = descripcion.getString(1);
                    String e2 = descripcion.getString(2);
                    descripcion_producto = descripcion.getString(3);
                    String e4 = descripcion.getString(4);
                    String e5 = descripcion.getString(5);

                    System.out.println("e1 " + e1 + "\n" +
                            "e2 " + e2 + "\n" +
                            "e3 " + descripcion_producto + "\n" +
                            "e4 " + e4 + "\n" +
                            "e5 " + e5 + "\n");
                }

                String select = ContratoPedidos.Invptdtab.INVPTDCOD + "=?";
                String cond[] = {pr1cod};
                Cursor c_invptdtab = exec.query(ContratoPedidos.Invptdtab.URI_CONTENIDO, null, select, condicion, null);

                if (c_invptdtab.moveToNext()){

                    String invptdcia = c_invptdtab.getString(1);
                    String invptdcod = c_invptdtab.getString(2);
                    String invptddispi = c_invptdtab.getString(3);

                    try {
                        items.add(new AdaptadorPrecios(1,descripcion_producto,pr1cod,invptddispi,"@mipmap"));
                    }catch (Exception e ){
                        e.printStackTrace();
                    }

                }

                }while (c_productos.moveToNext()) ;
        }


       return  items;
    }
}