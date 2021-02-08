package com.hdplayer.latestvideo.xvideoplayer.saxvideos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.Fragment.FavouriteListFragment;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.Fragment.FolderListFragment;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.Fragment.RecentItemFragment;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.Fragment.VideoListFragment;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.AppData;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class VideoPlayerDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener {
    private static final int REQUEST_PERMISSIONS = 100;
    ViewPager viewPager;
    TabLayout tabs;
    NavigationView view_nav;
    ImageView menuitem;
    ImageView searchicon;
    ImageView drowermenu;
    SearchView searchview;
    TextView title;
    boolean isGranted = false;
    int[] enableIcon = {R.drawable.video, R.drawable.folder, R.drawable.recent, R.drawable.favourite};
    int[] disableIcon = {R.drawable.video, R.drawable.folder, R.drawable.recent, R.drawable.favourite};
    AlertDialog.Builder builder;
    ImageView[] icon = new ImageView[4];
    InterstitialAd interstitialAd;
    AdManager adManager;
    NativeTemplateStyle styles;
    AdLoader adLoader;
    VideoListFragment videoListFragment;
    private DrawerLayout Navidrawer;
    private int Count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_dashboard);
        interstitialAd = new InterstitialAd(this);
        adManager = new AdManager(this, interstitialAd);
        adManager.fullScreenAd();
        builder = new AlertDialog.Builder(this);
        DefineIds();

        styles = new NativeTemplateStyle.Builder().build();
        adLoader = new AdLoader.Builder(VideoPlayerDashboard.this, getString(R.string.nativeadmainscreen))
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        TemplateView template = findViewById(R.id.my_template);
                        template.setStyles(styles);
                        template.setNativeAd(unifiedNativeAd);

                    }
                }).withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        Log.e("ADS_FAIL", adError.getMessage());
                        // Handle the failure by logging, altering the UI, and so on.
                    }

                })
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());


    }

    private void fn_checkpermission() {
        /*RUN TIME PERMISSIONS*/

        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(VideoPlayerDashboard.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);

        } else {
            fn_video();
        }
    }

    private void fn_showalert() {

        builder.setMessage("Please Allow Permission.")
                .setTitle("Alert")
                .setCancelable(false)
                .setPositiveButton("Allow", (dialog, id) -> {

                    dialog.dismiss();
                    fn_checkpermission();
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    isGranted = true;
                } else {

                    isGranted = false;
                    fn_showalert();
                    Toast.makeText(VideoPlayerDashboard.this, "The App was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting permission", Toast.LENGTH_LONG).show();
                }
            }
            if (isGranted) {
                fn_video();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        showAdsWithCount();

        if (id == R.id.sort_name_asc) {
            item.setChecked(true);
        } else if (id == R.id.sort_name_dsc) {
            item.setChecked(true);
        } else if (id == R.id.sort_date_asc) {
            item.setChecked(true);
        } else if (id == R.id.sort_date_dsc) {
            item.setChecked(true);
        } else if (id == R.id.sort_size_asc) {
            item.setChecked(true);
        } else if (id == R.id.sort_size_dsc) {
            item.setChecked(true);
        } else {
            item.setChecked(true);
        }

        return super.onOptionsItemSelected(item);
    }

    private void fn_video() {

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(1);

        setViewPagerData();
    }

    @SuppressLint("WrongConstant")
    private void DefineIds() {

        viewPager = findViewById(R.id.view_pager);
        tabs = findViewById(R.id.tabs);
        view_nav = findViewById(R.id.view_nav);
        menuitem = findViewById(R.id.menuitem);
        drowermenu = findViewById(R.id.drowermenu);
        searchicon = findViewById(R.id.searchicon);
        searchview = findViewById(R.id.searchview);
        title = findViewById(R.id.title);
        Navidrawer = findViewById(R.id.drawer_layout);
        view_nav.setNavigationItemSelectedListener(this);

        drowermenu.setOnClickListener(v -> Navidrawer.openDrawer(Gravity.START));
        fn_checkpermission();

        menuitem.setOnClickListener(this::showPopup);
        searchicon.setOnClickListener(v -> {
            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
            }
            Intent intent = new Intent(VideoPlayerDashboard.this, SearchingActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public void onBackPressed() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
        super.onBackPressed();
    }

    @SuppressLint("InflateParams")
    private void setViewPagerData() {

        @SuppressLint("InflateParams") View view1;

        for (int value = 0; value < disableIcon.length; value++) {
            view1 = getLayoutInflater().inflate(R.layout.tab_item, null);
            icon[value] = view1.findViewById(R.id.icon);

            icon[value].setImageResource(disableIcon[value]);

            tabs.addTab(tabs.newTab().setCustomView(view1));
            tabs.setTabMode(TabLayout.MODE_SCROLLABLE);

        }
        icon[0].setImageResource(enableIcon[0]);

        wrapTabIndicatorToTitle(tabs, AppData.getWidth(7), AppData.getWidth(7));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                showAdsWithCount();

                icon[tab.getPosition()].setImageResource(enableIcon[tab.getPosition()]);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                icon[tab.getPosition()].setImageResource(disableIcon[tab.getPosition()]);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                icon[tab.getPosition()].setImageResource(enableIcon[tab.getPosition()]);
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

    }

    public void wrapTabIndicatorToTitle(TabLayout tab_layout, int externalMargin, int internalMargin) {

        View tabStrip = tab_layout.getChildAt(0);
        if (tabStrip instanceof ViewGroup) {
            ViewGroup tabStripGroup = (ViewGroup) tabStrip;
            int childCount = ((ViewGroup) tabStrip).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View tabView = tabStripGroup.getChildAt(i);
                //set minimum width to 0 for instead for small texts, indicator is not wrapped as expected
                tabView.setMinimumWidth(0);
                // set padding to 0 for wrapping indicator as title
                tabView.setPadding(0, tabView.getPaddingTop(), 0, tabView.getPaddingBottom());
                // setting custom margin between tabs
                if (tabView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                    if (i == 0) {
                        // left
                        settingMargin(layoutParams, externalMargin, internalMargin);
                    } else if (i == childCount - 1) {
                        // right
                        settingMargin(layoutParams, internalMargin, externalMargin);
                    } else {
                        // internal
                        settingMargin(layoutParams, internalMargin, internalMargin);
                    }
                }
            }

            tab_layout.requestLayout();
        }
    }

    private void settingMargin(ViewGroup.MarginLayoutParams layoutParams, int start, int end) {
        layoutParams.setMarginStart(start);
        layoutParams.setMarginEnd(end);
        layoutParams.leftMargin = start;
        layoutParams.rightMargin = end;
    }

    public void showPopup(View v) {

        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sorting, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();

        if (PreferenceManager.getSorting() == 0) {

            popup.getMenu().findItem(R.id.sort_name_asc).setChecked(true);

        } else if (PreferenceManager.getSorting() == 1) {

            popup.getMenu().findItem(R.id.sort_name_dsc).setChecked(true);

        } else if (PreferenceManager.getSorting() == 2) {

            popup.getMenu().findItem(R.id.sort_date_asc).setChecked(true);

        } else if (PreferenceManager.getSorting() == 3) {

            popup.getMenu().findItem(R.id.sort_date_dsc).setChecked(true);

        } else if (PreferenceManager.getSorting() == 4) {

            popup.getMenu().findItem(R.id.sort_size_asc).setChecked(true);

        } else if (PreferenceManager.getSorting() == 5) {

            popup.getMenu().findItem(R.id.sort_size_dsc).setChecked(true);

        } else {
            popup.getMenu().findItem(R.id.sort_date_dsc).setChecked(true);
        }
    }

    private void showAdsWithCount() {

        if (Count == 2) {
            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
            }
            Count = 0;
        } else {
            Count = Count + 1;
        }
    }

    @Override
    protected void onDestroy() {
        if (PreferenceManager.interstitialAd != null) {
            PreferenceManager.interstitialAd.destroy();
        }

        if (PreferenceManager.adView != null) {
            PreferenceManager.adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        showAdsWithCount();
        switch (item.getItemId()) {

            case R.id.sort_name_asc:


                if (PreferenceManager.isNetworkConnected(VideoPlayerDashboard.this)) {
                    showAdsWithCount();
                }

                PreferenceManager.setSorting(0);
                videoListFragment.UpdateFragment();
                break;

            case R.id.sort_name_dsc:


                if (PreferenceManager.isNetworkConnected(VideoPlayerDashboard.this)) {
                    showAdsWithCount();
                }

                PreferenceManager.setSorting(1);
                videoListFragment.UpdateFragment();
                break;

            case R.id.sort_date_asc:


                if (PreferenceManager.isNetworkConnected(VideoPlayerDashboard.this)) {
                    showAdsWithCount();
                }

                PreferenceManager.setSorting(2);
                videoListFragment.UpdateFragment();
                break;

            case R.id.sort_date_dsc:


                if (PreferenceManager.isNetworkConnected(VideoPlayerDashboard.this)) {
                    showAdsWithCount();
                }

                PreferenceManager.setSorting(3);
                videoListFragment.UpdateFragment();
                break;

            case R.id.sort_size_asc:


                if (PreferenceManager.isNetworkConnected(VideoPlayerDashboard.this)) {
                    showAdsWithCount();
                }

                PreferenceManager.setSorting(4);
                videoListFragment.UpdateFragment();
                break;

            case R.id.sort_size_dsc:


                if (PreferenceManager.isNetworkConnected(VideoPlayerDashboard.this)) {
                    showAdsWithCount();
                }

                PreferenceManager.setSorting(5);
                videoListFragment.UpdateFragment();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @SuppressLint("LongLogTag")
    protected void Bugreport() {
        Intent intent;
        try {
            intent = new Intent("android.intent.action.CHOOSER");
            intent.setAction("android.intent.action.SEND");
            intent.setPackage("com.google.android.gm");
            intent.putExtra("android.intent.extra.EMAIL", new String[]{"mailto:phantomtechies@gmail.com"});
            intent.putExtra("android.intent.extra.SUBJECT", getResources().getString(R.string.app_name));
            intent.putExtra("android.intent.extra.TEXT", getResources().getString(R.string.feedback));
            intent.setType("text/html");
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.send_bug_report_via)));
        } catch (Exception e) {
            intent = new Intent("android.intent.action.CHOOSER");
            intent.setAction("android.intent.action.SEND");
            intent.setPackage("com.google.android.email");
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.send_bug_report_via)));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        PreferenceManager.ShowAds();
        if (id == R.id.video) {

            icon[0].setImageResource(enableIcon[0]);
            viewPager.setCurrentItem(0);
        } else if (id == R.id.privacy) {

            Intent rateintent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.privacy_policy)));
            try {
                startActivity(rateintent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.feedback) {

            Bugreport();
        } else if (id == R.id.share) {

            share();

        } else if (id == R.id.rateus) {

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }

        }

        Navidrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void share() {

        final String appPackageName = getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: https://play.google.com/store/apps/details?id=" + appPackageName);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @SuppressWarnings("deprecation")
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 1:
                    return new FolderListFragment();

                case 2:
                    return new RecentItemFragment();
                case 3:
                    return new FavouriteListFragment();

                case 0:

                default:
                    videoListFragment = new VideoListFragment();
                    return videoListFragment;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}