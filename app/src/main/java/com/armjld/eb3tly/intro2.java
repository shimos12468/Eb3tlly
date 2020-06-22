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

public class intro2 extends AppIntro {

    @Override
    public void onBackPressed() { }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroFragment.newInstance( "سهولة اختيار الاوردرات", " نساعدك علي ان تختار الاوردرات ذات المقدم الذي تحددة, و يمكنك بسهولة اختيار الاوردرات التي تناسب وجهتك",R.drawable.ic_intro1));
        addSlide(AppIntroFragment.newInstance( "الشفافية", "يتميز برنامج ابعتلي بامكانيه تقييمك لمعاملتك مع التاجر, و التعليق علي المشاكل التي واجهتها في المعاملة ",R.drawable.ic_intro2));
        addSlide(AppIntroFragment.newInstance( "تصفية الاوردرات", "بدلاً من ظهور جميع الاوردرات يمكنك انا تحدد الاوردرات القرييبة منك فقط ",R.drawable.ic_intro3));
        addSlide(AppIntroFragment.newInstance( "الامان", "لامان قم بأستلام الاوردر من مكان او بيت التاجر لضمان عدم حدوث اي مشاكل ",R.drawable.ic_alert));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(new Intent(getApplicationContext(), profile.class));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(getApplicationContext(), profile.class));
    }
}