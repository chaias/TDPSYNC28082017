package com.mosi.tdpsync.sqlite;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.mosi.tdpsync.sqlite.BaseDatosPedidos.Tablas;
import com.mosi.tdpsync.sqlite.ContratoPedidos.CabecerasPedido;
import com.mosi.tdpsync.sqlite.ContratoPedidos.Productos;

import java.util.ArrayList;

public class ProviderPedidos extends ContentProvider {


    public static final String TAG = "Provider";
    public static final String URI_NO_SOPORTADA = "Uri no soportada";

    private BaseDatosPedidos helper;

    private ContentResolver resolver;


    public ProviderPedidos() {
    }

    // [URI_MATCHER]
    public static final UriMatcher uriMatcher;

    // Casos
    public static final int CABECERAS_PEDIDOS = 100;
    public static final int CABECERAS_PEDIDOS_ID = 101;
    public static final int CABECERAS_ID_DETALLES = 102;

    public static final int DETALLES_PEDIDOS = 200;
    public static final int DETALLES_PEDIDOS_ID = 201;

    public static final int PRODUCTOS = 300;
    public static final int PRODUCTOS_ID = 301;

    public static final int CLIENTES = 400;
    public static final int CLIENTES_ID = 401;

    public static final int FORMAS_PAGO = 500;
    public static final int FORMAS_PAGO_ID = 501;

    public static final int USUARIOS = 600;
    public static final int USUARIOS_ID = 601;

    public static final int CIATAB = 700;
    public static final int CIATAB_ID = 701;

    public static final int CUSTAB = 800;
    public static final int CUSTAB_ID = 801;

    public static final int PEDIDO_MOVIL = 900;
    public static final int PEDIDO_MOVIL_ID = 901;

    public static final int INVPTDTAB = 1000;
    public static final int INVPTDTAB_ID = 1001;

    public static final int INVPTMTAB = 1100;
    public static final int INVPTMTAB_ID = 1101;

    public static final int PR1TAB = 1200;
    public static final int PR1TAB_ID = 1201;

    public static final int TR5TAB = 1300;
    public static final int TR5TAB_ID = 1301;

    public static final int TIPTAB = 1400;
    public static final int TIPTAB_ID = 1401;

    public static final int TALONARIOS = 1500;
    public static final int TALONARIOS_ID = 1501;
    public static final int TALONARIOS_ID_MULTIPLE = 1502;

    public static final int GASTO = 1600;
    public static final int GASTO_ID = 1601;

    public static final int IMAGENES = 1700;
    public static final int IMAGENES_ID = 1701;


    public static final String AUTORIDAD = "com.mosi.tdpsync";

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTORIDAD, "cabeceras_pedidos", CABECERAS_PEDIDOS);
        uriMatcher.addURI(AUTORIDAD, "cabeceras_pedidos/*", CABECERAS_PEDIDOS_ID);
        uriMatcher.addURI(AUTORIDAD, "cabeceras_pedidos/*/detalles", CABECERAS_ID_DETALLES);

        uriMatcher.addURI(AUTORIDAD, "detalles_pedidos", DETALLES_PEDIDOS);
        uriMatcher.addURI(AUTORIDAD, "detalles_pedidos/*", DETALLES_PEDIDOS_ID);

        uriMatcher.addURI(AUTORIDAD, "productos", PRODUCTOS);
        uriMatcher.addURI(AUTORIDAD, "productos/*", PRODUCTOS_ID);

        uriMatcher.addURI(AUTORIDAD, "clientes", CLIENTES);
        uriMatcher.addURI(AUTORIDAD, "clientes/*", CLIENTES_ID);

        uriMatcher.addURI(AUTORIDAD, "formas_pago", FORMAS_PAGO);
        uriMatcher.addURI(AUTORIDAD, "formas_pago/*", FORMAS_PAGO_ID);

        uriMatcher.addURI(AUTORIDAD, "usuarios", USUARIOS);
        uriMatcher.addURI(AUTORIDAD, "usuarios/*", USUARIOS_ID);

        uriMatcher.addURI(AUTORIDAD, "ciatab", CIATAB);
        uriMatcher.addURI(AUTORIDAD, "ciatab/*", CIATAB_ID);

        uriMatcher.addURI(AUTORIDAD, "custab", CUSTAB);
        uriMatcher.addURI(AUTORIDAD, "custab/*", CUSTAB_ID);

        uriMatcher.addURI(AUTORIDAD, "invptdtab", INVPTDTAB);
        uriMatcher.addURI(AUTORIDAD, "invptdtab/*", INVPTDTAB_ID);

        uriMatcher.addURI(AUTORIDAD, "invptmtab", INVPTMTAB);
        uriMatcher.addURI(AUTORIDAD, "invptmtab/*", INVPTMTAB_ID);

        uriMatcher.addURI(AUTORIDAD, "pr1tab", PR1TAB);
        uriMatcher.addURI(AUTORIDAD, "pr1tab/*", PR1TAB_ID);

        uriMatcher.addURI(AUTORIDAD, "tr5tab", TR5TAB);
        uriMatcher.addURI(AUTORIDAD, "tr5tab/*", TR5TAB_ID);

        uriMatcher.addURI(AUTORIDAD, "tiptab", TIPTAB);
        uriMatcher.addURI(AUTORIDAD, "tiptab/*", TIPTAB_ID);

        uriMatcher.addURI(AUTORIDAD, "pedido_movil", PEDIDO_MOVIL);
        uriMatcher.addURI(AUTORIDAD, "pedido_movil/*", PEDIDO_MOVIL_ID);

        uriMatcher.addURI(AUTORIDAD, "talonarios", TALONARIOS);
        uriMatcher.addURI(AUTORIDAD, "talonarios/*", TALONARIOS_ID);

        uriMatcher.addURI(AUTORIDAD, "gasto", GASTO);
        uriMatcher.addURI(AUTORIDAD, "gasto/*", GASTO_ID);
    }
    // [/URI_MATCHER]

    @Override
    public boolean onCreate() {
        helper = new BaseDatosPedidos(getContext());
        resolver = getContext().getContentResolver();
        return true;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete: " + uri);

        SQLiteDatabase bd = helper.getWritableDatabase();
        long id;
        int afectados;

        int match = ContratoPedidos.uriMatcher.match(uri);
        int affected;

        switch (uriMatcher.match(uri)) {
            case GASTO_ID:
                 id = ContentUris.parseId(uri);
                affected = bd.delete("gasto",ContratoPedidos.Columnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.notifyChange(uri, null, false);
                break;
            case USUARIOS_ID:
                id = ContentUris.parseId(uri);
                affected = bd.delete(Tablas.USUARIOS,ContratoPedidos.UsuarioColumnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.notifyChange(uri, null, false);
                break;
            case CIATAB_ID:
                id = ContentUris.parseId(uri);
                affected = bd.delete(Tablas.CIATAB,ContratoPedidos.CiatabColumnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.notifyChange(uri, null, false);
                break;
            case INVPTDTAB_ID:
                id = ContentUris.parseId(uri);
                affected = bd.delete(Tablas.INVPTDTAB,ContratoPedidos.InvptdtabColumnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.notifyChange(uri, null, false);
                break;
            case CUSTAB_ID:
                id = ContentUris.parseId(uri);
                affected = bd.delete(Tablas.CUSTAB,ContratoPedidos.CustabColumnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.notifyChange(uri, null, false);
                break;
            case TALONARIOS_ID:
                id = ContentUris.parseId(uri);
                affected = bd.delete(Tablas.TALONARIOS,ContratoPedidos.TalonariosColumnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.notifyChange(uri, null, false);
                break;
            case INVPTMTAB_ID:
                id = ContentUris.parseId(uri);
                affected = bd.delete(Tablas.INVPTMTAB,ContratoPedidos.InvptmtabColumnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.notifyChange(uri, null, false);
                break;
            case PR1TAB_ID:
                id = ContentUris.parseId(uri);
                affected = bd.delete(Tablas.PR1TAB,ContratoPedidos.Pr1tabColumnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.notifyChange(uri, null, false);
                break;
            case TIPTAB_ID:
                id = ContentUris.parseId(uri);
                affected = bd.delete(Tablas.TIPTAB,ContratoPedidos.TiptabColumnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.notifyChange(uri, null, false);
                break;
            case PEDIDO_MOVIL_ID:
                id = ContentUris.parseId(uri);
                affected = bd.delete(Tablas.PEDIDO_MOVIL,ContratoPedidos.PedidosColumnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.notifyChange(uri, null, false);
                break;
            case PEDIDO_MOVIL:

                affected = bd.delete(Tablas.PEDIDO_MOVIL,selection,
                        selectionArgs);
                /* afectados = bd.update(Tablas.gasto,values,
                        selection,selectionArgs);*/
                break;
            case TALONARIOS:

                affected = bd.delete(Tablas.TALONARIOS,selection,
                        selectionArgs);
                /* afectados = bd.update(Tablas.gasto,values,
                        selection,selectionArgs);*/
                break;
            case IMAGENES:

                affected = bd.delete(Tablas.IMAGENES,selection,
                        selectionArgs);
                /* afectados = bd.update(Tablas.gasto,values,
                        selection,selectionArgs);*/
                break;
            default:
                throw new IllegalArgumentException("Elemento gasto desconocido: " +
                        uri);
        }
        return affected;

    }

    @NonNull
    private String getWhere(String selection, String id) {
        return Productos.ID + "=" + "\"" + id + "\""
                + (!TextUtils.isEmpty(selection) ?
                " AND (" + selection + ')' : "");
    }

    @Override
    public String getType(Uri uri) {
        switch (ContratoPedidos.uriMatcher.match(uri)) {
            case GASTO:
                return ContratoPedidos.generarMime("gasto");
            case GASTO_ID:
                return ContratoPedidos.generarMimeItem("gasto");
            default:
                throw new IllegalArgumentException("Tipo uri desconocido: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) throws IllegalArgumentException {

        Log.d(TAG, "Inserción en " + uri + "( " + values.toString() + " )\n");
        SQLiteDatabase bd = helper.getWritableDatabase();

       switch (uriMatcher.match(uri)){
           case GASTO:
               bd.insertOrThrow(Tablas.gasto,null,values);
               notificarCambio(uri);
               return ContratoPedidos.Columnas.crearUriGasto(values.getAsString(Tablas.gasto));
           case USUARIOS:
               bd.insertOrThrow("usuarios",null,values);
               notificarCambio(uri);
               return ContratoPedidos.UsuarioColumnas.crearUriUsuarios(values.getAsString(Tablas.USUARIOS));
           case CIATAB:
               bd.insertOrThrow(Tablas.CIATAB,null,values);
               notificarCambio(uri);
               return ContratoPedidos.CiatabColumnas.crearUriCiatab(values.getAsString(Tablas.CIATAB));
           case INVPTDTAB:
               bd.insertOrThrow(Tablas.INVPTDTAB,null,values);
               notificarCambio(uri);
               return ContratoPedidos.InvptdtabColumnas.crearUriInvptdtab(values.getAsString(Tablas.INVPTDTAB));
           case CUSTAB:
               bd.insertOrThrow(Tablas.CUSTAB,null,values);
               notificarCambio(uri);
               return ContratoPedidos.CustabColumnas.crearUriCustab(values.getAsString(Tablas.CUSTAB));
           case TALONARIOS:
               bd.insertOrThrow(Tablas.TALONARIOS,null,values);
               notificarCambio(uri);
               return ContratoPedidos.TalonariosColumnas.crearUriTalonarios(values.getAsString(Tablas.TALONARIOS));
           case INVPTMTAB:
               bd.insertOrThrow(Tablas.INVPTMTAB,null,values);
               notificarCambio(uri);
               return ContratoPedidos.InvptmtabColumnas.crearUriInvptmtab(values.getAsString(Tablas.INVPTMTAB));
           case PR1TAB:
               bd.insertOrThrow(Tablas.PR1TAB,null,values);
               notificarCambio(uri);
               return ContratoPedidos.Pr1tabColumnas.crearUriPr1tab(values.getAsString(Tablas.PR1TAB));
           case TIPTAB:
               bd.insertOrThrow(Tablas.TIPTAB,null,values);
               notificarCambio(uri);
               return ContratoPedidos.TiptabColumnas.crearUriTipos(values.getAsString(Tablas.TIPTAB));
           case PEDIDO_MOVIL:
               bd.insertOrThrow(Tablas.PEDIDO_MOVIL,null,values);
               notificarCambio(uri);
               return ContratoPedidos.PedidosColumnas.crearUriPedido(values.getAsString(Tablas.PEDIDO_MOVIL));
           case IMAGENES:
               bd.insertOrThrow(Tablas.IMAGENES,null,values);
               notificarCambio(uri);
               return ContratoPedidos.ImagenesColumnas.crearUriImagenes(values.getAsString(Tablas.IMAGENES));
           default:
               throw new SQLException("Falla al insertar fila en : " + uri);
       }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Obtener base de datos
        SQLiteDatabase bd = helper.getReadableDatabase();

        // Comparar Uri
        int match = uriMatcher.match(uri);

        // string auxiliar para los ids
        String id;

        Cursor c;

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (match) {
            case GASTO:
                c = bd.query(Tablas.gasto,projection,
                        selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case GASTO_ID:
                id = ContratoPedidos.Gasto.ObtenerIdGasto(uri);
                c = bd.query(Tablas.gasto,projection,
                        ContratoPedidos.Columnas._ID + "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs,
                        null, null, sortOrder);
                break;
            case USUARIOS:
                c = bd.query(Tablas.USUARIOS,projection,
                        selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case USUARIOS_ID:
                id = ContratoPedidos.Usuarios.ObtenerIdUsuarios(uri);
                c = bd.query(Tablas.USUARIOS,projection,
                        ContratoPedidos.UsuarioColumnas._ID+ "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs,
                        null, null, sortOrder);
                break;
            case CIATAB:
                c = bd.query(Tablas.CIATAB,projection,
                        selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case CIATAB_ID:
                id = ContratoPedidos.Ciatab.ObtenerIdCiatab(uri);
                c = bd.query(Tablas.CIATAB,projection,
                        ContratoPedidos.CiatabColumnas._ID+ "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs,
                        null, null, sortOrder);
                break;
            case INVPTDTAB:
                c = bd.query(Tablas.INVPTDTAB,projection,
                        selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case INVPTDTAB_ID:
                id = ContratoPedidos.Invptdtab.ObtenerIdInvptdtab(uri);
                c = bd.query(Tablas.INVPTDTAB,projection,
                        ContratoPedidos.InvptdtabColumnas._ID+ "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs,
                        null, null, sortOrder);
                break;
            case CUSTAB:
                c = bd.query(Tablas.CUSTAB,projection,
                        selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case CUSTAB_ID:
                id = ContratoPedidos.Custab.ObtenerIdCustab(uri);
                c = bd.query(Tablas.CUSTAB,projection,
                        ContratoPedidos.CustabColumnas._ID+ "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs,
                        null, null, sortOrder);
                break;
            case TALONARIOS:
                c = bd.query(Tablas.TALONARIOS,projection,
                        selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case TALONARIOS_ID:
                id = ContratoPedidos.Talonarios.ObtenerIdTalonarios(uri);
                c = bd.query(Tablas.TALONARIOS,projection,
                        ContratoPedidos.TalonariosColumnas._ID+ "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs,
                        null, null, sortOrder);
                break;
            case INVPTMTAB:
                c = bd.query(Tablas.INVPTMTAB,projection,
                        selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case INVPTMTAB_ID:
                id = ContratoPedidos.Invptmtab.ObtenerIdInvptmtab(uri);
                c = bd.query(Tablas.INVPTMTAB,projection,
                        ContratoPedidos.InvptmtabColumnas._ID+ "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs,
                        null, null, sortOrder);
                break;
            case PR1TAB:
                c = bd.query(Tablas.PR1TAB,projection,
                        selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case PR1TAB_ID:
                id = ContratoPedidos.Pr1tab.ObtenerIdPr1tab(uri);
                c = bd.query(Tablas.PR1TAB,projection,
                        ContratoPedidos.Pr1tabColumnas._ID+ "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs,
                        null, null, sortOrder);
                break;
            case TIPTAB:
                c = bd.query(Tablas.TIPTAB,projection,
                        selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case TIPTAB_ID:
                id = ContratoPedidos.Tipos.ObtenerIdTipos(uri);
                c = bd.query(Tablas.TIPTAB,projection,
                        ContratoPedidos.TiptabColumnas._ID+ "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs,
                        null, null, sortOrder);
                break;
            case PEDIDO_MOVIL:
                c = bd.query(Tablas.PEDIDO_MOVIL,projection,
                        selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case PEDIDO_MOVIL_ID:
                id = ContratoPedidos.Pedidos.ObtenerIdPedido(uri);
                c = bd.query(Tablas.PEDIDO_MOVIL,projection,
                        ContratoPedidos.PedidosColumnas._ID+ "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs,
                        null, null, sortOrder);
                break;
            case IMAGENES:
                c = bd.query(Tablas.IMAGENES,projection,
                        selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case IMAGENES_ID:
                id = ContratoPedidos.Imagenes.ObtenerIdImagenes(uri);
                c = bd.query(Tablas.IMAGENES,projection,
                        ContratoPedidos.ImagenesColumnas._ID+ "=" + "\'" + id + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("URI no soportada: " + uri);
        }
        c.setNotificationUri(resolver,uri);
        return c;
    }

    private String construirFiltro(String filtro) {
        String sentencia = null;

        switch (filtro) {
            case CabecerasPedido.FILTRO_CLIENTE:
                sentencia = "cliente.nombres";
                break;
            case CabecerasPedido.FILTRO_FECHA:
                sentencia = "cabecera_pedido.fecha";
                break;
        }

        return sentencia;
    }

    private void notificarCambio(Uri uri) {
        resolver.notifyChange(uri, null);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,String[] selectionArgs) {
        //_id INTEGER PRIMARY KEY AUTOINCREMENT, monto TEXT, etiqueta TEXT, fecha TEXT, descripcion TEXT,id_Remota TEXT UNIQUE,estado INTEGER NOT NULL DEFAULT 0,pendiente_insercion INTEGER NOT NULL DEFAULT 0)
        Log.d(TAG,"Actualizando: "+uri);

        SQLiteDatabase bd = helper.getWritableDatabase();
        String id;
        int afectados;

        switch (uriMatcher.match(uri)) {
            case GASTO_ID:
                id = uri.getPathSegments().get(1);
                afectados = bd.update("gasto",values,
                        ContratoPedidos.Columnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case GASTO:
                afectados = bd.update(Tablas.gasto,values,
                        selection,selectionArgs);
                break;
            case USUARIOS_ID:
                id = uri.getPathSegments().get(1);
                afectados = bd.update(Tablas.USUARIOS,values,
                        ContratoPedidos.UsuarioColumnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case CIATAB_ID:
                id = uri.getPathSegments().get(1);
                afectados = bd.update(Tablas.CIATAB,values,
                        ContratoPedidos.CiatabColumnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case INVPTDTAB_ID:
                id = uri.getPathSegments().get(1);
                afectados = bd.update(Tablas.INVPTDTAB,values,
                        ContratoPedidos.InvptdtabColumnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case INVPTDTAB:
                afectados = bd.update(Tablas.INVPTDTAB,values,
                        selection,selectionArgs);
                break;
            case CUSTAB_ID:
                id = uri.getPathSegments().get(1);
                afectados = bd.update(Tablas.CUSTAB,values,
                        ContratoPedidos.CustabColumnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case TALONARIOS_ID:
                id = uri.getPathSegments().get(1);
                afectados = bd.update(Tablas.TALONARIOS,values,
                        ContratoPedidos.TalonariosColumnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case TALONARIOS:
                afectados = bd.update(Tablas.TALONARIOS,values,
                        selection,selectionArgs);
                break;
            case INVPTMTAB_ID:
                id = uri.getPathSegments().get(1);
                afectados = bd.update(Tablas.INVPTMTAB,values,
                        ContratoPedidos.InvptmtabColumnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case PR1TAB_ID:
                id = uri.getPathSegments().get(1);
                afectados = bd.update(Tablas.PR1TAB,values,
                        ContratoPedidos.Pr1tabColumnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case TIPTAB_ID:
                id = uri.getPathSegments().get(1);
                afectados = bd.update(Tablas.TIPTAB,values,
                        ContratoPedidos.TiptabColumnas.ID_REMOTA + "="+id
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case PEDIDO_MOVIL_ID:
                id = ContratoPedidos.PedidosColumnas.ObtenerIdPedido(uri);
                afectados = bd.update(Tablas.PEDIDO_MOVIL, values,
                        ContratoPedidos.PedidosColumnas.ID_REMOTA + "=" + "\"" + id + "\""
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case PEDIDO_MOVIL:
                afectados = bd.update(Tablas.PEDIDO_MOVIL,values,
                        selection,selectionArgs);
                break;
            case IMAGENES_ID:
                id = ContratoPedidos.ImagenesColumnas.ObtenerIdImagenes(uri);
                afectados = bd.update(Tablas.IMAGENES, values,
                        ContratoPedidos.ImagenesColumnas.ID_REMOTA + "=" + "\"" + id + "\""
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case IMAGENES:
                afectados = bd.update(Tablas.IMAGENES,values,
                        selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("URI desconocida update: " + uri);
        }
        resolver.notifyChange(uri,null,false);
        return afectados;
    }
}