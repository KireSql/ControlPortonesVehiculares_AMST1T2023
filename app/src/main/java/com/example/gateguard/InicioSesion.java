package com.example.gateguard;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.HashMap;

public class InicioSesion extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button iniciarSesionButton;
    private CheckBox showPasswordCheckBox;
    private TextView olvidarPasswordTextView;

    private FirebaseAuth mAuth;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verifica si el usuario ya ha iniciado sesión previamente
        if (isLoggedIn()) {
            // Redirige directamente a la pantalla principal
            goToMainActivity();
            return;
        }

        setContentView(R.layout.activity_inicio_sesion);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        iniciarSesionButton = findViewById(R.id.boton_inicioSesion);
        showPasswordCheckBox = findViewById(R.id.checkBoxShowPassword);
        olvidarPasswordTextView = findViewById(R.id.olvidarPassword);

        mAuth = FirebaseAuth.getInstance();



        iniciarSesionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCamposInicioSesion();
            }
        });

        olvidarPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                olvidarPassword(v);
            }
        });

        showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Mostrar contraseña
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // Ocultar contraseña
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent intent = getIntent();
        String msg = intent.getStringExtra("msg");
        if(msg != null){
            if(msg.equals("cerrarSesion")){
                cerrarSesion();
            }
        }
    }

    private void validarCamposInicioSesion() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Por favor ingrese su correo y contraseña", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Por favor ingrese su contraseña", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Por favor ingrese su correo", Toast.LENGTH_SHORT).show();
        } else {
            // Verificar la conexión a Internet antes de iniciar sesión
            if (isNetworkAvailable()) {
                // Verificar si el correo es "maria.fern.14@gmail.com"
                if (email.equals("maria.fern.14@gmail.com")) {
                    // Redirigir a la vista EditarCiudadelas
                    Intent intent = new Intent(InicioSesion.this, EditarCiudadelas.class);
                    startActivity(intent);
                } else {
                    iniciarSesion();
                }
            } else {
                Toast.makeText(getApplicationContext(), "No hay conexión a internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void iniciarSesion() {
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        // Verificar si el usuario existe en Firebase
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            if (result != null && result.getSignInMethods() != null && result.getSignInMethods().isEmpty()) {
                                // El usuario no está registrado en Firebase
                                Toast.makeText(getApplicationContext(), "Por favor revise que sus credenciales sean correctas", Toast.LENGTH_SHORT).show();
                            } else {
                                // El usuario está registrado, intentar iniciar sesión
                                mAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    if (user != null) {
                                                        boolean isFirstTime = isFirstTimeUser(); // Verificar si es la primera vez del usuario
                                                        if (isFirstTime) {
                                                            // Es la primera vez que inicia sesión, redirigir a la pestaña "InicioSesionGoogle"
                                                            Intent intent = new Intent(InicioSesion.this, AutenticacionCuenta.class);
                                                            startActivity(intent);
                                                        } else {
                                                            if (!password.equals("123456")) {
                                                                Intent intent = new Intent(InicioSesion.this, Ciudadelas.class);
                                                                startActivity(intent);
                                                            } else {
                                                                // La contraseña ingresada es "123456", mostrar un mensaje de error o realizar otra acción
                                                                Toast.makeText(getApplicationContext(), "Estimado usuario, cambie la contraseña", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(InicioSesion.this, AutenticacionCuenta.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    // El inicio de sesión falló, muestra un mensaje de error
                                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                                        // La contraseña es incorrecta
                                                        Toast.makeText(getApplicationContext(), "Por favor revise que sus credenciales sean correctas", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        // Otro tipo de error en el inicio de sesión
                                                        Toast.makeText(getApplicationContext(), "Error en el inicio de sesión", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                            }
                        } else {
                            // Error al verificar la existencia del usuario
                            Toast.makeText(getApplicationContext(), "Error en el inicio de sesión", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void iniciarSesionGoogle(View view) {
        if (isNetworkAvailable()) {
            resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));
        } else {
            Toast.makeText(getApplicationContext(), "No hay conexión a internet", Toast.LENGTH_SHORT).show();
        }
    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null){
                        String email = account.getEmail();
                        Log.d("TAG", "Correo electrónico de GoogleSignInAccount: " + email);  // Declaración de registro agregada
                        checkIfEmailExists(email, account);
                    }
                } catch (ApiException e) {
                    Log.w("TAG", "Fallo el inicio de sesión con google.", e);
                }
            }
        }
    });

    private void checkIfEmailExists(String email, GoogleSignInAccount account) {
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            if (result != null && result.getSignInMethods() != null && !result.getSignInMethods().isEmpty()) {
                                // El correo electrónico ya está registrado en Firebase
                                firebaseAuthWithGoogle(account);
                            } else {
                                // El correo electrónico no está registrado en Firebase
                                Toast.makeText(getApplicationContext(), "El correo electrónico no está registrado", Toast.LENGTH_SHORT).show();
                                // Aquí puedes mostrar un mensaje de error adicional o realizar otra acción
                            }
                        } else {
                            // Error al verificar el correo electrónico en Firebase
                            Log.e("TAG", "Error al verificar el correo electrónico en Firebase", task.getException());
                            Toast.makeText(getApplicationContext(), "Error al verificar el correo electrónico", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        if (acct != null) {
            Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.e("TAG", "Error al autenticar con Firebase mediante Google", task.getException());
                            updateUI(null);
                        }
                    });
        } else {
            Log.e("TAG", "GoogleSignInAccount es nulo");
            updateUI(null);
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String email = user.getEmail();
            checkIfEmailExists(email, null);
            HashMap<String, String> info_user = new HashMap<String, String>();
            info_user.put("user_name", user.getDisplayName());
            info_user.put("user_email", user.getEmail());
            info_user.put("user_photo", String.valueOf(user.getPhotoUrl()));
            info_user.put("user_id", user.getUid());
            info_user.put("user_phone", user.getPhoneNumber());
            saveLoggedInStatus(true);

            if (user.getProviderData().size() > 2) {
                Intent intent = new Intent(this, Ciudadelas.class);
                intent.putExtra("info_user", info_user);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, AutenticacionCuenta.class);
                //intent.putExtra("info_user", info_user);
                startActivity(intent);
            }
            finish();

        } else {
            Log.e("TAG", "El usuario de Firebase es nulo");
        }
    }

    public void olvidarPassword(View view) {
        Intent intent = new Intent(InicioSesion.this, RecuperarPassword.class);
        startActivity(intent);
    }

    private void cerrarSesion() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> updateUI(null));
        saveLoggedInStatus(false);
    }

    private boolean isFirstTimeUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("isFirstTime", true);
        if (isFirstTime) {
            // Si es la primera vez, marcar como no es la primera vez para futuros inicios de sesión
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();
        }
        return isFirstTime;
    }

    private boolean isLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void saveLoggedInStatus(boolean isLoggedIn) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, Ciudadelas.class);
        startActivity(intent);
        finish(); // Finaliza la actividad actual para evitar que el usuario vuelva a la pantalla de inicio de sesión
    }
}