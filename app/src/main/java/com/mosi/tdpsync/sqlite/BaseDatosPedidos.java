package com.mosi.tdpsync.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.provider.BaseColumns;

import com.mosi.tdpsync.sqlite.ContratoPedidos.CabecerasPedido;
import com.mosi.tdpsync.sqlite.ContratoPedidos.Clientes;
import com.mosi.tdpsync.sqlite.ContratoPedidos.DetallesPedido;
import com.mosi.tdpsync.sqlite.ContratoPedidos.FormasPago;
import com.mosi.tdpsync.sqlite.ContratoPedidos.Productos;

public class BaseDatosPedidos extends SQLiteOpenHelper {

    private static final String NOMBRE_BASE_DATOS = "pedidos.db";

    private static final int VERSION_ACTUAL = 49;

    protected final Context contexto;

    interface Tablas {
        String CABECERA_PEDIDO = "cabecera_pedido";
        String DETALLE_PEDIDO = "detalle_pedido";
        String PRODUCTO = "producto";
        String CLIENTE = "cliente";
        String FORMA_PAGO = "forma_pago";

        String CIATAB = "ciatab"; //ya esta creado el codigo para sincronizar
        String CUSTAB = "custab";// ya esta creado el codigo para sincronizar
        String INVPTDTAB = "invptdtab";//ya esta creado el codigo para sincronizar
        String INVPTMTAB = "invptmtab";//ya esta creado el codigo para sincronizar
        String PR1TAB = "pr1tab";//ya esta creado el codigo para sincronizar
        String TR5TAB = "tr5tab";
        String TIPTAB = "tiptab";//ya esta creado el codigo para sincronizar
        String PEDIDO_MOVIL = "pedido_movil";//ya esta creado el codigo para sincronizar
        String TALONARIOS = "talonario";//ya esta creado el codigo para sincronizar
        String USUARIOS = "usuarios";// ya esta creado el codigo para sincronizar
        String IMAGENES = "imagenes";// ya esta creado el codigo para sincronizar /// no creado Isaias 22/09/2017  11:19 am
        String gasto = "gasto";// ya esta creado el codigo para sincronizar


    }

    interface Referencias {

        String ID_CABECERA_PEDIDO = String.format("REFERENCES %s(%s) ON DELETE CASCADE",
                Tablas.CABECERA_PEDIDO, CabecerasPedido.ID);

        String ID_PRODUCTO = String.format("REFERENCES %s(%s)",
                Tablas.PRODUCTO, Productos.ID);

        String ID_CLIENTE = String.format("REFERENCES %s(%s)",
                Tablas.CLIENTE, Clientes.ID);

        String ID_FORMA_PAGO = String.format("REFERENCES %s(%s)",
                Tablas.FORMA_PAGO, FormasPago.ID);
    }

    public BaseDatosPedidos(Context contexto) {
        super(contexto, NOMBRE_BASE_DATOS, null, VERSION_ACTUAL);
        this.contexto = contexto;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                db.setForeignKeyConstraintsEnabled(true);
            } else {
                db.execSQL("PRAGMA foreign_keys=ON");
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "%s TEXT UNIQUE NOT NULL,%s DATETIME NOT NULL,%s TEXT NOT NULL %s," +
                        "%s TEXT NOT NULL %s)",
                Tablas.CABECERA_PEDIDO, BaseColumns._ID,
                CabecerasPedido.ID, CabecerasPedido.FECHA,
                CabecerasPedido.ID_CLIENTE, Referencias.ID_CLIENTE,
                CabecerasPedido.ID_FORMA_PAGO, Referencias.ID_FORMA_PAGO));

        db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "%s TEXT NOT NULL %s,%s INTEGER NOT NULL CHECK (%s>0),%s INTEGER NOT NULL %s," +
                        "%s INTEGER NOT NULL,%s REAL NOT NULL,UNIQUE (%s,%s) )",
                Tablas.DETALLE_PEDIDO, BaseColumns._ID,
                DetallesPedido.ID_CABECERA_PEDIDO, Referencias.ID_CABECERA_PEDIDO,
                DetallesPedido.SECUENCIA, DetallesPedido.SECUENCIA,
                DetallesPedido.ID_PRODUCTO, Referencias.ID_PRODUCTO,
                DetallesPedido.CANTIDAD, DetallesPedido.PRECIO,
                DetallesPedido.ID_CABECERA_PEDIDO, DetallesPedido.SECUENCIA));

        db.execSQL(String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "%s TEXT NOT NULL UNIQUE,%s TEXT NOT NULL,%s REAL NOT NULL," +
                        "%s INTEGER NOT NULL CHECK(%s>=0) )",
                Tablas.PRODUCTO, BaseColumns._ID,
                Productos.ID, Productos.NOMBRE, Productos.PRECIO,
                Productos.EXISTENCIAS, Productos.EXISTENCIAS));

        db.execSQL(String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "%s TEXT NOT NULL UNIQUE,%s TEXT NOT NULL,%s TEXT NOT NULL,%s )",
                Tablas.CLIENTE, BaseColumns._ID,
                Clientes.ID, Clientes.NOMBRES, Clientes.APELLIDOS, Clientes.TELEFONO));

        db.execSQL(String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "%s TEXT NOT NULL UNIQUE,%s TEXT NOT NULL )",
                Tablas.FORMA_PAGO, BaseColumns._ID,
                FormasPago.ID, FormasPago.NOMBRE));

        String cmd_pedidomovil = "CREATE TABLE " + Tablas.PEDIDO_MOVIL + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContratoPedidos.PedidosColumnas.COMPANIA + " TEXT, " +
                ContratoPedidos.PedidosColumnas.CLIENTE + " TEXT, " +
                ContratoPedidos.PedidosColumnas.RUTA + " TEXT, " +
                ContratoPedidos.PedidosColumnas.LUGAR_ENTREGA + " TEXT, " +
                ContratoPedidos.PedidosColumnas.PRODUCTO + " TEXT, " +
                ContratoPedidos.PedidosColumnas.PRECIO_PT + " TEXT, " +
                ContratoPedidos.PedidosColumnas.TIPO_PEDIDO + " TEXT, " +
                ContratoPedidos.PedidosColumnas.NO_PED_MOVIL + " TEXT, " +
                ContratoPedidos.PedidosColumnas.FECHA + " TEXT, " +
                ContratoPedidos.PedidosColumnas.UNIDADES_VTA + " TEXT, " +
                ContratoPedidos.PedidosColumnas.UNIDADES_BONI + " TEXT, " +
                ContratoPedidos.PedidosColumnas.LATITUD + " TEXT, " +
                ContratoPedidos.PedidosColumnas.LONGITUD + " TEXT, " +
                ContratoPedidos.PedidosColumnas.ESTATUS + " TEXT, " +
                ContratoPedidos.PedidosColumnas.ESTATUS_REG + " TEXT, " +
                ContratoPedidos.PedidosColumnas.QTY_ORIGINAL + " TEXT, " +
                ContratoPedidos.PedidosColumnas.ID_PEDIDO + " TEXT, " +
                ContratoPedidos.PedidosColumnas.REG_ANULADO + " TEXT, " +
                ContratoPedidos.PedidosColumnas.ESTATUS_FINAL + " TEXT, " +
                ContratoPedidos.PedidosColumnas.NO_PEDIDO_CMF + " TEXT, " +
                ContratoPedidos.PedidosColumnas.USUARIO + " TEXT, " +
                ContratoPedidos.PedidosColumnas.FECHA_SISTEMA + " TEXT," +
                ContratoPedidos.PedidosColumnas.ID_REMOTA + " TEXT UNIQUE," +
                ContratoPedidos.PedidosColumnas.ESTADO + " INTEGER NOT NULL DEFAULT " + ContratoPedidos.ESTADO_OK + "," +
                ContratoPedidos.PedidosColumnas.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";

       db.execSQL(cmd_pedidomovil);


        String cmd_companias = "CREATE TABLE " + Tablas.CIATAB + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContratoPedidos.ColumnasCiatab.CIACOD + " TEXT, " +
                ContratoPedidos.ColumnasCiatab.CIANOMBRE + " TEXT," +
                ContratoPedidos.ColumnasCiatab.ID_REMOTA + " TEXT UNIQUE," +
                ContratoPedidos.ColumnasCiatab.ESTADO + " INTEGER NOT NULL DEFAULT " + ContratoPedidos.ESTADO_OK + "," +
                ContratoPedidos.ColumnasCiatab.PENDIENTE_INSERCCION +  " INTEGER NOT NULL DEFAULT 0)";

        db.execSQL(cmd_companias);


        String cmd_clientes = "CREATE TABLE " + Tablas.CUSTAB + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContratoPedidos.ColumnasCustab.CUSCIA + " TEXT, " +
                ContratoPedidos.ColumnasCustab.CUSCOD + " TEXT, " +
                ContratoPedidos.ColumnasCustab.CUSNAME + " TEXT, " +
                ContratoPedidos.ColumnasCustab.CUSDIR + " TEXT, " +
                ContratoPedidos.ColumnasCustab.CUSTEL + " TEXT, " +
                ContratoPedidos.ColumnasCustab.CUSNIT + " TEXT, " +
                ContratoPedidos.ColumnasCustab.CUSLIMCRE + " TEXT, " +
                ContratoPedidos.ColumnasCustab.CUSDIAVEN + " TEXT, " +
                ContratoPedidos.ColumnasCustab.CUSMAIL + " TEXT," +
                ContratoPedidos.ColumnasCustab.CUSLATITUD + " TEXT, " +
                ContratoPedidos.ColumnasCustab.CUSLONGITUD + " TEXT, " +
                ContratoPedidos.ColumnasCustab.CUSCAT + " TEXT, " +
                ContratoPedidos.ColumnasCustab.CUSRUTA + " TEXT, " +
                ContratoPedidos.ColumnasCustab.CUSIVA + " TEXT, " +
                ContratoPedidos.ColumnasCustab.ID_REMOTA + " TEXT UNIQUE," +
                ContratoPedidos.ColumnasCustab.ESTADO + " INTEGER NOT NULL DEFAULT " + ContratoPedidos.ESTADO_OK + "," +
                ContratoPedidos.ColumnasCustab.PENDIENTE_INSERCCION + " INTEGER NOT NULL DEFAULT 0)";
        db.execSQL(cmd_clientes);


        String cmd_usuarios = "CREATE TABLE " + Tablas.USUARIOS + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContratoPedidos.ColumnasUsuarios.NOMBRE + " TEXT, " +
                ContratoPedidos.ColumnasUsuarios.PASSWORD + " TEXT," +
                ContratoPedidos.ColumnasUsuarios.ID_REMOTA + " TEXT UNIQUE," +
                ContratoPedidos.ColumnasUsuarios.ESTADO + " INTEGER NOT NULL DEFAULT " + ContratoPedidos.ESTADO_OK + "," +
                ContratoPedidos.ColumnasUsuarios.PENDIENTE_INSERCCION + " INTEGER NOT NULL DEFAULT 0)";

        db.execSQL(cmd_usuarios);


        String cmd_productos = "CREATE TABLE " + Tablas.INVPTMTAB + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContratoPedidos.ColumnasInvptmtab.INVPTMCIA + " TEXT, " +
                ContratoPedidos.ColumnasInvptmtab.INVPTMCOD + " TEXT, " +
                ContratoPedidos.ColumnasInvptmtab.INVPTMDESC + " TEXT, " +
                ContratoPedidos.ColumnasInvptmtab.INVPTMMED + " TEXT, " +
                ContratoPedidos.ColumnasInvptmtab.INVPTMEMP + " TEXT, " +
                ContratoPedidos.ColumnasInvptmtab.INVPTMIVA + " TEXT," +
                ContratoPedidos.ColumnasInvptmtab.ID_REMOTA + " TEXT UNIQUE," +
                ContratoPedidos.ColumnasInvptmtab.ESTADO + " INTEGER NOT NULL DEFAULT " + ContratoPedidos.ESTADO_OK + "," +
                ContratoPedidos.ColumnasInvptmtab.PENDIENTE_INSERCCION + " INTEGER NOT NULL DEFAULT 0)";


        db.execSQL(cmd_productos);

        String cmd_precios = "CREATE TABLE " + Tablas.PR1TAB + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContratoPedidos.ColumnasPrecio.PR1CIA + " TEXT, " +
                ContratoPedidos.ColumnasPrecio.PR1COD + " TEXT, " +
                ContratoPedidos.ColumnasPrecio.PR1CAT + " TEXT, " +
                ContratoPedidos.ColumnasPrecio.PR1FEC + " TEXT, " +
                ContratoPedidos.ColumnasPrecio.PR1PREC + " TEXT, " +
                ContratoPedidos.ColumnasPrecio.PR1DESC + " TEXT," +
                ContratoPedidos.ColumnasPrecio.ID_REMOTA + " TEXT UNIQUE," +
                ContratoPedidos.ColumnasPrecio.ESTADO + " INTEGER NOT NULL DEFAULT " + ContratoPedidos.ESTADO_OK + "," +
                ContratoPedidos.ColumnasPrecio.PENDIENTE_INSERCCION + " INTEGER NOT NULL DEFAULT 0)";

        db.execSQL(cmd_precios);


        String cmd_tipdoc = "CREATE TABLE " + Tablas.TIPTAB + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContratoPedidos.ColumnasTipos.DOCCIA + " TEXT, " +
                ContratoPedidos.ColumnasTipos.DOCCOD + " TEXT, " +
                ContratoPedidos.ColumnasTipos.DOCNAME + " TEXT," +
                ContratoPedidos.ColumnasTipos.ID_REMOTA + " TEXT UNIQUE," +
                ContratoPedidos.ColumnasTipos.ESTADO + " INTEGER NOT NULL DEFAULT " + ContratoPedidos.ESTADO_OK + "," +
                ContratoPedidos.ColumnasPrecio.PENDIENTE_INSERCCION + " INTEGER NOT NULL DEFAULT 0)";

        db.execSQL(cmd_tipdoc);


        String cmd_boni = "CREATE TABLE " + Tablas.TR5TAB + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContratoPedidos.ColumnasBonificaciones.PR5CIA + " TEXT, " +
                ContratoPedidos.ColumnasBonificaciones.PR5COD + " TEXT, " +
                ContratoPedidos.ColumnasBonificaciones.PR5FECINICIAL + " TEXT, " +
                ContratoPedidos.ColumnasBonificaciones.PR5FECFINAL + " TEXT, " +
                ContratoPedidos.ColumnasBonificaciones.PR5RANGO1 + " TEXT, " +
                ContratoPedidos.ColumnasBonificaciones.PR5RANGO2 + " TEXT, " +
                ContratoPedidos.ColumnasBonificaciones.PR5CNT + " TEXT," +
                ContratoPedidos.ColumnasBonificaciones.ID_REMOTA + " TEXT UNIQUE," +
                ContratoPedidos.ColumnasBonificaciones.ESTADO + " INTEGER NOT NULL DEFAULT " + ContratoPedidos.ESTADO_OK + "," +
                ContratoPedidos.ColumnasBonificaciones.PENDIENTE_INSERCCION + " INTEGER NOT NULL DEFAULT 0)";

        db.execSQL(cmd_boni);


        String cmd_dispi = "CREATE TABLE " + Tablas.INVPTDTAB + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContratoPedidos.ColumnasInvptdtab.INVPTDCIA + " TEXT, " +
                ContratoPedidos.ColumnasInvptdtab.INVPTDCOD + " TEXT, " +
                ContratoPedidos.ColumnasInvptdtab.INVPTDDISPI + " TEXT," +
                ContratoPedidos.ColumnasInvptdtab.ID_REMOTA + " TEXT UNIQUE," +
                ContratoPedidos.ColumnasInvptdtab.ESTADO + " INTEGER NOT NULL DEFAULT " + ContratoPedidos.ESTADO_OK + "," +
                ContratoPedidos.ColumnasInvptdtab.PENDIENTE_INSERCCION + " INTEGER NOT NULL DEFAULT 0)";

        db.execSQL(cmd_dispi);

        String cmd_talonarios = "CREATE TABLE " + Tablas.TALONARIOS + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContratoPedidos.ColumnasTalonarios.TALCOD + " TEXT , " +
                ContratoPedidos.ColumnasTalonarios.TALCIA + " TEXT , " +
                ContratoPedidos.ColumnasTalonarios.TALDES + " TEXT , " +
                ContratoPedidos.ColumnasTalonarios.TALCOR + " TEXT ," +
                ContratoPedidos.ColumnasTalonarios.ID_REMOTA + " TEXT UNIQUE," +
                ContratoPedidos.ColumnasTalonarios.ESTADO + " INTEGER NOT NULL DEFAULT " + ContratoPedidos.ESTADO_OK + "," +
                ContratoPedidos.ColumnasTalonarios.PENDIENTE_INSERCCION + " INTEGER NOT NULL DEFAULT 0)";

        db.execSQL(cmd_talonarios);

        String cmd_imagenes = "CREATE TABLE " + Tablas.IMAGENES + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContratoPedidos.ColumnasImagenes.CODIGO + " TEXT , " +
                ContratoPedidos.ColumnasImagenes.IMAGEN + " BLOB , " +
                ContratoPedidos.ColumnasImagenes.NOMBRE + " TEXT , " +
                ContratoPedidos.ColumnasImagenes.ID_REMOTA + " TEXT UNIQUE," +
                ContratoPedidos.ColumnasImagenes.ESTADO + " INTEGER NOT NULL DEFAULT " + ContratoPedidos.ESTADO_OK + "," +
                ContratoPedidos.ColumnasImagenes.PENDIENTE_INSERCCION + " INTEGER NOT NULL DEFAULT 0)";

        db.execSQL(cmd_imagenes);

        String cmd = "CREATE TABLE " + Tablas.gasto + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContratoPedidos.Columnas.MONTO + " TEXT, " +
                ContratoPedidos.Columnas.ETIQUETA + " TEXT, " +
                ContratoPedidos.Columnas.FECHA + " TEXT, " +
                ContratoPedidos.Columnas.DESCRIPCION + " TEXT," +
                ContratoPedidos.Columnas.ID_REMOTA + " TEXT UNIQUE," +
                ContratoPedidos.Columnas.ESTADO + " INTEGER NOT NULL DEFAULT " + ContratoPedidos.ESTADO_OK + "," +
                ContratoPedidos.Columnas.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";

        System.out.println("TABLA GASTO " + cmd);
        db.execSQL(cmd);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Tablas.CABECERA_PEDIDO);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.DETALLE_PEDIDO);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.PRODUCTO);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.CLIENTE);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.FORMA_PAGO);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.CIATAB);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.CUSTAB);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.INVPTDTAB);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.INVPTMTAB);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.PR1TAB);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.TR5TAB);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.TIPTAB);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.TALONARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.PEDIDO_MOVIL);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.gasto);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.IMAGENES);


        onCreate(db);
    }

   public String[] allData(){
       //http://www.thaicreate.com/mobile/android-sqlite-autocompletetextview.html
       try{
           String arrData[] = null;
           SQLiteDatabase db;
           db = this.getReadableDatabase(); //Lector de Data

           String sql = "select cianombre from ciatab";
           Cursor c = db.rawQuery(sql,null);

           if (c != null){
               if (c.moveToFirst()){
                   arrData = new String[c.getCount()];
                   int i = 0;
                   do {
                       arrData[i] = c.getString(0);
                       i++;
                   }while (c.moveToNext());
               }
           }
           c.close();
           return arrData;

       }catch (Exception e){
           return null;
       }

   }

    public String[] allDataCliente(String cia){
        //http://www.thaicreate.com/mobile/android-sqlite-autocompletetextview.html
        try{
            String arrData[] = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); //Lector de Data

            String sql = "select cusname from custab where cuscia = " + cia;
            Cursor c = db.rawQuery(sql,null);

            if (c != null){
                if (c.moveToFirst()){
                    arrData = new String[c.getCount()];
                    int i = 0;
                    do {
                        arrData[i] = c.getString(0);
                        i++;
                    }while (c.moveToNext());
                }
            }
            c.close();
            return arrData;

        }catch (Exception e){
            return null;
        }

    }

    public String[] allDataProducts(String cia){
        //http://www.thaicreate.com/mobile/android-sqlite-autocompletetextview.html
        try{
            String arrData[] = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); //Lector de Data

            String sql = "select invptmdesc from invptmtab where invptmcia = " + cia;
            Cursor c = db.rawQuery(sql,null);

            if (c != null){
                if (c.moveToFirst()){
                    arrData = new String[c.getCount()];
                    int i = 0;
                    do {
                        arrData[i] = c.getString(0);
                        i++;
                    }while (c.moveToNext());
                }
            }
            c.close();
            return arrData;

        }catch (Exception e){
            return null;
        }

    }

}