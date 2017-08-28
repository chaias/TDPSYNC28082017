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
import com.mosi.tdpsync.ui.AdaptadorPrecios;
import com.mosi.tdpsync.ui.Buscar_pedidos;

import java.util.ArrayList;

/**
 * Created by Isaias on 26/08/2017.
 */

public class item_precios extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<AdaptadorPrecios> items;
    String font_path = "fonts/LucidaGrande.ttf";
    Typeface TF;


    public item_precios(Activity activity, ArrayList<AdaptadorPrecios>items){

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
        return items.get(i).get_id();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View vi = view;

        if (view == null){
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.item_precios,null);
        }
        Typeface TF;
        TF = Typeface.createFromAsset(this.activity.getAssets(),font_path);

        AdaptadorPrecios item = items.get(i);

        TextView Tvw_Descripcion = (TextView)vi.findViewById(R.id.twv_descripcionproducto);
        Tvw_Descripcion.setTypeface(TF);
        Tvw_Descripcion.setText(item.getDescripcion());

        TextView Tvw_Monto = (TextView)vi.findViewById(R.id.twv_unidades);
        Tvw_Monto.setTypeface(TF);
        Tvw_Monto.setText("Unidades: "+item.getUnidades());


        return vi;
    }
}
