

package com.armjld.eb3tly;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.Map;
import java.util.Objects;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
    private DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    private DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests");
    String body = "";
    String nameFrom = "";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "From: " + remoteMessage.getFrom());
            Map<String, String> data = remoteMessage.getData();
            String title = data.get("title");
            String statue = data.get("statue");
            String OrderID = data.get("orderid");
            String sendby = data.get("sendby");
            String To = data.get("sendto");
            Log.i(TAG, "Check This : " + statue + OrderID + sendby + To);
            uDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    nameFrom = Objects.requireNonNull(snapshot.child(sendby).child("name").getValue()).toString();
                    String ToType = Objects.requireNonNull(snapshot.child(To).child("accountType").getValue()).toString();
                    mDatabase.child(OrderID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String orderTo = Objects.requireNonNull(snapshot.child("dname").getValue()).toString();
                            assert statue != null;
                            switch (statue) {
                                case "edited": {
                                    body = " قام " + nameFrom + " بتعديل بعض بيانات الاوردر الذي قبلته ";
                                    break;
                                }
                                case "deleted": {
                                    if (ToType.equals("Supplier")) {
                                        body = " قام " + nameFrom + " بالغاء الاوردر " + orderTo + " الذي قام بقبولة ";
                                    } else {
                                        body = " قام " + nameFrom + " بالغاء الاوردر ";
                                    }
                                    break;
                                }
                                case "delivered": {
                                    body = " قام " + nameFrom + " بتوصيل اوردر " + orderTo;
                                    break;
                                }
                                case "accepted": {
                                    body = " قام " + nameFrom + " بقبول اوردر " + orderTo;
                                    break;
                                }
                                case "recived": {
                                    body = "قام" + nameFrom + " بتسليمك الاوردر";
                                    break;
                                }
                                case "welcome": {
                                    body = "اهلا بيك في برنامج ابعتلي, اول منصة مهمتها توصيل التاجر بمندوب الشحن";
                                    break;
                                }
                                default: {
                                    body = statue;
                                    break;
                                }
                            }
                            sendNotification(body,nameFrom);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

            if (true) {
                scheduleJob();
            } else {
                handleNow();
            }
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void scheduleJob() {
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class).build();
        WorkManager.getInstance().beginWith(work).enqueue();
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void sendRegistrationToServer(String token) {

    }

    private void sendNotification(String messageBody, String title) {

        Intent intent = new Intent(this, Notifications.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}