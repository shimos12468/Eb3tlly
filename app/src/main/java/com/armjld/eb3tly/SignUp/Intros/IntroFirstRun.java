package com.armjld.eb3tly.SignUp.Intros;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.armjld.eb3tly.Login.Login_Options;
import com.armjld.eb3tly.R;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;

public class IntroFirstRun extends AppIntro {

    @Override
    public void onBackPressed() { }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance( "اهلا بيك", "اول منصة حقيقية لربط التاجر بكابتن التوصيل في مصر", R.drawable.firstintro1, Color.parseColor("#32807e"), Color.parseColor("#ffffff"), Color.parseColor("#ffffff")));
        addSlide(AppIntroFragment.newInstance( "لو انت كابتن", "معانا هتقدر تشتغل لو معاك عجله او سكوتر او عربيه وتختار المناطق الى تريحك في اي مكان بالجمهوريه ليك شغل معانا",R.drawable.firstintro2, Color.parseColor("#32807e"), Color.parseColor("#ffffff"), Color.parseColor("#ffffff")));
        addSlide(AppIntroFragment.newInstance( "لو انت تاجر", "لو انت شركة او ويب سايت او عندك صفحة علي الفيس وبتبيع اون لاين و عندك اوردرات عايز توصلها للعملاء بتاعتك في اسرع وقت وتحصل فلوسك وانت قاعد مكانك في نفس اليوم",R.drawable.firstintro3, Color.parseColor("#32807e"), Color.parseColor("#ffffff"), Color.parseColor("#ffffff")));

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(new Intent(getApplicationContext(), Login_Options.class));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(getApplicationContext(), Login_Options.class));
    }
}