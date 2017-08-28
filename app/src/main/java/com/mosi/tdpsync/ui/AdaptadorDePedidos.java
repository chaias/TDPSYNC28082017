package com.mosi.tdpsync.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Mosi on 28/04/2016.
 */
public class AdaptadorDePedidos extends RecyclerView.Adapter<AdaptadorDePedidos.ViewHolder> {

    private Cursor cursor;
    private Context context;

    String monto;
    String descripcion;
    String cantidad;
    String cantidadxprecio;
    String precio;
    String codigoproducto;
    String rutaimagen;
    long id;

    public AdaptadorDePedidos(){
        monto="";
        descripcion="";
        cantidad="";
        cantidadxprecio="";
        precio="";
        codigoproducto="";
        rutaimagen="";


    }

    public AdaptadorDePedidos(long id,String monto,String codigoproducto,String descripcion,String cantidad,String cantidadxprecio,String precio){
        this.id = id;
        this.monto = monto;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.cantidadxprecio = cantidadxprecio;
        this.precio = precio;
        this.codigoproducto = codigoproducto;

    }
    public AdaptadorDePedidos(long id, String monto,String codigoproducto,String descripcion,String cantidad,String rutaimagen,String cantidadxprecio,String precio) {
        this.id = id;
        this.monto = monto;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.rutaimagen = rutaimagen;
        this.cantidadxprecio = cantidadxprecio;
        this.precio = precio;
        this.codigoproducto = codigoproducto;
        System.out.println("Datos Recibidos en el recogedor de datos...... de la clase adaptador de pedidos");
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        cursor.moveToPosition(position);

        String usuario;
        String password;

        usuario = cursor.getString(1);
        password = cursor.getString(2);

    }

    @Override
    public int getItemCount() {
        if (cursor!=null)
        return cursor.getCount();
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView usuario;
        public TextView password;

        public ViewHolder(View view){
            super(view);
        }

    }

    public AdaptadorDePedidos(Context context){

        this.context = context;

    }

    public void swapCursor(Cursor newcursor){

        cursor = newcursor;
        notifyDataSetChanged();

    }

    public Cursor getCursor(){

        return cursor;

    }

}
