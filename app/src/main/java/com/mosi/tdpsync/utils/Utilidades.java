package com.mosi.tdpsync.utils;

import android.database.Cursor;
import android.os.Build;
import android.util.Log;

import com.mosi.tdpsync.sqlite.ContratoPedidos;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mosi on 27/11/2016.
 */
public class Utilidades {
    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_ID = 0;
    public static final int COLUMNA_ID_REMOTA = 1;
    public static final int COLUMNA_MONTO = 2;
    public static final int COLUMNA_ETIQUETA = 3;
    public static final int COLUMNA_FECHA = 4;
    public static final int COLUMNA_DESCRIPCION = 5;

    /**
     * Determina si la aplicación corre en versiones superiores o iguales
     * a Android LOLLIPOP
     *
     * @return booleano de confirmación
     */
    public static boolean materialDesign() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * Copia los datos de un gasto almacenados en un cursor hacia un
     * JSONObject
     *
     * @param c cursor
     * @return objeto jason
     */
    public static JSONObject deCursorAJSONObject(Cursor c) {
        JSONObject jObject = new JSONObject();
        String monto;
        String etiqueta;
        String fecha;
        String descripcion;

        monto = c.getString(COLUMNA_MONTO);
        etiqueta = c.getString(COLUMNA_ETIQUETA);
        fecha = c.getString(COLUMNA_FECHA);
        descripcion = c.getString(COLUMNA_DESCRIPCION);
        System.out.println("deCursorAJSONObject ");

        try {
            jObject.put(ContratoPedidos.Columnas.MONTO, monto);
            jObject.put(ContratoPedidos.Columnas.ETIQUETA, etiqueta);
            jObject.put(ContratoPedidos.Columnas.FECHA, fecha);
            jObject.put(ContratoPedidos.Columnas.DESCRIPCION, descripcion);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("Cursor a JSONObject", String.valueOf(jObject));

        return jObject;
    }

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE USUARIOS\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final int COLUMNA_USUARIO = 2;
    public static final int COLUMNA_PASSWORD = 3;

    public static JSONObject deCursorAJSONObjectUsuarios(Cursor c){
        JSONObject jObjects = new JSONObject();
        String usuario;
        String password;


        usuario = c.getString(COLUMNA_USUARIO);
        password = c.getString(COLUMNA_PASSWORD);
        System.out.println("deCursorAJSONObjectUsuarios "+ "usuario "+ usuario+ " password "+ password);

        try {
            jObjects.put(ContratoPedidos.UsuarioColumnas.USUARIO,usuario);
            jObjects.put(ContratoPedidos.UsuarioColumnas.PASSWORD,password);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.i("Cursor a JSONObject USR", String.valueOf(jObjects));
        return  jObjects;
    }

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE CIATAB\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final int COLUMNA_CIACOD = 2;
    public static final int COLUMNA_CIANOMBRE = 3;

    public static JSONObject deCursorAJSONObjectCiatab(Cursor c){
        JSONObject jObjects = new JSONObject();
        String ciacod;
        String cianombre;


        ciacod = c.getString(COLUMNA_CIACOD);
        cianombre = c.getString(COLUMNA_CIANOMBRE);

        try {
            jObjects.put(ContratoPedidos.CiatabColumnas.CIACOD,ciacod);
            jObjects.put(ContratoPedidos.CiatabColumnas.CIANOMBRE,cianombre);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.i("Cursor a JSONObject CIA", String.valueOf(jObjects));
        return  jObjects;
    }
    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE INVPTDTAB\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public static final int COLUMNA_INVPTDCIA = 2;
    public static final int COLUMNA_INVPTDCOD = 3;
    public static final int COLUMNA_INVPTDDISPI = 4;

    public static JSONObject deCursorAJSONObjectInvptdtab(Cursor c){
        JSONObject jObjects = new JSONObject();
        String invptdcia;
        String invptdcod;
        String invptddispi;


        invptdcia = c.getString(COLUMNA_INVPTDCIA);
        invptdcod = c.getString(COLUMNA_INVPTDCOD);
        invptddispi = c.getString(COLUMNA_INVPTDDISPI);

        try {
            jObjects.put(ContratoPedidos.InvptdtabColumnas.INVPTDCIA,invptdcia);
            jObjects.put(ContratoPedidos.InvptdtabColumnas.INVPTDCOD,invptdcod);
            jObjects.put(ContratoPedidos.InvptdtabColumnas.INVPTDDISPI,invptddispi);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.i("Cursor a invptdtab", String.valueOf(jObjects));
        return  jObjects;
    }

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE INVPTDTAB\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final int COLUMNA_CUSCIA = 2;
    public static final int COLUMNA_CUSCOD = 3;
    public static final int COLUMNA_CUSNAME= 4;
    public static final int COLUMNA_CUSDIR = 5;
    public static final int COLUMNA_CUSTEL = 6;
    public static final int COLUMNA_CUSNIT = 7;
    public static final int COLUMNA_CUSLIMCRE = 8;
    public static final int COLUMNA_CUSDIAVEN = 9;
    public static final int COLUMNA_CUSMAIL = 10;
    public static final int COLUMNA_CUSLATITUD = 11;
    public static final int COLUMNA_CUSLONGITUD = 12;
    public static final int COLUMNA_CUSCAT = 13;
    public static final int COLUMNA_CUSRUTA = 14;
    public static final int COLUMNA_CUSIVA = 15;
    public static final int COLUMNA_CUSBONI = 16;

    public static JSONObject deCursorAJSONObjectCustab(Cursor c){
        JSONObject jObjects = new JSONObject();
        String cuscia;
        String cuscod;
        String cusname;
        String cusdir;
        String custel;
        String cusnit;
        String cuslimcre;
        String cusdiaven;
        String cusmail;
        String cuslatitud;
        String cuslongitud;
        String cuscat;
        String cusruta;
        String cusiva;
        String cusboni;



        cuscia = c.getString(COLUMNA_CUSCIA);
        cuscod = c.getString(COLUMNA_CUSCOD);
        cusname = c.getString(COLUMNA_CUSNAME);
        cusdir = c.getString(COLUMNA_CUSDIR);
        custel = c.getString(COLUMNA_CUSTEL);
        cusnit = c.getString(COLUMNA_CUSNIT);
        cuslimcre = c.getString(COLUMNA_CUSLIMCRE);
        cusdiaven = c.getString(COLUMNA_CUSDIAVEN);
        cusmail = c.getString(COLUMNA_CUSMAIL);
        cuslatitud = c.getString(COLUMNA_CUSLATITUD);
        cuslongitud = c.getString(COLUMNA_CUSLONGITUD);
        cuscat = c.getString(COLUMNA_CUSCAT);
        cusruta = c.getString(COLUMNA_CUSRUTA);
        cusiva = c.getString(COLUMNA_CUSIVA);
        cusboni = c.getString(COLUMNA_CUSBONI);

        try {
            jObjects.put(ContratoPedidos.CustabColumnas.CUSCIA,cuscia);
            jObjects.put(ContratoPedidos.CustabColumnas.CUSCOD,cuscod);
            jObjects.put(ContratoPedidos.CustabColumnas.CUSNAME,cusname);
            jObjects.put(ContratoPedidos.CustabColumnas.CUSDIR,cusdir);
            jObjects.put(ContratoPedidos.CustabColumnas.CUSTEL,custel);
            jObjects.put(ContratoPedidos.CustabColumnas.CUSNIT,cusnit);
            jObjects.put(ContratoPedidos.CustabColumnas.CUSLIMCRE,cuslimcre);
            jObjects.put(ContratoPedidos.CustabColumnas.CUSDIAVEN,cusdiaven);
            jObjects.put(ContratoPedidos.CustabColumnas.CUSMAIL,cusmail);
            jObjects.put(ContratoPedidos.CustabColumnas.CUSLATITUD,cuslatitud);
            jObjects.put(ContratoPedidos.CustabColumnas.CUSLONGITUD,cuslongitud);
            jObjects.put(ContratoPedidos.CustabColumnas.CUSCAT,cuscat);
            jObjects.put(ContratoPedidos.CustabColumnas.CUSRUTA,cusruta);
            jObjects.put(ContratoPedidos.CustabColumnas.CUSIVA,cusiva);
            jObjects.put(ContratoPedidos.CustabColumnas.CUSBONI,cusboni);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.i("Cursor a custab", String.valueOf(jObjects));
        return  jObjects;
    }

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE TALONARIOS\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final int COLUMNA_TALCOD = 2;
    public static final int COLUMNA_TALCIA = 3;
    public static final int COLUMNA_TALDES = 4;
    public static final int COLUMNA_TALCOR = 4;

    public static JSONObject deCursorAJSONObjectTalonarios(Cursor c){
        JSONObject jObjects = new JSONObject();
        String talcod;
        String talcia;
        String taldes;
        String talcor;


        talcod = c.getString(COLUMNA_TALCOD);
        talcia = c.getString(COLUMNA_TALCIA);
        taldes = c.getString(COLUMNA_TALDES);
        talcor = c.getString(COLUMNA_TALCOR);

        try {
            jObjects.put(ContratoPedidos.TalonariosColumnas.TALCOD,talcod);
            jObjects.put(ContratoPedidos.TalonariosColumnas.TALCIA,talcia);
            jObjects.put(ContratoPedidos.TalonariosColumnas.TALDES,taldes);
            jObjects.put(ContratoPedidos.TalonariosColumnas.TALCOR,talcor);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.i("Cursor a TALONARIOS", String.valueOf(jObjects));
        return  jObjects;
    }

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE INVPTMTAB\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public static final int COLUMNA_INVPTMCIA = 2;
    public static final int COLUMNA_INVPTMCOD = 3;
    public static final int COLUMNA_INVPTMDESC = 4;
    public static final int COLUMNA_INVPTMMED = 5;
    public static final int COLUMNA_INVPTMEMP = 6;
    public static final int COLUMNA_INVPTMIVA = 7;

    public static JSONObject deCursorAJSONObjectInvptmtab(Cursor c){
        JSONObject jObjects = new JSONObject();
          String invptmcia;
          String invptmcod;
          String invptmdesc;
          String invptmmed;
          String invptmemp;
          String invptmiva;


        invptmcia = c.getString(COLUMNA_INVPTMCIA);
        invptmcod = c.getString(COLUMNA_INVPTMCOD);
        invptmdesc = c.getString(COLUMNA_INVPTMDESC);
        invptmmed = c.getString(COLUMNA_INVPTMMED);
        invptmemp = c.getString(COLUMNA_INVPTMEMP);
        invptmiva = c.getString(COLUMNA_INVPTMIVA);

        try {
            jObjects.put(ContratoPedidos.InvptmtabColumnas.INVPTMCIA,invptmcia);
            jObjects.put(ContratoPedidos.InvptmtabColumnas.INVPTMCOD,invptmcod);
            jObjects.put(ContratoPedidos.InvptmtabColumnas.INVPTMDESC,invptmdesc);
            jObjects.put(ContratoPedidos.InvptmtabColumnas.INVPTMMED,invptmmed);
            jObjects.put(ContratoPedidos.InvptmtabColumnas.INVPTMEMP,invptmemp);
            jObjects.put(ContratoPedidos.InvptmtabColumnas.INVPTMIVA,invptmiva);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.i("Cursor a invptmtab", String.valueOf(jObjects));
        return  jObjects;
    }

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE PR1TAB\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public static final int COLUMNA_PR1CIA = 2;
    public static final int COLUMNA_PR1COD = 3;
    public static final int COLUMNA_PR1CAT = 4;
    public static final int COLUMNA_PR1FEC = 5;
    public static final int COLUMNA_PR1DIQ = 6;

    public static JSONObject deCursorAJSONObjectPr1tab(Cursor c){
        JSONObject jObjects = new JSONObject();
          String pr1cia;
          String pr1cod;
          String pr1cat;
          String pr1fec;
          String pr1diq;


        pr1cia = c.getString(COLUMNA_PR1CIA);
        pr1cod = c.getString(COLUMNA_PR1COD);
        pr1cat = c.getString(COLUMNA_PR1CAT);
        pr1fec = c.getString(COLUMNA_PR1FEC);
        pr1diq = c.getString(COLUMNA_PR1DIQ);

        try {
            jObjects.put(ContratoPedidos.Pr1tabColumnas.PR1CIA,pr1cia);
            jObjects.put(ContratoPedidos.Pr1tabColumnas.PR1COD,pr1cod);
            jObjects.put(ContratoPedidos.Pr1tabColumnas.PR1CAT,pr1cat);
            jObjects.put(ContratoPedidos.Pr1tabColumnas.PR1FEC,pr1fec);
            jObjects.put(ContratoPedidos.Pr1tabColumnas.PR1PREC,pr1diq);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.i("Cursor a PR1TAB", String.valueOf(jObjects));
        return  jObjects;
    }

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE TIPTAB\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public static final int COLUMNA_TIPCIA = 2;
    public static final int COLUMNA_TIPCOD = 3;
    public static final int COLUMNA_TIPDES = 4;

    public static JSONObject deCursorAJSONObjectTiptab(Cursor c){
        JSONObject jObjects = new JSONObject();
        String tipcia;
        String tipcod;
        String tipdes;

        tipcia = c.getString(COLUMNA_TIPCIA);
        tipcod = c.getString(COLUMNA_TIPCOD);
        tipdes = c.getString(COLUMNA_TIPDES);

        try {
            jObjects.put(ContratoPedidos.TiptabColumnas.DOCCIA,tipcia);
            jObjects.put(ContratoPedidos.TiptabColumnas.DOCCOD,tipcod);
            jObjects.put(ContratoPedidos.TiptabColumnas.DOCNAME,tipdes);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.i("Cursor a TIPTAB", String.valueOf(jObjects));
        return  jObjects;
    }

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE PEDIDOS\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public static final int COMPANIA= 2;
    public static final int CLIENTE= 3;
    public static final int RUTA = 4;
    public static final int LUGAR_ENTREGA = 5;
    public static final int PRODUCTO = 6;
    public static final int PRECIO_PT = 7;
    public static final int TIPO_PEDIDO = 8;
    public static final int NO_PED_MOVIL = 9;
    public static final int FECHA = 10;
    public static final int UNIDADES_VTA = 11;
    public static final int UNIDADES_BONI = 12;
    public static final int LATITUD = 13;
    public static final int LONGITUD = 14;
    public static final int ESTATUS = 15;
    public static final int ESTATUS_REG = 16;
    public static final int QTY_ORIGINAL = 17;
    public static final int ID_PEDIDO = 18;
    public static final int REG_ANULADO = 19;
    public static final int ESTATUS_FINAL = 20;
    public static final int NO_PEDIDO_CMF = 21;
    public static final int USUARIO = 22;
    public static final int FECHA_SISTEMA = 23;

    public static JSONObject deCursorAJSONObjectPedidos(Cursor c){
        System.out.println("deCursorAJSONObjectPedidos " +c);

        JSONObject jObject = new JSONObject();

        String compania;
        String cliente;
        String ruta;
        String lugar_entrega;
        String producto;
        String precio_pt;
        String tipo_pedido;
        String no_ped_movil;
        String fecha;
        String unidades_vta;
        String unidades_boni;
        String latitud;
        String longitud;
        String estatus;
        String estatus_reg;
        String qty_original;
        String id_pedido;
        String reg_anulado;
        String estatus_final;
        String no_pedido_cmf;
        String usuario;
        String fecha_sistema;

        compania = c.getString(COMPANIA);
        cliente = c.getString(CLIENTE);
        ruta = c.getString(RUTA);
        lugar_entrega = c.getString(LUGAR_ENTREGA);
        producto = c.getString(PRODUCTO);
        precio_pt = c.getString(PRECIO_PT);
        tipo_pedido = c.getString(TIPO_PEDIDO);
        no_ped_movil = c.getString(NO_PED_MOVIL);
        fecha = c.getString(FECHA);
        unidades_vta = c.getString(UNIDADES_VTA);
        unidades_boni = c.getString(UNIDADES_BONI);
        latitud = c.getString(LATITUD);
        longitud = c.getString(LONGITUD);
        estatus = c.getString(ESTATUS);
        estatus_reg = c.getString(ESTATUS_REG);
        qty_original = c.getString(QTY_ORIGINAL);
        id_pedido = c.getString(ID_PEDIDO);
        reg_anulado = c.getString(REG_ANULADO);
        estatus_final = c.getString(ESTATUS_FINAL);
        no_pedido_cmf = c.getString(NO_PEDIDO_CMF);
        usuario = c.getString(USUARIO);
        fecha_sistema = c.getString(FECHA_SISTEMA);

        try {
            jObject.put(ContratoPedidos.PedidosColumnas.COMPANIA,compania);
            jObject.put(ContratoPedidos.PedidosColumnas.CLIENTE,cliente);
            jObject.put(ContratoPedidos.PedidosColumnas.RUTA,ruta);
            jObject.put(ContratoPedidos.PedidosColumnas.LUGAR_ENTREGA,lugar_entrega);
            jObject.put(ContratoPedidos.PedidosColumnas.PRODUCTO,producto);
            jObject.put(ContratoPedidos.PedidosColumnas.PRECIO_PT,precio_pt);
            jObject.put(ContratoPedidos.PedidosColumnas.TIPO_PEDIDO,tipo_pedido);
            jObject.put(ContratoPedidos.PedidosColumnas.NO_PED_MOVIL,no_ped_movil);
            jObject.put(ContratoPedidos.PedidosColumnas.FECHA,fecha);
            jObject.put(ContratoPedidos.PedidosColumnas.UNIDADES_VTA,unidades_vta);
            jObject.put(ContratoPedidos.PedidosColumnas.UNIDADES_BONI,unidades_boni);
            jObject.put(ContratoPedidos.PedidosColumnas.LATITUD,latitud);
            jObject.put(ContratoPedidos.PedidosColumnas.LONGITUD,longitud);
            jObject.put(ContratoPedidos.PedidosColumnas.ESTATUS,estatus);
            jObject.put(ContratoPedidos.PedidosColumnas.ESTATUS_REG,estatus_reg);
            jObject.put(ContratoPedidos.PedidosColumnas.QTY_ORIGINAL,qty_original);
            jObject.put(ContratoPedidos.PedidosColumnas.ID_PEDIDO,id_pedido);
            jObject.put(ContratoPedidos.PedidosColumnas.REG_ANULADO,reg_anulado);
            jObject.put(ContratoPedidos.PedidosColumnas.ESTATUS_FINAL,estatus_final);
            jObject.put(ContratoPedidos.PedidosColumnas.NO_PEDIDO_CMF,no_pedido_cmf);
            jObject.put(ContratoPedidos.PedidosColumnas.USUARIO,usuario);
            jObject.put(ContratoPedidos.PedidosColumnas.FECHA_SISTEMA,fecha_sistema);
            //

        }catch (JSONException e){
            e.printStackTrace();
            System.out.println("ERROR JSON " + e.toString());
        }
        Log.i("Cursor a PEDIDOS", String.valueOf(jObject));

        return jObject;
    }
//Melanie Molina

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE IMAGENES\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final int COLUMNA_CODIGO = 2;
    public static final int COLUMNA_IMAGEN = 3;
    public static final int COLUMNA_NOMBRE = 4;

    public static JSONObject deCursorAJSONObjectImagen(Cursor c){
        JSONObject jObjects = new JSONObject();
        String codigo;
        String imagen;
        String nombre;

        imagen = c.getString(COLUMNA_IMAGEN);
        nombre = c.getString(COLUMNA_NOMBRE);

        try {
            jObjects.put(ContratoPedidos.ImagenesColumnas.IMAGEN,imagen);
            jObjects.put(ContratoPedidos.ImagenesColumnas.NOMBRE,nombre);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.i("Cursor a IMAGENES", String.valueOf(jObjects));
        return  jObjects;
    }

}