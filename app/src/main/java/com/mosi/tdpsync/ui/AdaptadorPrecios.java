package com.mosi.tdpsync.ui;

/**
 * Created by Isaias on 26/08/2017.
 */

public class AdaptadorPrecios  {

    long _id;
    String descripcion,codigo,unidades,imagen;



    public AdaptadorPrecios() {
        descripcion = "";
        codigo = "";
        unidades = "";
        imagen = "";
    }

    public AdaptadorPrecios(long _id, String descripcion, String codigo, String unidades,String imagen) {
        this._id = _id;
        this.descripcion = descripcion;
        this.codigo = codigo;
        this.unidades = unidades;
        this.imagen = imagen;
    }


    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getUnidades() {
        return unidades;
    }

    public void setUnidades(String unidades) {
        this.unidades = unidades;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }


}
