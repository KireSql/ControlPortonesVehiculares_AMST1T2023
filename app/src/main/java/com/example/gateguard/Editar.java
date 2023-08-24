//VISTAS USUARIO

package com.example.gateguard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Editar extends AppCompatActivity {

    private EditText nombreEditText;
    private TextView correoTextView;
    private TextView idUsuarioTextView;
    private ImageView fotoImageView;
    private Button guardarButton;
    private Button cancelarButton;
    private TextView passwordTextView;
    private String userId;
    private ImageButton botonCerrarSesion;
    private ImageButton botonInicio;
    private ImageButton botonSoporte;
    private ImageButton botonPerfil;
    private ImageButton botonHistorial;
    private FirebaseUser user;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private static final String PROFILE_IMAGES_PATH = "profile_images";
    private Uri imageUri;
    private static final int GALLERY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        botonCerrarSesion = findViewById(R.id.boton_salir);
        botonInicio = findViewById(R.id.boton_inicio);
        botonSoporte = findViewById(R.id.boton_soporte);
        botonPerfil = findViewById(R.id.boton_perfil);

        nombreEditText = findViewById(R.id.txt_nombre);
        correoTextView = findViewById(R.id.txt_correo);
        fotoImageView = findViewById(R.id.imv_foto);
        idUsuarioTextView = findViewById(R.id.txt_userId);
        guardarButton = findViewById(R.id.boton_guardar);
        cancelarButton = findViewById(R.id.boton_cancelar);
        passwordTextView = findViewById(R.id.txt_password);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Obtener el usuario actual de Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();

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
                    // Obtener el nombre de usuario actual
                    String nombreUsuarioActual = user.getDisplayName();
                    nombreEditText.setText(nombreUsuarioActual);

                    // Obtener el correo electrónico actual
                    String correoActual = user.getEmail();
                    correoTextView.setText(correoActual);

                    // Obtener la foto de perfil actual desde Firebase Storage y mostrarla en el ImageView
                    String fotoPerfilActual = user.getPhotoUrl().toString();
                    Picasso.get()
                            .load(fotoPerfilActual)
                            .resize(300, 300) // Establecer el tamaño deseado
                            .centerCrop() // Recortar la imagen para ajustarla al tamaño especificado
                            .into(fotoImageView);
                } else if (providerId.equals("password")) {
                    if (user.getProviderData().size() > 2) {
                        // Obtener el nombre de usuario actual
                        String nombreUsuarioActual = user.getDisplayName();
                        nombreEditText.setText(nombreUsuarioActual);

                        // Obtener el correo electrónico actual
                        String correoActual = user.getEmail();
                        correoTextView.setText(correoActual);

                        // Obtener la foto de perfil actual desde Firebase Storage y mostrarla en el ImageView
                        String fotoPerfilActual = user.getPhotoUrl().toString();
                        Picasso.get()
                                .load(fotoPerfilActual)
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
                            nombreEditText.setText(username);
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

        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCambios();
            }
        });

        cancelarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelar();
            }
        });

        passwordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarPassword();
            }
        });

        fotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFoto();
            }
        });
    }

    private void guardarCambios() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String nuevoNombreUsuario = nombreEditText.getText().toString().trim();

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nuevoNombreUsuario)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(profileTask -> {
                        if (profileTask.isSuccessful()) {
                            StorageReference imageRef = storageReference.child(PROFILE_IMAGES_PATH).child(userId + ".jpg");

                            UploadTask uploadTask = imageRef.putFile(imageUri);
                            uploadTask.addOnSuccessListener(taskSnapshot -> {
                                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String photoUrl = uri.toString();
                                    databaseReference.child("users").child(userId).child("photoUrl").setValue(photoUrl)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(Editar.this, "Cambios guardados", Toast.LENGTH_SHORT).show();
                                                cancelar();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(Editar.this, "Error al actualizar la foto de perfil", Toast.LENGTH_SHORT).show();
                                            });
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(Editar.this, "Error al obtener la URL de descarga de la imagen", Toast.LENGTH_SHORT).show();
                                });
                            }).addOnFailureListener(e -> {
                                Toast.makeText(Editar.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Toast.makeText(Editar.this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void cancelar() {
        Intent intent = new Intent(Editar.this, PerfilUsuario.class);
        startActivity(intent);
        finish();
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
        Intent intent = new Intent(Editar.this, Ciudadelas.class);
        startActivity(intent);
    }

    public void soporte() {
        // Redirigir a la actividad Soporte
        Intent intent = new Intent(Editar.this, Soporte.class);
        startActivity(intent);
    }

    public void perfil() {
        // Redirigir a la actividad Perfil Usuario
        Intent intent = new Intent(Editar.this, PerfilUsuario.class);
        startActivity(intent);
    }

    private void cambiarPassword() {
        Intent intent = new Intent(Editar.this, CambiarPassword.class);
        startActivity(intent);
    }

    private void seleccionarFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Obtenemos la imagen seleccionada de la galería
            Uri imageUri = data.getData();

            if (user != null) {
                // Crear una referencia única para la imagen en Firebase Storage
                StorageReference imageRef = storageReference.child(PROFILE_IMAGES_PATH).child(userId + ".jpg");

                // Subir la imagen al Firebase Storage
                UploadTask uploadTask = imageRef.putFile(imageUri);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Obtener la URL de descarga de la imagen
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    // Actualizar la foto de perfil en Firebase Authentication
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(downloadUri)
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(profileTask -> {
                                                if (profileTask.isSuccessful()) {
                                                    // Mostrar la nueva foto de perfil en el ImageView
                                                    Picasso.get().load(downloadUri).resize(300, 300) // Establecer el tamaño deseado
                                                            .centerCrop().into(fotoImageView);
                                                    Toast.makeText(Editar.this, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                                                    //cancelar();

                                                    // Guardar la URL de la foto de perfil en Firebase Realtime Database
                                                    String photoUrl = downloadUri.toString();
                                                    databaseReference.child("users").child(userId).child("photoUrl").setValue(photoUrl);
                                                } else {
                                                    Toast.makeText(Editar.this, "Error al actualizar la foto de perfil", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    // Actualizar la foto de perfil en el ImageView
                                    Picasso.get().load(downloadUri).resize(300, 300) // Establece el tamaño deseado
                                            .centerCrop().into(fotoImageView);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Editar.this, "Error al obtener la URL de descarga de la imagen", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(Editar.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

}