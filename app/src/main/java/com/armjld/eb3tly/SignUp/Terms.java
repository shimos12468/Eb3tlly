package com.armjld.eb3tly.SignUp;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Login.MainActivity;

public class Terms extends AppCompatActivity {
    private TextView txtTerms;
    private String privcyCode;
    private ImageView btnBack;

    @Override
    public void onBackPressed() {
        finish();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        txtTerms = findViewById(R.id.txtTerms);
        btnBack = findViewById(R.id.btnBack);
        txtTerms.setMovementMethod(new ScrollingMovementMethod());
        btnBack.setOnClickListener(v-> finish());
        privcyCode = "<p>سياسة الخصوصية</p>\n" +
                "<p><br></p>\n" +
                "التعامل علي الابكيشن علي مسؤليتك الخاصه\n" +
                "\n" +
                "و اتبع خطوات السلامة في التعامل مع الطرف الاخر\n" +
                "\n" +
                "مندوب الشحن يتسلم الاوردرات من محل سكن التاجر او مكتبة فقط\n" +
                "\n" +
                "و يقوم التاجر بتحصيل مقدم الاوردر قبل تسليم الاوردر للمندوب\n" +
                "\n" +
                "و ذلك حرصا علي ضمان حق كلا الطرفين\n" +
                "\n" +
                "و مطور البرنامج غير مسؤل عن اي اوردر/حموله او من يوصلها او من يتاجر بها." +
                "<p>قام <strong>Armjld</strong> ببناء تطبيق <strong>Eb3tly</strong> باعتباره تطبيقًا مجانيًا. يتم توفير هذه الخدمة من قبل <strong>Armjld</strong> دون أي تكلفة وهي مخصصة للاستخدام كما هي.</p>\n" +
                "<p><br></p>\n" +
                "<p>تُستخدم هذه الصفحة لإبلاغ الزائرين بشأن سياساتي فيما يتعلق بجمع المعلومات الشخصية واستخدامها والكشف عنها إذا قرر أي شخص استخدام الخدمة الخاصة بي.</p>\n" +
                "<p><br></p>\n" +
                "<p>إذا اخترت استخدام الخدمة الخاصة بي ، فأنت توافق على جمع المعلومات واستخدامها فيما يتعلق بهذه السياسة. تُستخدم المعلومات الشخصية التي أقوم بجمعها لتوفير الخدمة وتحسينها. لن أستخدم أو أشارك معلوماتك مع أي شخص باستثناء ما هو موضح في سياسة الخصوصية هذه.</p>\n" +
                "<p><br></p>\n" +
                "<p>المصطلحات المستخدمة في سياسة الخصوصية هذه لها نفس المعاني الموجودة في الشروط والأحكام الخاصة بنا ، والتي يمكن الوصول إليها في Eb3tly ما لم يتم تحديد خلاف ذلك في سياسة الخصوصية هذه.</p>\n" +
                "<p><br></p>\n" +
                "<p><strong>جمع المعلومات واستخدامها</strong></p>\n" +
                "<p><br></p>\n" +
                "<p>للحصول على تجربة أفضل ، أثناء استخدام خدمتنا ، قد أطلب منك أن تزودنا ببعض المعلومات الشخصية المحددة للهوية ، بما في ذلك على سبيل المثال لا الحصر البريد الإلكتروني والاسم والهاتف والمواقع. سيتم الاحتفاظ بالمعلومات التي أطلبها على جهازك ولن أقوم بجمعها بأي شكل من الأشكال.</p>\n" +
                "<p><br></p>\n" +
                "<p>يستخدم التطبيق خدمات الطرف الثالث التي قد تجمع المعلومات المستخدمة لتحديد هويتك.</p>\n" +
                "<p><br></p>\n" +
                "<p>رابط إلى سياسة الخصوصية لموفري خدمة الطرف الثالث المستخدمة من قبل التطبيق</p>\n" +
                "<p><br></p>\n" +
                "<p style=\"text-align: center;\"><strong><span style=\"font-size: 20px;\">خدمات Google Play</span></strong></p>\n" +
                "<p><strong>تسجيل البيانات</strong></p>\n" +
                "<p><br></p>\n" +
                "<p>أريد أن أبلغك أنه كلما استخدمت خدمتي ، في حالة وجود خطأ في التطبيق ، أقوم بجمع البيانات والمعلومات (من خلال منتجات الطرف الثالث) على هاتفك تسمى Log Data. قد تتضمن بيانات السجل هذه معلومات مثل عنوان بروتوكول الإنترنت (&quot;IP&quot; الخاص بجهازك ، واسم الجهاز ، وإصدار نظام التشغيل ، وتكوين التطبيق عند استخدام الخدمة الخاصة بي ، ووقت وتاريخ استخدامك للخدمة ، وإحصاءات أخرى .</p>\n" +
                "<p><br></p>\n" +
                "<p><strong>ملفات التعريف</strong></p>\n" +
                "<p><br></p>\n" +
                "<p>ملفات تعريف الارتباط هي ملفات تحتوي على كمية صغيرة من البيانات التي يتم استخدامها بشكل شائع كمعرفات فريدة مجهولة الهوية. يتم إرسالها إلى متصفحك من مواقع الويب التي تزورها ويتم تخزينها على الذاكرة الداخلية لجهازك.</p>\n" +
                "<p><br></p>\n" +
                "<p>لا تستخدم هذه الخدمة &quot;ملفات تعريف الارتباط&quot; هذه بشكل صريح. ومع ذلك ، قد يستخدم التطبيق كودًا ومكتبات تابعة لجهة خارجية تستخدم &quot;ملفات تعريف الارتباط&quot; لجمع المعلومات وتحسين خدماتها. لديك خيار إما قبول أو رفض ملفات تعريف الارتباط هذه ومعرفة متى يتم إرسال ملف تعريف ارتباط إلى جهازك. إذا اخترت رفض ملفات تعريف الارتباط الخاصة بنا ، فقد لا تتمكن من استخدام بعض أجزاء هذه الخدمة.</p>\n" +
                "<p><br></p>\n" +
                "<p><strong>مقدمي الخدمة</strong></p>\n" +
                "<p><br></p>\n" +
                "<p>يجوز لي توظيف شركات وأفراد من جهات خارجية للأسباب التالية:</p>\n" +
                "<p><br></p>\n" +
                "<p>لتسهيل خدمتنا ؛</p>\n" +
                "<p>لتقديم الخدمة نيابة عنا ؛</p>\n" +
                "<p>لأداء الخدمات المتعلقة بالخدمة ؛ أو</p>\n" +
                "<p>لمساعدتنا في تحليل كيفية استخدام خدمتنا.</p>\n" +
                "<p>أريد إبلاغ مستخدمي هذه الخدمة أن هذه الأطراف الثالثة يمكنها الوصول إلى معلوماتك الشخصية. والسبب هو أداء المهام المسندة إليهم نيابة عنا. ومع ذلك ، فإنهم ملزمون بعدم الكشف عن المعلومات أو استخدامها لأي غرض آخر.</p>\n" +
                "<p><br></p>\n" +
                "<p><strong>الأمان</strong></p>\n" +
                "<p><br></p>\n" +
                "<p>أقدر ثقتك في تزويدنا بمعلوماتك الشخصية ، وبالتالي نسعى جاهدين لاستخدام وسائل مقبولة تجاريًا لحمايتها. ولكن تذكر أنه لا توجد طريقة إرسال عبر الإنترنت ، أو طريقة تخزين إلكترونية آمنة وموثوق بها بنسبة 100٪ ، ولا يمكنني ضمان أمانها المطلق.</p>\n" +
                "<p><br></p>\n" +
                "<p><strong>روابط لمواقع أخرى</strong></p>\n" +
                "<p><br></p>\n" +
                "<p>قد تحتوي هذه الخدمة على روابط لمواقع أخرى. إذا قمت بالنقر فوق ارتباط جهة خارجية ، فسيتم توجيهك إلى هذا الموقع. لاحظ أن هذه المواقع الخارجية لا أديرها. لذلك ، أنصحك بشدة بمراجعة سياسة الخصوصية لهذه المواقع. ليس لدي أي سيطرة ولا أتحمل أي مسؤولية عن المحتوى أو سياسات الخصوصية أو الممارسات الخاصة بأي مواقع أو خدمات تابعة لجهات خارجية.</p>\n" +
                "<p><br></p>\n" +
                "<p><strong>خصوصية الأطفال</strong></p>\n" +
                "<p><br></p>\n" +
                "<p>لا تتعامل هذه الخدمات مع أي شخص يقل عمره عن 13 عامًا. لا أقوم بجمع معلومات التعريف الشخصية عن عمد من الأطفال تحت سن 13 عامًا. في حالة اكتشاف أن طفلاً دون 13 عامًا قد زودني بمعلومات شخصية ، فإنني أحذفها على الفور من خوادمنا. إذا كنت أحد الوالدين أو الوصي وكنت على علم بأن طفلك قدم لنا معلومات شخصية ، يرجى الاتصال بي حتى أتمكن من القيام بالإجراءات اللازمة.</p>\n" +
                "<p><br></p>\n" +
                "<p><strong>التغييرات على سياسة الخصوصية هذه</strong></p>\n" +
                "<p><br></p>\n" +
                "<p>قد أقوم بتحديث سياسة الخصوصية الخاصة بنا من وقت لآخر. وبالتالي ، ننصحك بمراجعة هذه الصفحة بشكل دوري لأية تغييرات. سأعلمك بأي تغييرات عن طريق نشر سياسة الخصوصية الجديدة على هذه الصفحة.</p>\n" +
                "<p><br></p>\n" +
                "<p>تسري هذه السياسة اعتبارًا من 2020-05-21</p>\n" +
                "<p><br></p>\n" +
                "<p><strong>اتصل بنا</strong></p>\n" +
                "<p><br></p>\n" +
                "<p>إذا كان لديك أي أسئلة أو اقتراحات حول سياسة الخصوصية الخاصة بي ، فلا تتردد في الاتصال بي على armjldtrainer@gmail.com.</p>";

        // get the terms of the app
        txtTerms.setText(HtmlCompat.fromHtml(privcyCode, 0));

        // Tool Bar Title
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("الشروط و الاحكام");
    }
}
