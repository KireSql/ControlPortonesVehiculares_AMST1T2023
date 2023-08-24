//VISTAS USUARIO

package com.example.gateguard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.UUID;

public class PerfilUsuario extends AppCompatActivity {

    private ImageView fotoImageView;
    private TextView idUsuarioTextView;
    private TextView nombreUsuarioTextView;
    private TextView correoTextView;

    private ImageButton botonCerrarSesion;
    private ImageButton botonInicio;
    private ImageButton botonSoporte;
    private ImageButton botonPerfil;
    private String userId, providerId;
    private TextView botonEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        botonCerrarSesion = findViewById(R.id.boton_salir);
        botonInicio = findViewById(R.id.boton_inicio);
        botonSoporte = findViewById(R.id.boton_soporte);
        botonPerfil = findViewById(R.id.boton_perfil);
        botonEditar = findViewById(R.id.boton_editar);

        fotoImageView = findViewById(R.id.imv_foto);
        idUsuarioTextView = findViewById(R.id.txt_userId);
        nombreUsuarioTextView = findViewById(R.id.txt_nombre);
        correoTextView = findViewById(R.id.txt_correo);

        // Obtener el usuario actual de Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Obtener el ID del usuario
            userId = user.getUid();
            idUsuarioTextView.setText(userId);

            List<? extends UserInfo> providerDataList = user.getProviderData();
            for (UserInfo userInfo : providerDataList) {
                String providerId = userInfo.getProviderId();
                if (providerId.equals("google.com")) {
                    // Obtener el nombre de usuario (parte antes del arroba del correo electrónico)
                    String username = user.getDisplayName();
                    nombreUsuarioTextView.setText(username);

                    // Obtener correo electrónico del usuario
                    String email = user.getEmail();
                    correoTextView.setText(email);

                    // Obtener la foto del usuario desde Firebase Storage y mostrarla en el ImageView
                    String photoUrl = user.getPhotoUrl().toString();
                    Picasso.get()
                            .load(photoUrl)
                            .resize(300, 300) // Establecer el tamaño deseado
                            .centerCrop() // Recortar la imagen para ajustarla al tamaño especificado
                            .into(fotoImageView);

                } else if (providerId.equals("password")) {

                    if (user.getProviderData().size() > 2) {
                        // Obtener el nombre de usuario (parte antes del arroba del correo electrónico)
                        String username = user.getDisplayName();
                        nombreUsuarioTextView.setText(username);

                        // Obtener correo electrónico del usuario
                        String email = user.getEmail();
                        correoTextView.setText(email);

                        // Obtener la foto del usuario desde Firebase Storage y mostrarla en el ImageView
                        String photoUrl = user.getPhotoUrl().toString();
                        Picasso.get()
                                .load(photoUrl)
                                .resize(300, 300) // Establecer el tamaño deseado
                                .centerCrop() // Recortar la imagen para ajustarla al tamaño especificado
                                .into(fotoImageView);
                    } else {
                        // Usuario inició sesión mediante correo electrónico
                        String email = user.getEmail();
                        if (email != null) {
                            String username = user.getDisplayName();
                            if (username == null || username.isEmpty()) {
                                username = "Username";
                            }
                            nombreUsuarioTextView.setText(username);
                            correoTextView.setText(email);
                        }
                        // Verificar si el usuario tiene una foto de perfil en Firebase
                        Uri photoUrl = user.getPhotoUrl();
                        if (photoUrl != null) {
                            // Obtener la foto de perfil actual desde Firebase Storage y mostrarla en el ImageView
                            Picasso.get()
                                    .load(photoUrl)
                                    .resize(300, 300) // Establecer el tamaño deseado
                                    .centerCrop() // Recortar la imagen para ajustarla al tamaño especificado
                                    .into(fotoImageView);
                        } else {
                            // Utilizar la foto de perfil por defecto
                            Picasso.get()
                                    .load(R.drawable.foto_perfil)
                                    .resize(300, 300) // Establecer el tamaño deseado
                                    .centerCrop() // Recortar la imagen para ajustarla al tamaño especificado
                                    .into(fotoImageView);
                        }
                    }

                }
            }


        }

        // Obtener el correo electrónico del usuario desde Firebase Realtime Database
       /*FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("email")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String email = dataSnapshot.getValue(String.class);
                        correoTextView.setText(email);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Error al obtener el correo electrónico del usuario
                    }
                });*/

        botonCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion(v);
            }
        });

        botonInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresarInicio();
            }
        });

        botonSoporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soporte();
            }
        });

        botonPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perfil();
            }
        });

        botonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editar();
            }
        });
    }

    public void cerrarSesion(View view) {
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(this, InicioSesion.class);
        intent.putExtra("msg", "cerrarSesion");
        startActivity(intent);
    }

    public void regresarInicio() {
        // Redirigir a la actividad Ciudadelas
        Intent intent = new Intent(PerfilUsuario.this, Ciudadelas.class);
        startActivity(intent);
    }

    public void soporte() {
        // Redirigir a la actividad Soporte
        Intent intent = new Intent(PerfilUsuario.this, Soporte.class);
        startActivity(intent);
    }

    public void perfil() {
        // Redirigir a la actividad Perfil Usuario
        Intent intent = new Intent(PerfilUsuario.this, PerfilUsuario.class);
        startActivity(intent);
    }

    public void editar() {
        Intent intent = new Intent(PerfilUsuario.this, Editar.class);
        startActivity(intent);
    }
}