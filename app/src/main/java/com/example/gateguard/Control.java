package com.example.gateguard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class Control extends AppCompatActivity {

    private ImageButton botonCerrarSesion;
    private ImageButton botonInicio;
    private ImageButton botonSoporte;
    private ImageButton botonPerfil;
    private ImageButton botonEstado;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        botonCerrarSesion = findViewById(R.id.boton_salir);
        botonInicio = findViewById(R.id.boton_inicio);
        botonSoporte = findViewById(R.id.boton_soporte);
        botonPerfil = findViewById(R.id.boton_perfil);
        botonEstado = findViewById(R.id.boton_estado);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        botonEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarEstadoDialog();
            }
        });

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

    }

    private void mostrarEstadoDialog() {
        if (isNetworkConnected()) {
            // Extraer el currentCiudadela y currentPuerta de los IntentExtras
            String currentCiudadela = getIntent().getStringExtra("nombreCiudadela");
            String currentPuerta = getIntent().getStringExtra("nombrePuerta");

            // Obtener una referencia a la ubicación de datos en Firebase
            DatabaseReference estadoRef = mDatabase.child("Gateguard").child("Usuarios")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("Ciudadelas")
                    .child(currentCiudadela)
                    .child(currentPuerta)
                    .child("estado");

            estadoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String estado = dataSnapshot.getValue(String.class); //Revisar esta linea, devuelve un null

                    Log.d("DEBUG", "Valor de estado: " + estado);

                    mostrarDialogoEmergente(estado);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mostrarErrorConexion();
                }
            });
        } else {
            mostrarErrorConexion();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void mostrarErrorConexion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error de Conexión")
                .setMessage("No se puede conectar al sistema. Verifique su conexión a internet.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cerrar el diálogo
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void mostrarDialogoEmergente(String estado) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        dialogBuilder.setView(dialogView);

        TextView dialogText = dialogView.findViewById(R.id.dialog_text);
        dialogText.setText("Estado: " + estado);

        AlertDialog alertDialog = dialogBuilder.create(); // Crear el AlertDialog

        Button dialogButton = dialogView.findViewById(R.id.dialog_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar el cuadro de diálogo
                alertDialog.dismiss(); // Cerrar el AlertDialog
            }
        });

        alertDialog.show();
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        finish();
        // Redirigir a la actividad InicioSesion
        Intent intent = new Intent(this, InicioSesion.class);
        intent.putExtra("msg", "cerrarSesion");
        startActivity(intent);
    }

    private void regresarInicio() {
        // Redirigir a la actividad Ciudadelas
        Intent intent = new Intent(this, Ciudadelas.class);
        startActivity(intent);
    }

    private void soporte() {
        // Redirigir a la actividad Soporte
        Intent intent = new Intent(this, Soporte.class);
        startActivity(intent);
    }

    private void perfil() {
        // Redirigir a la actividad Perfil Usuario
        Intent intent = new Intent(this, PerfilUsuario.class);
        startActivity(intent);
    }
}
