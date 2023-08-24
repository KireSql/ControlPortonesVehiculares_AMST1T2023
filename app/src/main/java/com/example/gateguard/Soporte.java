//VISTAS USUARIO

package com.example.gateguard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Soporte extends AppCompatActivity {

    private ImageButton botonCerrarSesion;
    private ImageButton botonInicio;
    private ImageButton botonSoporte;
    private ImageButton botonPerfil;
    private Button cancelarButton;
    private Button enviarButton;
    DatabaseReference db_reference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soporte);

        botonCerrarSesion = findViewById(R.id.boton_salir);
        botonInicio = findViewById(R.id.boton_inicio);
        botonSoporte = findViewById(R.id.boton_soporte);
        enviarButton = findViewById(R.id.boton_enviar);
        cancelarButton = findViewById(R.id.boton_cancelar);
        botonPerfil = findViewById(R.id.boton_perfil);

        // Obtener el usuario actual de Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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

        cancelarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelar(v);
            }
        });

        userId = user.getUid();

        iniciarBaseDeDatos();
        leerTweets();

        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicarComentario(userId);
            }
        });



    }

    public void iniciarBaseDeDatos() {
        db_reference = FirebaseDatabase.getInstance().getReference().child("GateGuard");
    }
    public void leerTweets() {
        db_reference.child("Soporte").child("Comentarios")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            System.out.println(snapshot);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.println(error.toException());
                    }
                });
    }

    public void escribirComentario(String autor, String comentario, String fecha){
        Map<String, String> escrito = new HashMap<String, String>();
        escrito.put("autor", autor);
        escrito.put("fecha", fecha);
        DatabaseReference comentarios = db_reference.child("Soporte").child("Comentarios");
        DatabaseReference nuevoTweetRef = comentarios.push(); //  Generar una nueva clave única
        nuevoTweetRef.child(comentario).child("autor").setValue(autor);
        nuevoTweetRef.child(comentario).child("fecha").setValue(fecha);
    }

    public void publicarComentario(String autor) {
        // Obtén el texto
        EditText mensajeEditText = findViewById(R.id.mensaje);
        String mensaje = mensajeEditText.getText().toString().trim();

        // Verifica si el tweet no está vacío
        if (!mensaje.isEmpty()) {
            // Obtiene la fecha actual
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String fecha = dateFormat.format(new Date());

            // Llama al método escribirTweets con el autor, texto del tweet y fecha
            escribirComentario(autor, mensaje, fecha);
            Toast.makeText(this, "El comentario ha sido enviado", Toast.LENGTH_SHORT).show();

            // Limpia el contenido del EditText después de publicar el tweet
            mensajeEditText.setText("");
        } else {
            // El tweet está vacío, muestra un mensaje de error o realiza alguna acción apropiada
            Toast.makeText(this, "Ingrese un comentario antes de enviarlo", Toast.LENGTH_SHORT).show();
        }

    }

    public void cancelar(View view) {
        Intent intent = new Intent(Soporte.this, Ciudadelas.class);
        startActivity(intent);
        finish();
    }

    public void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        finish();
        // Redirigir a la actividad InicioSesion
        Intent intent = new Intent(Soporte.this, InicioSesion.class);
        intent.putExtra("msg", "cerrarSesion");
        startActivity(intent);
    }

    public void regresarInicio() {
        // Redirigir a la actividad Ciudadelas
        Intent intent = new Intent(Soporte.this, Ciudadelas.class);
        startActivity(intent);
    }

    public void soporte() {
        // Redirigir a la actividad Ciudadelas
        Intent intent = new Intent(Soporte.this, Soporte.class);
        startActivity(intent);
    }

    public void perfil() {
        // Redirigir a la actividad Perfil Usuario
        Intent intent = new Intent(Soporte.this, PerfilUsuario.class);
        startActivity(intent);
    }
}