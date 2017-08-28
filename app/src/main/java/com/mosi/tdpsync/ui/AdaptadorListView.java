package com.mosi.tdpsync.ui;

public class AdaptadorListView {

    long _id;
    String descripcion,monto,bonificacion,cantidad,precio,imagen,producto;

    public AdaptadorListView(){
        descripcion = "";
        monto = "";
        bonificacion = "";
        cantidad = "";
        precio = "";
        imagen = "";
        producto = "";
    }

    public AdaptadorListView(long _id, String descripcion, String monto, String bonificacion, String cantidad, String precio, String producto){

        this._id = _id;
        this.descripcion = descripcion;
        this.monto = monto;
        this.bonificacion = bonificacion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.producto = producto;
    }

    public AdaptadorListView(long _id, String descripcion, String monto, String bonificacion, String cantidad, String precio, String imagen, String producto){
        this._id = _id;
        this.descripcion = descripcion;
        this.monto = monto;
        this.bonificacion = bonificacion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.imagen = imagen;
        this.producto = producto;
    }

    public long getIdIdentificador() {
        return _id;
    }

    public void setIdIdentificador(long _id) {
        this._id = _id;
    }

    public String getDescripcion(){
        return descripcion;
    }

    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }

    public String getMonto(){
        return monto;
    }

    public void setMonto(String monto){
        this.monto = monto;
    }

    public String getBonificacion(){
        return bonificacion;
    }

    public void setBonificacion(String bonificacion){
        this.bonificacion = bonificacion;
    }

    public String getCantidadvendida(){
        return cantidad;
    }

    public void setCantidadvendida(String cantidad){
        this.cantidad = cantidad;
    }

    public String getPreciounidad(){
        return precio;
    }

    public void setPreciounidad(String precio){
        this.precio = precio;
    }

    public String getImagen(){
        return imagen;
    }

    public void setImagen(){
        this.imagen = imagen;
    }

    public String getProductos(){
        return producto;
    }

    public void setProductos(){
        this.producto = producto;
    }
}