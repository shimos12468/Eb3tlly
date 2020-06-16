package com.armjld.eb3tly;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class introSup extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // sup
        addSlide(AppIntroFragment.newInstance( "أضافة الاوردرات بسهولة", "اذا كان لديك اوردر تريد ارسالة فقط قم باضافتة وسوف يظهر لمندوبين الشحن القريبين منك وسيتواصلون معك في اقرب وقت  ",R.drawable.ic_intro4));
        addSlide(AppIntroFragment.newInstance( "نظام التقييم", "حيث يمكنك ان تري تقييم المندوب الذي سيقوم باستلام اوردرك وتذكر دائما يمكنك الابلاغ اذا واجهت اي مشكلة مع المندوب",R.drawable.ic_intro2));
        addSlide(AppIntroFragment.newInstance( "المتابعة", "يمكنك متابعة الاوردر الخاص بك الي ان يصل الي العميل  ",R.drawable.ic_intro5));
        addSlide(AppIntroFragment.newInstance( "الامان", "عند وصول المندوب اليك تذكر ان تراجع الرقم القومي وتطابقة مع الرقم القومي الموجود في حسابة للتجنب اي احتيال او مشاكل ",R.drawable.ic_alert));

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uType = snapshot.child("accountType").getValue().toString();
                if (uType.equals("Supplier")) {
                    startActivity(new Intent(getApplicationContext(), profile.class));
                } else if (uType.equals("Delivery Worker")) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uType = snapshot.child("accountType").getValue().toString();
                if (uType.equals("Supplier")) {
                    startActivity(new Intent(getApplicationContext(), profile.class));
                } else if (uType.equals("Delivery Worker")) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}