//VISTAS USUARIO

package com.example.gateguard;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CambiarPassword extends AppCompatActivity {
    private EditText passwordViejo;
    private EditText passwordNuevo;
    private EditText passwordNuevo2;
    private CheckBox checkBoxShowPassword1;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_password);

        mAuth = FirebaseAuth.getInstance();

        passwordViejo = findViewById(R.id.passwordViejo);
        passwordNuevo = findViewById(R.id.passwordNuevo);
        passwordNuevo2 = findViewById(R.id.passwordNuevo2);
        checkBoxShowPassword1 = findViewById(R.id.checkBoxShowPassword1);

        Button botonAceptar = findViewById(R.id.boton_aceptar);
        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = passwordViejo.getText().toString().trim();
                String newPassword1 = passwordNuevo.getText().toString().trim();
                String newPassword2 = passwordNuevo2.getText().toString().trim();

                if (oldPassword.isEmpty() || newPassword1.isEmpty() || newPassword2.isEmpty()) {
                    showToast("Por favor, complete todos los campos.");
                } else {
                    // Validar la contraseña actual y actualizar la contraseña en Firebase
                    validatePasswordAndUpdate(oldPassword, newPassword1, newPassword2);
                }
            }
        });

        Button botonCancelar = findViewById(R.id.boton_cancelar);
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void validatePasswordAndUpdate(String oldPassword, String newPassword1, String newPassword2) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (isPasswordValid(newPassword1)) { // Verificar complejidad de la nueva contraseña
                            if (!newPassword1.equals(oldPassword)) { // Verificar que la nueva contraseña no sea igual a la actual
                                if (newPassword1.equals(newPassword2)) {
                                    updatePasswordInFirebase(newPassword1);
                                } else {
                                    showToast("Las nuevas contraseñas no coinciden.");
                                }
                            } else {
                                showToast("La nueva contraseña no puede ser igual a la actual.");
                            }
                        } else {
                            showToast("La nueva contraseña debe contener al menos un carácter especial, un número, una mayúscula y una minúscula, y tener al menos 8 caracteres.");
                        }
                    } else {
                        showToast("La contraseña actual es incorrecta");
                    }
                }
            });
        }
    }

    private boolean isPasswordValid(String password) {
        // Validar la complejidad de la contraseña (caracteres especiales, números, mayúsculas y minúsculas)
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!¡¿?¡!¿?_\\-\\.\\,<>\\(\\)\\[\\]\\{\\}\\\\/\\|\\*:;\"']).{8,}$";
        return password.matches(passwordPattern);
    }

    private void updatePasswordInFirebase(String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Lógica para actualizar la contraseña del usuario en Firebase Authentication
            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        showToast("Contraseña actualizada correctamente.");
                        finish();
                    } else {
                        showToast("No se pudo actualizar la contraseña.");
                    }
                }
            });
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
