package Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseInstanceService extends FirebaseMessagingService {

    private FirebaseAuth mAuth;
    private DatabaseReference userReference;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            userReference = FirebaseDatabase.getInstance().getReference()
                    .child("GateGuard")
                    .child("Usuarios")
                    .child(mAuth.getCurrentUser().getUid())
                    .child("Ciudadelas");
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("TOKENFIREBASE", s);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody());
    }
    private void showNotification(String title, String body) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "gate_guard_notifications";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                            NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("EDMT Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("info");

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        if (userReference != null) {
            userReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot usuarioSnapshot, String previousChildName) {
                    // Iteramos a través de las ciudadelas del usuario
                    for (DataSnapshot ciudadelasSnapshot : usuarioSnapshot.getChildren()) {
                        String nombreCiudadelas = ciudadelasSnapshot.getKey();
                        // Iteramos a través de las puertas dentro de la ciudadelas
                        for (DataSnapshot nombrePuertaSnapshot : ciudadelasSnapshot.getChildren()) {
                            String nombrePuerta = nombrePuertaSnapshot.getKey();
                            // Ahora, accedemos al nodo de la puerta y establecemos el listener para el estado
                            DatabaseReference puertaReference = nombrePuertaSnapshot.child("estado").getRef();
                            puertaReference.addValueEventListener(new ValueEventListener() {
                                private String estadoAnterior = null;

                                @Override
                                public void onDataChange(@NonNull DataSnapshot puertaSnapshot) {
                                    String nuevoEstado = puertaSnapshot.getValue(String.class);

                                    // Comparamos el nuevo estado con el estado anterior
                                    if (estadoAnterior != null && !estadoAnterior.equals(nuevoEstado)) {
                                        // Aquí puedes enviar una notificación porque el estado cambió
                                        showNotification("Estado Cambiado", "La puerta " + nombrePuerta + " en " + nombreCiudadelas + " ahora está " + nuevoEstado);
                                    }

                                    estadoAnterior = nuevoEstado; // Actualizamos el estado anterior
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Manejamos los errores si es necesario
                                    Log.e("Firebase Error", "Error al leer el estado de la puerta " + nombrePuerta, error.toException());
                                }
                            });
                        }
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                    String nombreCiudadelas = snapshot.getKey();  // Nombre de la ciudadelas
                    Log.d("ChildChanged", "Nodo hijo cambiado: " + nombreCiudadelas);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    Log.d("ChildRemoved", "Nodo hijo eliminado ");
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                    String nombreCiudadelas = snapshot.getKey();  // Nombre de la ciudadelas
                    Log.d("ChildMoved", "Nodo hijo movido: " + nombreCiudadelas);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase Error", "Error en operación: " + error.getMessage());
                }
            });
        }
    }
}