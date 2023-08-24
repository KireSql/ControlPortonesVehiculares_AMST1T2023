//VISTAS USUARIO

package com.example.gateguard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Puertas extends AppCompatActivity {

    private ImageButton botonCerrarSesion;
    private ImageButton botonInicio;
    private ImageButton botonSoporte;
    private ImageButton botonPerfil;
    private DatabaseReference databaseReference;
    private String currentUserId; // Almacena el ID de usuario actual
    private RecyclerView recyclerViewPuertas;
    private PuertasAdapter puertasAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puertas);

        String nombreCiudadela = getIntent().getStringExtra("nombreCiudadela");

        botonCerrarSesion = findViewById(R.id.boton_salir);
        botonInicio = findViewById(R.id.boton_inicio);
        botonSoporte = findViewById(R.id.boton_soporte);
        botonPerfil = findViewById(R.id.boton_perfil);

        recyclerViewPuertas = findViewById(R.id.recyclerViewPuertas);
        recyclerViewPuertas.setLayoutManager(new LinearLayoutManager(this));
        puertasAdapter = new PuertasAdapter();
        recyclerViewPuertas.setAdapter(puertasAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("GateGuard").child("Usuarios").child(currentUserId).child("Ciudadelas").child(nombreCiudadela);
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Puertas", "onDataChange event triggered");

                puertasAdapter.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombrePuerta = snapshot.getKey();
                    Log.d("Puertas", "Nombre de puerta: " + nombrePuerta);
                    puertasAdapter.addPuertas(nombrePuerta);
                }
                puertasAdapter.notifyDataSetChanged();
                Log.d("Puertas", "notifyDataSetChanged() llamado");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Puertas", "Error en Firebase Realtime Database: " + databaseError.getMessage());
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

    public void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(this, InicioSesion.class);
        intent.putExtra("msg", "cerrarSesion");
        startActivity(intent);
    }

    public void regresarInicio() {
        Intent intent = new Intent(this, Ciudadelas.class);
        startActivity(intent);
    }

    public void soporte() {
        Intent intent = new Intent(this, Soporte.class);
        startActivity(intent);
    }

    public void perfil() {
        Intent intent = new Intent(this, PerfilUsuario.class);
        startActivity(intent);
    }

    class PuertasAdapter extends RecyclerView.Adapter<PuertasAdapter.ViewHolder> {

        private List<String> nombresPuertas = new ArrayList<>();

        public void addPuertas(String nombre) {
            nombresPuertas.add(nombre);
            notifyDataSetChanged();
        }

        public void clear() {
            nombresPuertas.clear();
            notifyDataSetChanged();
        }

        @Override
        public PuertasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disenio_opciones, parent, false);
            return new PuertasAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PuertasAdapter.ViewHolder holder, int position) {
            String nombrePuerta = nombresPuertas.get(position);
            holder.nombreButton.setText(nombrePuerta);

            holder.nombreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Acción al hacer clic en el texto del elemento
                    Intent intent = new Intent(v.getContext(), Control.class);
                    intent.putExtra("nombreCiudadela", getIntent().getStringExtra("nombreCiudadela")); // Envía el nombre de la ciudadela
                    intent.putExtra("nombrePuerta", nombrePuerta); // Envía el nombre de la puerta
                    v.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return nombresPuertas.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            Button nombreButton;

            public ViewHolder(View itemView) {
                super(itemView);
                nombreButton = itemView.findViewById(R.id.nombreButton);
            }
        }
    }
}
