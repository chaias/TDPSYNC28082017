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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Isaias on 08/12/2016.
 */
public class ProviderPedidosUsr   {

   /* public static final String TAG = "Provider Usr";
    public static final String URI_NO_SOPORTADA = "Uri no soportada";

    private BaseDatosPedidos helper;

    private ContentResolver resolver;


    public ProviderPedidosUsr() {
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


    public static final String AUTORIDAD = "com.mosi.tdpsync";

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTORIDAD, "cabeceras_pedidos", CABECERAS_PEDIDOS);
        uriMatcher.addURI(AUTORIDAD, "cabeceras_pedidos/*", CABECERAS_PEDIDOS_ID);
        uriMatcher.addURI(AUTORIDAD, "cabeceras_pedidos/*///detalles", CABECERAS_ID_DETALLES);

       /* uriMatcher.addURI(AUTORIDAD, "detalles_pedidos", DETALLES_PEDIDOS);
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
    }*/
    // [/URI_MATCHER]

    // [CAMPOS_AUXILIARES]
  /* private static final String CABECERA_PEDIDO_JOIN_CLIENTE_Y_FORMA_PAGO = "cabecera_pedido " +
            "INNER JOIN cliente " +
            "ON cabecera_pedido.id_cliente = cliente.id " +
            "INNER JOIN forma_pago " +
            "ON cabecera_pedido.id_forma_pago = forma_pago.id";

    private static final String DETALLE_PEDIDO_JOIN_PRODUCTO =
            "detalle_pedido " +
                    "INNER JOIN producto " +
                    "ON detalle_pedido.id_producto = producto.id";

    private final String[] proyCabeceraPedido = new String[]{
            BaseDatosPedidos.Tablas.CABECERA_PEDIDO + "." + ContratoPedidos.CabecerasPedido.ID,
            ContratoPedidos.CabecerasPedido.FECHA,
            ContratoPedidos.Clientes.NOMBRES,
            ContratoPedidos.Clientes.APELLIDOS,
            ContratoPedidos.FormasPago.NOMBRE};

    private String[] proyDetalle = {
            BaseDatosPedidos.Tablas.DETALLE_PEDIDO + ".*",
            ContratoPedidos.Productos.NOMBRE
    };
    // [/CAMPOS_AUXILIARES]

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
        String id;
        int afectados;

        int match = ContratoPedidos.uriMatcher.match(uri);
        int affected;

        switch (match) {
            case ContratoPedidos.ALLROWS:
                affected = bd.delete(ContratoPedidos.USUARIOS,
                        selection,
                        selectionArgs);
                break;
            case ContratoPedidos.SINGLE_ROW:
                long idGasto = ContentUris.parseId(uri);
                affected = bd.delete(ContratoPedidos.USUARIOS,
                        ContratoPedidos.UsuarioColumnas.ID_REMOTA + "=" + idGasto
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                // Notificar cambio asociado a la uri
                resolver.
                        notifyChange(uri, null, false);
                break;
            default:
                throw new IllegalArgumentException("Elemento gasto desconocido: " +
                        uri);
        }
        return affected;

    }

    @NonNull
    private String getWhere(String selection, String id) {
        return ContratoPedidos.Productos.ID + "=" + "\"" + id + "\""
                + (!TextUtils.isEmpty(selection) ?
                " AND (" + selection + ')' : "");
    }

    @Override
    public String getType(Uri uri) {
        switch (ContratoPedidos.uriMatcher.match(uri)) {
            case ContratoPedidos.ALLROWS_USR:
                return ContratoPedidos.MULTIPLE_MIME_USUARIOS;
            case ContratoPedidos.SINGLE_ROW_USR:
                return ContratoPedidos.SINGLE_MIME_USUARIOS;
            default:
                throw new IllegalArgumentException("Tipo uri desconocido: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) throws IllegalArgumentException {

        Log.d(TAG, "Inserción en " + uri + "( " + values.toString() + " )\n");

        if (ContratoPedidos.uriMatcher.match(uri) != ContratoPedidos.ALLROWS_USR) {
            throw new IllegalArgumentException("URI desconocida : " + uri);
        }
        ContentValues contentValues;
        if (values != null) {
            contentValues = new ContentValues(values);
        } else {
            contentValues = new ContentValues();
        }

        // Inserción de nueva fila
        SQLiteDatabase db = helper.getWritableDatabase();
        long rowId = db.insert(ContratoPedidos.USUARIOS, null, contentValues);
        if (rowId > 0) {
            Uri uri_gasto = ContentUris.withAppendedId(
                    ContratoPedidos.CONTENT_URI_USUARIOS, rowId);
            resolver.notifyChange(uri_gasto, null, false);
            return uri_gasto;
        }
        throw new SQLException("Falla al insertar fila en : " + uri);

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Obtener base de datos
        SQLiteDatabase bd = helper.getReadableDatabase();

        // Comparar Uri
        int match = ContratoPedidos.uriMatcher.match(uri);

        // string auxiliar para los ids
        String id;

        Cursor c;

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (match) {
            case ContratoPedidos.ALLROWS_USR:
                // Consultando todos los registros
                c = bd.query(ContratoPedidos.USUARIOS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContratoPedidos.CONTENT_URI_USUARIOS);
                break;
            case ContratoPedidos.SINGLE_ROW:
                // Consultando un solo registro basado en el Id del Uri
                long idGasto = ContentUris.parseId(uri);
                c = bd.query(ContratoPedidos.USUARIOS, projection,
                        ContratoPedidos.UsuarioColumnas._ID + " = " + idGasto,
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(
                        resolver,
                        ContratoPedidos.CONTENT_URI_USUARIOS);
                break;
            default:
                throw new IllegalArgumentException("URI no soportada: " + uri);
        }

        return c;

    }

    private String construirFiltro(String filtro) {
        String sentencia = null;

        switch (filtro) {
            case ContratoPedidos.CabecerasPedido.FILTRO_CLIENTE:
                sentencia = "cliente.nombres";
                break;
            case ContratoPedidos.CabecerasPedido.FILTRO_FECHA:
                sentencia = "cabecera_pedido.fecha";
                break;
        }

        return sentencia;
    }

    private void notificarCambio(Uri uri) {
        resolver.notifyChange(uri, null);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase bd = helper.getWritableDatabase();
        String id;
        int afectados;

        int affected;
        switch (ContratoPedidos.uriMatcher.match(uri)) {
            case ContratoPedidos.ALLROWS_USR:
                affected = bd.update(ContratoPedidos.USUARIOS, values,
                        selection, selectionArgs);
                break;
            case ContratoPedidos.SINGLE_ROW_USR:
                String idGasto = uri.getPathSegments().get(1);
                affected = bd.update(ContratoPedidos.USUARIOS, values,
                        ContratoPedidos.UsuarioColumnas.ID_REMOTA + "=" + idGasto
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("URI desconocida: " + uri);
        }
        resolver.notifyChange(uri, null, false);
        return affected;
    }*/
}

