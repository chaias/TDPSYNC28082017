package com.mosi.tdpsync.item;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mosi.tdpsync.R;
import com.mosi.tdpsync.ui.AdaptadorListView;

import java.util.ArrayList;

/**
 * Created by Isaias on 04/12/2016.
 */
public class item_pedido extends BaseAdapter{

    protected Activity activity;
    protected ArrayList<AdaptadorListView> items;
    String font_path = "fonts/LucidaGrande.ttf";
    Typeface TF;


    public item_pedido(Activity activity, ArrayList<AdaptadorListView>items){

        this.activity = activity;
        this.items = items;
        try {
            System.out.println("SLEEPING");
            Thread.sleep(1600);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return items.get(i).getIdIdentificador();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View vi = view;

        if (view == null){
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.item_pedido,null);
        }

        AdaptadorListView item = items.get(i);
        TF = Typeface.createFromAsset(this.activity.getAssets(),font_path);

        System.out.println("DESCRIPCION "+ item.getDescripcion()+"\n"+
        "MONTO "+ item.getMonto()+"\n"+
        "BONIFICACION "+ item.getBonificacion()+"\n"+
        "CANTIDAD "+ item.getCantidadvendida()+"\n"+
        "PRECIO "+ item.getPreciounidad());

        //TF = Typeface.createFromAsset(getAssets(),font_path);


        TextView Tvw_Descripcion = (TextView)vi.findViewById(R.id.twv_descripcion);
        Tvw_Descripcion.setTypeface(TF);
        Tvw_Descripcion.setText(item.getDescripcion());

        TextView Tvw_Monto = (TextView)vi.findViewById(R.id.twv_cantidad_x_precio);
        Tvw_Monto.setTypeface(TF);
        Tvw_Monto.setText("Q. "+item.getMonto());

        TextView Tvw_bonificacion = (TextView)vi.findViewById(R.id.twv_bonificacion);
        Tvw_bonificacion.setTypeface(TF);
        Tvw_bonificacion.setText("Bonificacion:"+item.getBonificacion());

        TextView Tvw_Cantidad = (TextView) vi.findViewById(R.id.twv_cantidad);
        Tvw_Cantidad.setTypeface(TF);
        Tvw_Cantidad.setText("Cantidad:"+item.getCantidadvendida());

        TextView Tvw_Precio = (TextView)vi.findViewById(R.id.twv_precio);
        Tvw_Precio.setTypeface(TF);
        Tvw_Precio.setText("Precio:"+item.getPreciounidad());

        return vi;
    }
}
