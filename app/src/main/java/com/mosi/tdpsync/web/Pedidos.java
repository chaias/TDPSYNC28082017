package com.mosi.tdpsync.web;

/**
 * Created by Isaias on 23/11/2016.
 */
public class Pedidos {

    public  String id;
    public  String compania;
    public  String cliente;
    public  String ruta;
    public  String lugar_entrega;
    public  String producto;
    public  String precio_pt;
    public  String tipo_pedido;
    public  String no_ped_movil;
    public  String fecha;
    public  String unidades_vta;
    public  String unidades_boni;
    public  String latitud;
    public  String longitud;
    public  String estatus;
    public  String estatus_reg;
    public  String qty_original;
    public  String id_pedido;
    public  String reg_anulado;
    public  String estatus_final;
    public  String no_pedido_cmf;
    public  String usuario;
    public  String fecha_sistema;

    public Pedidos(String id,String compania,String cliente,String ruta, String lugar_entrega, String producto, String precio_pt,
                   String tipo_pedido,String no_ped_movil, String fecha, String unidades_vta, String unidades_boni, String latitud, String longitud,
                   String estatus,String estatus_reg,String qty_original,
                   String id_pedido, String reg_anulado,String estatus_final,String no_pedido_cmf, String usuario,String fecha_sistema){

        this.id = id;
        this.compania = compania;
        this.cliente = cliente;
        this.ruta = ruta;
        this.lugar_entrega = lugar_entrega;
        this.producto = producto;
        this.precio_pt = precio_pt;
        this.tipo_pedido = tipo_pedido;
        this.no_ped_movil = no_ped_movil;
        this.fecha = fecha;
        this.unidades_vta = unidades_vta;
        this.unidades_boni = unidades_boni;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estatus = estatus;
        this.estatus_reg = estatus_reg;
        this.qty_original = qty_original;
        this.id_pedido = id_pedido;
        this.reg_anulado = reg_anulado;
        this.estatus_final = estatus_final;
        this.no_pedido_cmf = no_pedido_cmf;
        this.usuario = usuario;
        this.fecha_sistema = fecha_sistema;

    }

}
