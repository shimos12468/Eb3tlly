package com.armjld.eb3tly.Intros;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.armjld.eb3tly.main.HomeActivity;
import com.armjld.eb3tly.R;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;

public class intro2 extends AppIntro {

    @Override
    public void onBackPressed() { }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroFragment.newInstance( "سهولة اختيار الاوردرات", " نساعدك علي ان تختار الاوردرات ذات المقدم الذي تحددة, و يمكنك بسهولة اختيار الاوردرات التي تناسب وجهتك", R.drawable.ic_intro1, Color.parseColor("#FF0099CC")));
        addSlide(AppIntroFragment.newInstance( "الشفافية", "يتميز برنامج ابعتلي بامكانيه تقييمك لمعاملتك مع التاجر, و التعليق علي المشاكل التي واجهتها في المعاملة ",R.drawable.ic_intro2, Color.parseColor("#FF0099CC")));
        addSlide(AppIntroFragment.newInstance( "تصفية الاوردرات", "بدلاً من ظهور جميع الاوردرات يمكنك انا تحدد الاوردرات القرييبة منك فقط ",R.drawable.ic_intro3, Color.parseColor("#FF0099CC")));
        addSlide(AppIntroFragment.newInstance( "الامان", "لامان قم بأستلام الاوردر من مكان او بيت التاجر لضمان عدم حدوث اي مشاكل ",R.drawable.ic_alert, Color.parseColor("#FF0099CC")));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }
}