

package com.armjld.eb3tly.Utilites;

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
import android.widget.Toast;

import com.armjld.eb3tly.Notifications.Notifications;
import com.armjld.eb3tly.Orders.AddOrders;
import com.armjld.eb3tly.Orders.EditOrders;
import com.armjld.eb3tly.Orders.MapsActivity;
import com.armjld.eb3tly.Orders.OneOrder;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Settings.Wallet.MyWallet;
import com.armjld.eb3tly.Home.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import Model.Data;
import Model.UserInFormation;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
    private DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    private DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests");
    String body = "";
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    //Intent intent = new Intent(getApplicationContext(), Notifications.class);


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "From: " + remoteMessage.getFrom());
            Map<String, String> data = remoteMessage.getData();

            if(data.get("type").equals("normal")) {
                String nameFrom = data.get("title");
                String statue = data.get("body");
                String action = data.get("action");
                String OrderID = data.get("orderid");
                sendNotification(statue, nameFrom,action, OrderID);
                Log.i(TAG, "This is a normal Notification");
            } else {
                String title = data.get("title");
                String body = data.get("body");
                chatNoti(title, body);
                Log.i(TAG, "This is a Chat Notification");
            }

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

    private void chatNoti (String title, String body) {
        Intent chatintent = new Intent(this, HomeActivity.class);
        chatintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0 /* Request code */, chatintent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent2);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(0 , notificationBuilder.build());
    }

    private void sendNotification(String messageBody, String title, String action, String OrderID) {
        Intent intent = new Intent(this, Notifications.class);
        /*switch (action) {
            case "noting": {
                intent = new Intent(this, Notifications.class);
                break;
            }
            case "profile" : {
                HomeActivity.whichFrag = "Profile";
                startActivity(new Intent(this, HomeActivity.class));
                break;
            }
            case "order": {
                mDatabase.child(OrderID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if((int) snapshot.getChildrenCount() > 1) {
                            Data orderData = snapshot.getValue(Data.class);
                            assert orderData != null;
                            Log.i(TAG, orderData.getId() + " : " +orderData.getDDate());
                            if(!orderData.getStatue().equals("placed")) {
                                Toast.makeText(MyFirebaseMessagingService.this, "نعتذر, لقد تم قبول الاوردر بالفعل من مندوب اخر", Toast.LENGTH_SHORT).show();
                            } else {
                                Date orderDate = null;
                                Date myDate = null;
                                try {
                                    orderDate = format.parse(orderData.getDDate());
                                    myDate =  format.parse(format.format(Calendar.getInstance().getTime()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                assert orderDate != null;
                                assert myDate != null;
                                if(orderDate.compareTo(myDate) >= 0) {
                                    Log.i(TAG, orderData.getId());
                                    intent = new Intent(MyFirebaseMessagingService.this, OneOrder.class);
                                    intent.putExtra("oID", orderData.getId());
                                } else {
                                    Toast.makeText(MyFirebaseMessagingService.this, "معاد تسليم الاوردر قد فات", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(MyFirebaseMessagingService.this, "تم حذف هذا الاوردر", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
                break;
            }
            case "home" : {
                intent = new Intent(this, HomeActivity.class);
                break;
            }
            case "facebook" : {
                String fbLink = "https://www.facebook.com/Eb3tlyy/";
                intent = new Intent(Intent.ACTION_VIEW , Uri.parse(fbLink));
                break;
            }
            case "playstore" : {
                String psLink = "https://play.google.com/store/apps/details?id=com.armjld.eb3tly";
                intent = new Intent( Intent.ACTION_VIEW , Uri.parse(psLink));
                break;
            }
            case "add" : {
                if(UserInFormation.getAccountType().equals("Supplier")) {
                    intent = new Intent(this, AddOrders.class);
                }
                break;
            }
            case "edit" : {
                if(UserInFormation.getAccountType().equals("Supplier")) {
                    intent = new Intent(this, EditOrders.class);
                    intent.putExtra("orderid", OrderID);
                }
                break;
            }
            case "map" : {
                intent = new Intent(this, MapsActivity.class);

                break;
            }

            case "wallet" : {
                if(UserInFormation.getAccountType().equals("Delivery Worker")) {
                    intent = new Intent(this, MyWallet.class);
                }
                break;
            }

            default: {
                intent = new Intent(this, Notifications.class);
            }
        }*/

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(0 , notificationBuilder.build());
    }
}