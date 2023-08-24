//VISTAS ADMINISTRADOR

package com.example.gateguard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public class RegistroCiudadela extends AppCompatActivity {

    private ImageButton botonCerrarSesion;
    private ImageButton botonInicio;
    private ImageButton botonMensajes;

    private ImageButton botonPerfil;
    private ImageButton botonUsuarios;
    private Button cancelarButton;
    private Button enviarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_ciudadela);

        botonCerrarSesion = findViewById(R.id.boton_salir);
        botonInicio = findViewById(R.id.boton_inicio);
        botonMensajes = findViewById(R.id.boton_mensajes);
        botonPerfil = findViewById(R.id.boton_perfil);
        botonUsuarios = findViewById(R.id.boton_usuarios);
        enviarButton = findViewById(R.id.boton_enviar);
        cancelarButton = findViewById(R.id.boton_cancelar);

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

        cancelarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelar(v);
            }
        });

        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void cancelar(View view) {
        Intent intent = new Intent(this, EditarCiudadelas.class);
        startActivity(intent);
        finish();
    }

    public void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        finish();
        // Redirigir a la actividad InicioSesion
        Intent intent = new Intent(this, InicioSesion.class);
        intent.putExtra("msg", "cerrarSesion");
        startActivity(intent);
    }

    public void regresarInicio() {
        // Redirigir a la actividad Ciudadelas
        Intent intent = new Intent(this, EditarCiudadelas.class);
        startActivity(intent);
    }

    public void mensajes() {
        // Redirigir a la actividad Soporte
        Intent intent = new Intent(this, Mensajes.class);
        startActivity(intent);
    }

    public void perfil() {
        // Redirigir a la actividad Perfil Usuario
        Intent intent = new Intent(this, PerfilUsuario.class);
        startActivity(intent);
    }

    public void usuarios() {
        // Redirigir a la actividad Ciudadelas
        Intent intent = new Intent(this, ListaUsuarios.class);
        startActivity(intent);
    }
}