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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Control extends AppCompatActivity {

    private ImageButton botonCerrarSesion;
    private ImageButton botonInicio;
    private ImageButton botonSoporte;
    private ImageButton botonPerfil;
    private ImageButton botonEstado;
    private ImageButton botonAbrir;
    private ImageButton botonCerrar;
    private DatabaseReference mDatabase;
    private DatabaseReference estadoRef;
    private static final String BROKER_URL = "tcp://193.122.149.232:1883"; // Cambiar por la dirección de broker MQTT
    private static final String CLIENT_ID = "AndroidClient";
    private MqttClient mqttClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        botonCerrarSesion = findViewById(R.id.boton_salir);
        botonInicio = findViewById(R.id.boton_inicio);
        botonSoporte = findViewById(R.id.boton_soporte);
        botonPerfil = findViewById(R.id.boton_perfil);
        botonEstado = findViewById(R.id.boton_estado);
        botonAbrir = findViewById(R.id.boton_abrir);
        botonCerrar = findViewById(R.id.boton_cerrar);

        connectToMqttBroker();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        estadoRef = mDatabase.child("Gateguard").child("Usuarios");

        // Mostrar mensaje de conexión establecida
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Control.this, "Conexión establecida", Toast.LENGTH_SHORT).show();
            }
        });

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

        botonAbrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrir();
            }
        });

        botonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrar();
            }
        });
    }

    private void mostrarEstadoDialog() {
        if (isNetworkConnected()) {
            // Extraer el currentCiudadela y currentPuerta de los IntentExtras
            String currentCiudadela = getIntent().getStringExtra("nombreCiudadela");
            String currentPuerta = getIntent().getStringExtra("nombrePuerta");

            estadoRef.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // Verificar si el nodo "estado" existe antes de obtener su valor
                    String estado = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Ciudadelas")
                            .child(currentCiudadela)
                            .child(currentPuerta)
                            .child("estado").getValue(String.class);

                    Log.d("DEBUG", "Valor de estado: " + estado);

                    if (estado != null) {
                        mostrarDialogoEmergente(estado);
                    } else {
                        Log.e("DEBUG", "El valor de estado es nulo.");
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("DEBUG", "Error al leer los datos: " + databaseError.getMessage());
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

    private void connectToMqttBroker() {
        try {
            mqttClient = new MqttClient(BROKER_URL, CLIENT_ID, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            mqttClient.connect(options);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrir() {
        try {
            String topic = "puerta/control"; // Cambia el topic según tu configuración
            String mensaje = "abrir_puerta";

            MqttMessage mqttMessage = new MqttMessage(mensaje.getBytes());
            mqttClient.publish(topic, mqttMessage);

            mostrarMensajePuertaAbierta();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cerrar() {
        try {
            String topic = "puerta/control"; // Cambia el topic según tu configuración
            String mensaje = "cerrar_puerta";

            MqttMessage mqttMessage = new MqttMessage(mensaje.getBytes());
            mqttClient.publish(topic, mqttMessage);

            mostrarMensajePuertaCerrada();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarMensajePuertaAbierta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Puerta Abierta")
                .setMessage("La puerta ha sido abierta.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void mostrarMensajePuertaCerrada() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Puerta Cerrada")
                .setMessage("La puerta ha sido cerrada.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}