package com.mosi.tdpsync.ui;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.mosi.tdpsync.R;
import com.mosi.tdpsync.sqlite.ContratoPedidos;

public class Toma_Pedido extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toma__pedido);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }/*ContentResolver rexec = getContentResolver();

                                            String dialog_cantidad = edt_dialog_cantidad.getText().toString();
                                            //String dialog_bonificacion = edt_dialog_bonificacion.getText().toString();
                                            int dialog_cnt = Integer.parseInt(dialog_cantidad);
                                            //int dialog_boni = Integer.parseInt(dialog_bonificacion);
                                            int unid_vta = Integer.parseInt(unidades_vta);
                                            int dif = 0;

                                            String invptddispi = ContratoPedidos.Invptdtab.INVPTDCOD + "=?";
                                            String[] where_producto ={codigo_producto};
                                            Cursor c_disponible = rexec.query(ContratoPedidos.Invptdtab.URI_CONTENIDO,null,invptddispi,where_producto,null);
                                            if (c_disponible.moveToNext()){
                                                v0c = c_disponible.getString(0);
                                                v3c = c_disponible.getString(3);

                                                System.out.println( " v3c "+v3c);
                                            }

                                            int disponible = Integer.parseInt(v3c);

                                            if (dialog_cnt > unid_vta){
                                                dif = dialog_cnt - unid_vta;
                                                if (dif >= disponible){
                                                    ContentValues newValue1 = new ContentValues();
                                                    newValue1.put(ContratoPedidos.PedidosColumnas.UNIDADES_VTA,edt_dialog_cantidad.getText().toString());

                                                    String cuando = ContratoPedidos.PedidosColumnas._ID + " = " + v0c + " AND "
                                                            + ContratoPedidos.PedidosColumnas.PRODUCTO + " = " + codigo_producto + " AND "
                                                            + ContratoPedidos.PedidosColumnas.ID_PEDIDO + " = " + correlativo_gnr;

                                                    int response = getContentResolver().update(ContratoPedidos.CONTENT_URI_PED,newValue1,cuando,null);
                                                    if (response == 1){
                                                        try {
                                                            ObtenerItemsPedido(identificador_movil+"-"+correlativo_gnr);
                                                        }catch (Exception e){
                                                            e.printStackTrace();
                                                        }
                                                        Calcula_Total();
                                                        Toast exito = Toast.makeText(getApplicationContext(), "El producto ha sido actualizado", Toast.LENGTH_LONG);
                                                        exito.show();
                                                    }else{
                                                        try {
                                                            ObtenerItemsPedido(identificador_movil+"-"+correlativo_gnr);
                                                        }catch (Exception e){
                                                            e.printStackTrace();
                                                        }
                                                        Calcula_Total();
                                                        Toast fallo = Toast.makeText(getApplicationContext(), "Ocurrio un problema con tu solicitud  \n Intentalo mas tarde...", Toast.LENGTH_LONG);
                                                        fallo.show();

                                                    }

                                                }else{
                                                    Toast disponible_inv = Toast.makeText(getApplicationContext(), "No cuentas con unidades disponibles para acualizar el producto", Toast.LENGTH_LONG);
                                                    disponible_inv.show();
                                                    try {
                                                        ObtenerItemsPedido(identificador_movil+"-"+correlativo_gnr);
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }else{
                                                dif = unid_vta - dialog_cnt;
                                                disponible = disponible + dif;

                                                ContentValues newValue = new ContentValues();
                                                newValue.put(ContratoPedidos.InvptdtabColumnas.INVPTDDISPI,disponible);

                                                String cuando = ContratoPedidos.InvptdtabColumnas.INVPTDCOD + " = " + codigo_producto;

                                                int response = getContentResolver().update(ContratoPedidos.CONTENT_URI_INVPTD,newValue,cuando,null);
                                                Toast consulta = Toast.makeText(getApplicationContext(), "actualizado " + response, Toast.LENGTH_LONG);
                                                consulta.show();
                                                if (response == 1){
                                                    String nueva_cantidad = edt_dialog_cantidad.getText().toString();
                                                    ContentValues nuevo = new ContentValues();
                                                    newValue.put(ContratoPedidos.PedidosColumnas.UNIDADES_VTA,nueva_cantidad);

                                                    System.out.println(" V0C "+ v0c + " codigo producto "+ codigo_producto + " correlativo "+ correlativo_gnr +  "nueva_cantidad "+nueva_cantidad);

                                                    String where = ContratoPedidos.PedidosColumnas._ID + " = " + v0c + " AND "
                                                            + ContratoPedidos.PedidosColumnas.PRODUCTO + " = " + codigo_producto + " AND "
                                                            + ContratoPedidos.PedidosColumnas.ID_PEDIDO + " = " + correlativo_gnr;

                                                    int response_ped = getContentResolver().update(ContratoPedidos.CONTENT_URI_PED,nuevo,where,null);
                                                    Toast ext = Toast.makeText(getApplicationContext(), "response_ped "+response_ped, Toast.LENGTH_LONG);
                                                    ext.show();
                                                        if (response_ped == 1){
                                                            try {
                                                                ObtenerItemsPedido(identificador_movil+"-"+correlativo_gnr);
                                                            }catch (Exception e){
                                                                e.printStackTrace();
                                                            }
                                                            Calcula_Total();
                                                            Toast exito = Toast.makeText(getApplicationContext(), "El producto ha sido actualizado", Toast.LENGTH_LONG);
                                                            exito.show();
                                                        }else{
                                                            try {
                                                                ObtenerItemsPedido(identificador_movil+"-"+correlativo_gnr);
                                                            }catch (Exception e){
                                                                e.printStackTrace();
                                                            }
                                                            Calcula_Total();
                                                            Toast fallo = Toast.makeText(getApplicationContext(), "Ocurrio un problema con tu solicitud  \n Intentalo mas tarde...", Toast.LENGTH_LONG);
                                                            fallo.show();
                                                        }

                                                }else{
                                                    try {
                                                        ObtenerItemsPedido(identificador_movil+"-"+correlativo_gnr);
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                    Calcula_Total();
                                                    Toast fallo = Toast.makeText(getApplicationContext(), "Ocurrio un problema con tu solicitud  \n Intentalo mas tarde...", Toast.LENGTH_LONG);
                                                    fallo.show();
                                                }

                                            }*/










    ////////////////////NUEVO------------------------


    /*if (edt_dialog_cantidad_int > unidades_vta_int){
        diferencia =  edt_dialog_cantidad_int - unidades_vta_int;
        if (diferencia >= disponible){
            disponible = disponible - diferencia;

            ContentValues nuevo = new ContentValues();
            nuevo.put(ContratoPedidos.Invptdtab.INVPTDDISPI,disponible);

            String cuando = ContratoPedidos.Invptdtab.INVPTDCOD + " = " + codigo_producto + " AND "
                    + ContratoPedidos.Invptdtab.INVPTDCIA + " = " + invptdcia;

            int response_invptdtab = getContentResolver().update(ContratoPedidos.CONTENT_URI_INVPTD,nuevo,cuando,null);
            if (response_invptdtab == 1){
                ContentValues nuevo_ped = new ContentValues();
                nuevo.put(ContratoPedidos.Pedidos.UNIDADES_VTA,edt_dialog_cantidad_str);

                String cuando_ped = ContratoPedidos.PedidosColumnas.PRODUCTO + " = " + codigo_producto + " AND "
                        + ContratoPedidos.PedidosColumnas.NO_PED_MOVIL + " = " + correlativo_gnr;

                int response_pedidos = getContentResolver().update(ContratoPedidos.CONTENT_URI_PED,nuevo_ped,cuando_ped,null);
                if (response_pedidos == 1){
                    //exito
                    try {
                        ObtenerItemsPedido(identificador_movil+"-"+correlativo_gnr);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Calcula_Total();
                    Toast exito = Toast.makeText(getApplicationContext(), "El producto ha sido actualizado", Toast.LENGTH_LONG);
                    exito.show();
                }else{
                    //fallo
                    try {
                        ObtenerItemsPedido(identificador_movil+"-"+correlativo_gnr);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Calcula_Total();
                    Toast fallo = Toast.makeText(getApplicationContext(), "Ocurrio un problema con tu solicitud\\nIntentalo mas tarde...", Toast.LENGTH_LONG);
                    fallo.show();
                }

            }else{
                //fallo
                try {
                    ObtenerItemsPedido(identificador_movil+"-"+correlativo_gnr);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Calcula_Total();
                Toast fallo = Toast.makeText(getApplicationContext(), "Ocurrio un problema con tu solicitud\\nIntentalo mas tarde...", Toast.LENGTH_LONG);
                fallo.show();
            }
        }else{
            Toast diponible = Toast.makeText(getApplicationContext(), "No cuentas con disponble para actualizar el producto seleccionado", Toast.LENGTH_LONG);
            diponible.show();
        }
    }else{
        diferencia = unidades_vta_int - edt_dialog_cantidad_int;
        disponible = disponible + diferencia;

        ContentValues nuevo = new ContentValues();
        nuevo.put(ContratoPedidos.Invptdtab.INVPTDDISPI,disponible);

        String cuando = ContratoPedidos.Invptdtab.INVPTDCOD + " = " + codigo_producto + " AND "
                + ContratoPedidos.Invptdtab.INVPTDCIA + " = " + invptdcia;

        int response_invptdtab = getContentResolver().update(ContratoPedidos.CONTENT_URI_INVPTD,nuevo,cuando,null);
        if (response_invptdtab == 1){
            ContentValues nuevo_ped = new ContentValues();
            nuevo.put(ContratoPedidos.Pedidos.UNIDADES_VTA,edt_dialog_cantidad_str);

            String cuando_ped = ContratoPedidos.PedidosColumnas.PRODUCTO + " = " + codigo_producto + " AND "
                    + ContratoPedidos.PedidosColumnas.NO_PED_MOVIL + " = " + correlativo_gnr;

            int response_pedidos = getContentResolver().update(ContratoPedidos.CONTENT_URI_PED,nuevo_ped,cuando_ped,null);
            if (response_pedidos == 1){
                //exito
                try {
                    ObtenerItemsPedido(identificador_movil+"-"+correlativo_gnr);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Calcula_Total();
                Toast exito = Toast.makeText(getApplicationContext(), "El producto ha sido actualizado", Toast.LENGTH_LONG);
                exito.show();
            }else{
                //fallo
                try {
                    ObtenerItemsPedido(identificador_movil+"-"+correlativo_gnr);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Calcula_Total();
                Toast fallo = Toast.makeText(getApplicationContext(), "Ocurrio un problema con tu solicitud\\nIntentalo mas tarde...", Toast.LENGTH_LONG);
                fallo.show();
            }
        }else{
            Toast fallo = Toast.makeText(getApplicationContext(), "Ocurrio un problema con tu solicitud\\nIntentalo mas tarde...", Toast.LENGTH_LONG);
            fallo.show();
        }
    }*/

}
