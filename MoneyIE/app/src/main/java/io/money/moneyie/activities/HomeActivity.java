package io.money.moneyie.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import io.money.moneyie.R;
import io.money.moneyie.fragments.Fragment_DataHistory;
//import io.money.moneyie.fragments.Fragment_AddFriend;
import io.money.moneyie.fragments.Fragment_Reminders;
import io.money.moneyie.fragments.Fragment_Income_Expense;
import io.money.moneyie.fragments.Fragment_Profile;
import io.money.moneyie.fragments.Fragment_Statistics;
import io.money.moneyie.model.database.DatabaseHelperFirebase;
import io.money.moneyie.model.utilities.LocaleHelper;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_PERMISSION = 1;
    private static String[] PERMISSIONS= {
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    private Button btnOutcome, btnIncome, btnProfile, btnStatistics, btnAlarms, btnAddFriend, btnLogOut;
    private ImageView sandwichButton, statisticsButton;
    private DrawerLayout drawerLayout;
    private DatabaseHelperFirebase fdb;
    private TextView currentFragment;
    private Fragment_Income_Expense fragment_incomeExpense;
    private Bundle bundle;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        verifyPermissions(this);
        initialiseElements();
        removeActionBar();
        loadFragment(fragment_incomeExpense);
        drawerDropMenuCreator();
        logOutDrawerMenuBtnListener();
        keyboardHideListener();
        showCaseView();
        refreshLanguage();
        clickToolbarHideDrawerListener();
    }

    //starting the adds in onStart because if its started in onCreate it may cause FatalError
    @Override
    protected void onStart() {
        super.onStart();
        bannerAdd();
    }

    public static void verifyPermissions(Activity activity) {
        int permissionInternet = ActivityCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);
        int permissionWrite = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionAcc = ActivityCompat.checkSelfPermission(activity, Manifest.permission.GET_ACCOUNTS);
        int permissionCon = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);
        int permissionNetState = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE);

        if (permissionInternet != PackageManager.PERMISSION_GRANTED || permissionWrite != PackageManager.PERMISSION_GRANTED
                || permissionAcc != PackageManager.PERMISSION_GRANTED || permissionCon != PackageManager.PERMISSION_GRANTED
                || permissionNetState != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS,
                    REQUEST_PERMISSION
            );
        }
    }

    private void bannerAdd() {
        final AdRequest adRequest = new AdRequest.Builder().build();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String s = preferences.getString("firstTimeInstall", "NOO");
        if (s != null && !s.equals("NOO")) {
            mAdView.loadAd(adRequest);
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("firstTimeInstall", "YESS");
            editor.apply();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String FILENAME = "moneyielanguage";
        File file = new File(android.os.Environment.getExternalStorageDirectory().toString(), FILENAME);

        int length = (int) file.length();

        byte[] bytes = new byte[length];

        FileInputStream in = null;
        try {
            if(file.exists()) {
                in = new FileInputStream(file);
                in.read(bytes);
            }
        } catch (IOException ex){
            ex.printStackTrace();
        } finally {
            try {
                if(in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String language = new String(bytes);

        if (!language.isEmpty()) {
            super.attachBaseContext(LocaleHelper.wrap(newBase, language));
        } else {
            super.attachBaseContext(newBase);
        }
    }

    private void showCaseView() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(250);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "userID");
        sequence.setConfig(config);

        sequence.addSequenceItem(sandwichButton,
                getString(R.string.tutorial_text1), getString(R.string.tutorial_got_it_btn_text));

        sequence.addSequenceItem(statisticsButton,
                getString(R.string.tutorial_text2), getString(R.string.tutorial_got_it_btn_text));
        sequence.start();
    }

    //hide the menu if the user clicks on the toolbar text
    private void clickToolbarHideDrawerListener() {
        currentFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(Gravity.START)) {
                    drawerLayout.closeDrawer(Gravity.START);
                }
            }
        });
    }

    private void setCurrentFragment(String s) {
        currentFragment.setText(s);
    }

    private void initialiseElements(){
        fdb = DatabaseHelperFirebase.getInstance(this);
        fragment_incomeExpense = new Fragment_Income_Expense();
        bundle = new Bundle();
        bundle.putBoolean("isExpense", true);
        fragment_incomeExpense.setArguments(bundle);
        sandwichButton = findViewById(R.id.home_toolbar_sandwich_btn);
        drawerLayout = findViewById(R.id.dlContent);
        currentFragment = findViewById(R.id.home_toolbar_app_name);
        btnOutcome = findViewById(R.id.home_outcome_btn);
        btnOutcome.setOnClickListener(this);
        btnIncome = findViewById(R.id.home_income_btn);
        btnIncome.setOnClickListener(this);
        btnStatistics = findViewById(R.id.home_statistics_btn);
        btnStatistics.setOnClickListener(this);
        btnProfile = findViewById(R.id.home_myProfile_btn);
        btnProfile.setOnClickListener(this);
        btnAlarms = findViewById(R.id.home_alarms_btn);
        btnAlarms.setOnClickListener(this);
        btnLogOut = findViewById(R.id.home_logout_btn);
        statisticsButton = findViewById(R.id.home_toolbar_statistics_icon_btn);
        statisticsButton.setOnClickListener(this);
        btnAddFriend = findViewById(R.id.home_add_friend_btn);
        btnAddFriend.setOnClickListener(this);
        mAdView = findViewById(R.id.adView);
    }

    private void removeActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    //log out button listener
    public void logOutDrawerMenuBtnListener() {
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void refreshLanguage() {
        if (getIntent().hasExtra("changeLanguage")) {
            String change = getIntent().getExtras().getString("changeLanguage");
            if (change != null && !change.isEmpty() && change.equals("yes")) {
                drawerMenuButtonsAction(getString(R.string.my_profile), new Fragment_Profile());
            }
        }
    }

    public void keyboardHideListener(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.home_activity);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    //creates drawer menu
    public void drawerDropMenuCreator() {

        //drawer menu settings
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.setStatusBarBackground(Color.TRANSPARENT);

        //set functionality of the button
        sandwichButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.START)) {
                    drawerLayout.closeDrawer(Gravity.START);
                } else {
                    drawerLayout.openDrawer(Gravity.START);
                }
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.home_main,fragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        //closes drawer menu
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);

            //check witch fragment is loaded
        } else if (fragment_incomeExpense != null && fragment_incomeExpense.isVisible()){
            exit();

        } else {
            bundle.putBoolean("isExpense", true);
            fragment_incomeExpense.setArguments(bundle);
            loadFragment(fragment_incomeExpense);
            setCurrentFragment(getString(R.string.expense));
            statisticsButton.setVisibility(View.VISIBLE);
        }
    }

    //exit dialog interface
    public void exit(){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(HomeActivity.this);
        a_builder.setMessage(R.string.do_you_want_to_exit)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle(getString(R.string.quit));
        alert.show();
    }

    private void hideDrawer(){
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        }
    }

    //sets click listeners
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_income_btn:
                bundle.putBoolean("isExpense", false);
                Fragment_Income_Expense fragment_incomeExpense1 = new Fragment_Income_Expense();
                fragment_incomeExpense1.setArguments(bundle);
                drawerMenuButtonsAction(getString(R.string.income), fragment_incomeExpense1);
                break;
            case R.id.home_outcome_btn:
                bundle.putBoolean("isExpense", true);
                fragment_incomeExpense.setArguments(bundle);
                drawerMenuButtonsAction(getString(R.string.expense), fragment_incomeExpense);
                break;
            case R.id.home_statistics_btn:
                startInterstitialAd();
                drawerMenuButtonsAction(getString(R.string.statistics), new Fragment_Statistics());
                break;
            case R.id.home_myProfile_btn:
                drawerMenuButtonsAction(getString(R.string.my_profile), new Fragment_Profile());
                break;
            case R.id.home_add_friend_btn:
//                drawerMenuButtonsAction(getString(R.string.add_friend), new Fragment_AddFriend());
                break;
            case R.id.home_alarms_btn:
                drawerMenuButtonsAction(getString(R.string.reminders), new Fragment_Reminders());
                break;
            case R.id.home_toolbar_statistics_icon_btn:
                startInterstitialAd();
                drawerMenuButtonsAction(getString(R.string.my_stats), new Fragment_DataHistory());
                break;
        }
    }

    //on drawer button click - loads new fragment, set correct name, hide drawer
    private void drawerMenuButtonsAction(String fragmentTitle, Fragment fragment) {
        loadFragment(fragment);
        setCurrentFragment(fragmentTitle);
        hideDrawer();
    }

    private void startInterstitialAd() {
        Random r = new Random();
        int e = r.nextInt(555);
        if (e == 1) { //TODO fix with random number
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mInterstitialAd.loadAd(adRequest);
            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });
        }
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            bannerAdd(); //TODO remove this method from here
        }
    }
}
