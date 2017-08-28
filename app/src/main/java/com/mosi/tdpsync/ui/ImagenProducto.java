package com.mosi.tdpsync.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.mosi.tdpsync.R;

public class ImagenProducto extends AppCompatActivity {

    String codigo,descripcion,imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen_producto);
        ImageView image = (ImageView) findViewById(R.id.imagen);
        TextView textView = (TextView)findViewById(R.id.descripcion_imagen);

        codigo = getIntent().getStringExtra("codigop");
        descripcion = getIntent().getStringExtra("nombrep");
        imagen = getIntent().getStringExtra("imagen");

        textView.setText(codigo + "  "+descripcion);
        int imageResource = getResources().getIdentifier(imagen, null, getPackageName());
        image.setImageDrawable(getResources().getDrawable(imageResource));
    }
}
