//VISTAS ADMINISTRADOR

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

public class EditarCiudadelas extends AppCompatActivity {

    private ImageButton botonCerrarSesion;
    private ImageButton botonInicio;
    private ImageButton botonMensajes;

    private ImageButton botonPerfil;
    private ImageButton botonUsuarios;
    private ImageButton botonAgregar;
    private DatabaseReference databaseReference;
    private String currentUserId; // Almacena el ID de usuario actual
    private RecyclerView recyclerViewCiudadelas;
    private CiudadelasAdapter ciudadelasAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_ciudadelas);

        botonCerrarSesion = findViewById(R.id.boton_salir);
        botonInicio = findViewById(R.id.boton_inicio);
        botonMensajes = findViewById(R.id.boton_mensajes);
        botonPerfil = findViewById(R.id.boton_perfil);
        botonUsuarios = findViewById(R.id.boton_usuarios);
        botonAgregar = findViewById(R.id.boton_agregar);

        recyclerViewCiudadelas = findViewById(R.id.recyclerViewCiudadelas);
        recyclerViewCiudadelas.setLayoutManager(new LinearLayoutManager(this));
        ciudadelasAdapter = new CiudadelasAdapter();
        recyclerViewCiudadelas.setAdapter(ciudadelasAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("GateGuard").child("Usuarios");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Ciudadelas", "onDataChange event triggered");

                ciudadelasAdapter.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    currentUserId = userSnapshot.getKey();
                    DatabaseReference userCiudadelasRef = userSnapshot.child("Ciudadelas").getRef();

                    userCiudadelasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot userCiudadelasSnapshot) {
                            for (DataSnapshot snapshot : userCiudadelasSnapshot.getChildren()) {
                                String nombreCiudadela = snapshot.getKey();
                                Log.d("Ciudadelas", "Nombre de ciudadela: " + nombreCiudadela);
                                ciudadelasAdapter.addNombreCiudadela(nombreCiudadela);
                            }
                            ciudadelasAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("Ciudadelas", "Error en Firebase Realtime Database: " + databaseError.getMessage());
                        }
                    });
                }
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

        botonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregar();
            }
        });

    }

    public void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        finish();
        // Redirigir a la actividad InicioSesion
        Intent intent = new Intent(EditarCiudadelas.this, InicioSesion.class);
        intent.putExtra("msg", "cerrarSesion");
        startActivity(intent);
    }

    public void regresarInicio() {
        // Redirigir a la actividad Ciudadelas
        Intent intent = new Intent(EditarCiudadelas.this, EditarCiudadelas.class);
        startActivity(intent);
    }

    public void mensajes() {
        // Redirigir a la actividad Soporte
        Intent intent = new Intent(EditarCiudadelas.this, Mensajes.class);
        startActivity(intent);
    }

    public void perfil() {
        // Redirigir a la actividad Perfil Usuario
        Intent intent = new Intent(EditarCiudadelas.this, PerfilUsuario.class);
        startActivity(intent);
    }

    public void usuarios() {
        // Redirigir a la actividad Ciudadelas
        Intent intent = new Intent(EditarCiudadelas.this, ListaUsuarios.class);
        startActivity(intent);
    }

    public void agregar() {
        // Redirigir a la actividad Ciudadelas
        Intent intent = new Intent(EditarCiudadelas.this, RegistroCiudadela.class);
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
        public CiudadelasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disenio_opciones, parent, false);
            return new CiudadelasAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CiudadelasAdapter.ViewHolder holder, int position) {
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