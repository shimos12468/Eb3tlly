package com.armjld.eb3tly.Intros;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.armjld.eb3tly.Profiles.*;
import com.armjld.eb3tly.Utilites.HowTo;
import com.armjld.eb3tly.main.MainActivity;
import com.armjld.eb3tly.R;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.google.firebase.auth.FirebaseAuth;

public class introSup extends AppIntro {

    @Override
    public void onBackPressed() { }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroFragment.newInstance( "أضافة الاوردرات بسهولة", "اذا كان لديك اوردر تريد ارسالة فقط قم باضافتة وسوف يظهر لمندوبين الشحن القريبين منك وسيتواصلون معك في اقرب وقت  ", R.drawable.ic_intro4, Color.parseColor("#FF0099CC")));
        addSlide(AppIntroFragment.newInstance( "الشفافية", "حيث يمكنك ان تري تقييم المندوب الذي سيقوم باستلام اوردرك, و تجارب التجار الاخرين معه.",R.drawable.ic_intro2, Color.parseColor("#FF0099CC")));
        addSlide(AppIntroFragment.newInstance( "المتابعة", "يمكنك متابعة الاوردر الخاص بك الي ان يصل الي العميل",R.drawable.ic_intro5, Color.parseColor("#FF0099CC")));
        addSlide(AppIntroFragment.newInstance( "الامان", "يجب استلام مقدم الشحن بالكامل من المندوب و تسليمة من محل السكن او العمل ضمانا لحق كلا الطرفين",R.drawable.ic_alert, Color.parseColor("#FF0099CC")));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), supplierProfile.class));
        } else {
            Toast.makeText(this, "االرجاء تسجيل الدخول مجددا ..", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), supplierProfile.class));
        } else {
            Toast.makeText(this, "االرجاء تسجيل الدخول مجددا ..", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }
}