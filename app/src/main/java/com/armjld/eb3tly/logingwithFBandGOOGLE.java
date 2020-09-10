package com.armjld.eb3tly;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;

public class logingwithFBandGOOGLE {

    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;
    public logingwithFBandGOOGLE() {
        firebaseAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(FacebookSdk.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }
}
