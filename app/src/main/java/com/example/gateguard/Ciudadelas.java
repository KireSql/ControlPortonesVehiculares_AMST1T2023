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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Ciudadelas extends AppCompatActivity {

    private ImageButton botonCerrarSesion;
    private ImageButton botonInicio;
    private ImageButton botonSoporte;
    private ImageButton botonPerfil;
    private DatabaseReference databaseReference;
    private String currentUserId; // Almacena el ID de usuario actual
    private RecyclerView recyclerViewCiudadelas;
    private CiudadelasAdapter ciudadelasAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ciudadelas);

        botonCerrarSesion = findViewById(R.id.boton_salir);
        botonInicio = findViewById(R.id.boton_inicio);
        botonSoporte = findViewById(R.id.boton_soporte);
        botonPerfil = findViewById(R.id.boton_perfil);

        recyclerViewCiudadelas = findViewById(R.id.recyclerViewCiudadelas);
        recyclerViewCiudadelas.setLayoutManager(new LinearLayoutManager(this));
        ciudadelasAdapter = new CiudadelasAdapter();
        recyclerViewCiudadelas.setAdapter(ciudadelasAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("GateGuard").child("Usuarios").child(currentUserId).child("Ciudadelas");
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Ciudadelas", "onDataChange event triggered");

                ciudadelasAdapter.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombreCiudadela = snapshot.getKey();
                    Log.d("Ciudadelas", "Nombre de ciudadela: " + nombreCiudadela);
                    ciudadelasAdapter.addNombreCiudadela(nombreCiudadela);
                }
                ciudadelasAdapter.notifyDataSetChanged();
                Log.d("Ciudadelas", "notifyDataSetChanged() llamado");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Ciudadelas", "Error en Firebase Realtime Database: " + databaseError.getMessage());
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
        Intent intent = new Intent(Ciudadelas.this, InicioSesion.class);
        intent.putExtra("msg", "cerrarSesion");
        startActivity(intent);
    }

    public void regresarInicio() {
        Intent intent = new Intent(Ciudadelas.this, Ciudadelas.class);
        startActivity(intent);
    }

    public void soporte() {
        Intent intent = new Intent(Ciudadelas.this, Soporte.class);
        startActivity(intent);
    }

    public void perfil() {
        Intent intent = new Intent(Ciudadelas.this, PerfilUsuario.class);
        startActivity(intent);
    }

    class CiudadelasAdapter extends RecyclerView.Adapter<CiudadelasAdapter.ViewHolder> {

        private List<String> nombresCiudadelas = new ArrayList<>();

        public void addNombreCiudadela(String nombre) {
            nombresCiudadelas.add(nombre);
            notifyDataSetChanged();
        }

        public void clear() {
            nombresCiudadelas.clear();
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disenio_opciones, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String nombreCiudadela = nombresCiudadelas.get(position);
            holder.nombreButton.setText(nombreCiudadela);

            holder.nombreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Acción al hacer clic en el texto del elemento
                    Intent intent = new Intent(v.getContext(), Puertas.class);
                    intent.putExtra("nombreCiudadela", nombreCiudadela); // Envía el nombre de la ciudadela
                    v.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return nombresCiudadelas.size();
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
