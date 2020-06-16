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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroFragment.newInstance( "سهولة اختيار الاوردرات", " سيظهر لك جميع الاوردرات المتاحة و مواعيد التسليم الخاصة بها ومقدم الشحن واجر التوصيل حيث يتاح لك قبول اي اوردر مناسبا لك",R.drawable.ic_intro1 ));
        addSlide(AppIntroFragment.newInstance( "نظام التقييم", "حيث يمكنك ان تري تقييم الموزع قبل قبول الاوردر ومصدقيتة في العمل وتذكر دائما يمكنك الابلاغ اذا واجهت اي مشكله مع الموزع ",R.drawable.ic_intro2));
        addSlide(AppIntroFragment.newInstance( "تصفية الاوردرات", "بدلاً من ظهور جميع الاوردرات يمكنك انا تحدد الاوردرات القرييبة منك فقط ",R.drawable.ic_intro3));
        addSlide(AppIntroFragment.newInstance( "الامان", "لامان قم بأستلام الاوردر من محل العمل او بيت العميل وليس من الطريق للتجنب اي احتيال او مشاكل ",R.drawable.ic_alert));
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