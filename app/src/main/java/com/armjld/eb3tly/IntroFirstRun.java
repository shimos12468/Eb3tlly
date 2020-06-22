package com.armjld.eb3tly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;

public class IntroFirstRun extends AppIntro {

    @Override
    public void onBackPressed() { }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroFragment.newInstance( "اهلا بيك", "بنرحب بيك في ابلكيشن ابعتلي, اول ابلكيشن في مصر بيوصل مندوبين الشحن المقدم بالتجار",R.drawable.firstintro1));
        addSlide(AppIntroFragment.newInstance( "عايز تكسب ؟", "لو انت مندوب شحن او حتي شخص بيحاول يكسب فلوس من المشاوير الي بيعملها كل يوم, تقدر تسجل في الابلكيشن و تختار الاوردرات الي في سكتك و توصلها معاك.",R.drawable.firstintro2));
        addSlide(AppIntroFragment.newInstance( "عايز شغلك يوصل ؟", "لو انت تاجر و محتاج اوردرك يوصل في معادو, تقدر تنزل الاوردر بتاعك و واحد من المندوبين هيكلمك و يجي يستلمو منك",R.drawable.firstintro3));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}