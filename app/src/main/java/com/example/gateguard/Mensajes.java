//VISTAS ADMINISTRADOR

package com.example.gateguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Mensajes extends AppCompatActivity {

    private ImageButton botonCerrarSesion;
    private ImageButton botonInicio;
    private ImageButton botonMensajes;
    private ImageButton botonPerfil;
    private ImageButton botonUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajes);

        botonCerrarSesion = findViewById(R.id.boton_salir);
        botonInicio = findViewById(R.id.boton_inicio);
        botonMensajes = findViewById(R.id.boton_mensajes);
        botonPerfil = findViewById(R.id.boton_perfil);
        botonUsuarios = findViewById(R.id.boton_usuarios);

        botonCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        botonInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresarInicio();
            }
        });

        botonMensajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mensajes();
            }
        });

        botonUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuarios();
            }
        });

        botonPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perfil();
            }
        });

    }

    public void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        finish();
        // Redirigir a la actividad InicioSesion
        Intent intent = new Intent(Mensajes.this, InicioSesion.class);
        intent.putExtra("msg", "cerrarSesion");
        startActivity(intent);
    }

    public void regresarInicio() {
        // Redirigir a la actividad Ciudadelas
        Intent intent = new Intent(Mensajes.this, EditarCiudadelas.class);
        startActivity(intent);
    }

    public void mensajes() {
        // Redirigir a la actividad Ciudadelas
        Intent intent = new Intent(Mensajes.this, Mensajes.class);
        startActivity(intent);
    }

    public void perfil() {
        // Redirigir a la actividad Perfil Usuario
        Intent intent = new Intent(Mensajes.this, PerfilUsuario.class);
        startActivity(intent);
    }

    public void usuarios() {
        // Redirigir a la actividad Ciudadelas
        Intent intent = new Intent(Mensajes.this, ListaUsuarios.class);
        startActivity(intent);
    }
}