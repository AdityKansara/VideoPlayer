package com.hdplayer.latestvideo.xvideoplayer.saxvideos;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.AppData;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.DBHandler;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.Logger;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.PreferenceManager;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.SampleData;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

public class SearchingActivity extends AppCompatActivity {

    ListView listView;
    SearchView searchview;
    AdView adviewBanner;
    AdManager adManager;
    InterstitialAd interstitialAd;
    private ArrayList<HashMap<String, String>> List = new ArrayList<>();
    private ArrayList<HashMap<String, String>> DummyList = new ArrayList<>();
    private Dialog detaliDialog;
    private DBHandler DBHandler;
    private Dialog deleteDialog;
    private SearchListAdapter adpter;

    private static String msToString(int ms) {
        long totalSecs = ms / 1000;
        long hours = (totalSecs / 3600);
        long mins = (totalSecs / 60) % 60;
        long secs = totalSecs % 60;
        String minsString = (mins == 0)
                ? "00"
                : ((mins < 10)
                ? "0" + mins
                : "" + mins);
        String secsString = (secs == 0)
                ? "00"
                : ((secs < 10)
                ? "0" + secs
                : "" + secs);
        if (hours > 0)
            return hours + ":" + minsString + ":" + secsString;
        else if (mins > 0)
            return mins + ":" + secsString;
        else return "00:" + secsString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

        DBHandler = new DBHandler(this);

        listView = findViewById(R.id.listView);
        searchview = findViewById(R.id.searchview);

        adviewBanner = findViewById(R.id.adviewBanner);
        interstitialAd = new InterstitialAd(this);
        adManager = new AdManager(this, adviewBanner, interstitialAd);

        fn_videoData();
        PreferenceManager.ShowAds();
        searchview.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                String text = newText.toLowerCase(Locale.getDefault());

                Logger.Print(">>> Name 11 >> " + text);
                adpter.filter(text);

                return true;
            }
        });


    }

    @SuppressLint("Recycle")
    private void fn_videoData() {

        Uri uri;
        Cursor cursor;
        int column_index_data, duration, thum, size, id, date, resolution;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.TITLE, MediaStore.Video.Media._ID, MediaStore.MediaColumns.SIZE,
                MediaStore.Video.Media.DATE_MODIFIED, MediaStore.Video.Media.RESOLUTION, MediaStore.Video.Media.DURATION, MediaStore.Video.Thumbnails.DATA};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE);
            size = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE);
            duration = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
            date = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED);
            resolution = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION);

            do {
                Logger.Print("date >> " + cursor.getString(id));

                String value = new DecimalFormat("##.##").format(cursor.getFloat(size) / (1000 * 1000));

                HashMap<String, String> map = new HashMap<>();

                map.put(AppData.VideoPath, cursor.getString(thum));
                map.put(AppData.VideoReso, cursor.getString(resolution));
                map.put(AppData.VideoDate, sdf.format(cursor.getInt(date)));
                map.put(AppData.VideoName, cursor.getString(column_index_data));
                map.put(AppData.VideoTime, msToString(cursor.getInt(duration)));
                map.put(AppData.VideoSize, value);
                map.put(AppData.VideoId, cursor.getString(id));

                List.add(map);
            } while (cursor.moveToNext());
        }


        if (cursor != null) {
            cursor.close();
        }

        adpter = new SearchListAdapter();
        listView.setAdapter(adpter);

        DummyList.addAll(List);

        Logger.Print(">>> DummyList >> " + DummyList);
    }

    public void OnBackClickSearch(View view) {
        finish();
    }

    private SampleData mSampleData(int position) {

        Logger.Print(">>> List >> " + List);

        SampleData sampleData = new SampleData();
        sampleData.setKeyID(List.get(position).get(AppData.VideoId));
        sampleData.setSampleVideo(List.get(position).get(AppData.VideoPath));
        sampleData.setSongName(List.get(position).get(AppData.VideoName));
        sampleData.setVideoTime(List.get(position).get(AppData.VideoTime));
        sampleData.setVideoSize(List.get(position).get(AppData.VideoSize));
        sampleData.setVideoResolution(List.get(position).get(AppData.VideoReso));
        sampleData.setVideoDateModi(List.get(position).get(AppData.VideoDate));

        return sampleData;
    }

    @SuppressLint("SetTextI18n")
    private void ShowDetailDailogue(int position) {

        if (detaliDialog != null && detaliDialog.isShowing()) {
            detaliDialog.dismiss();
            detaliDialog = null;
        }

        detaliDialog = new Dialog(SearchingActivity.this, R.style.Transparent);
        detaliDialog.setContentView(R.layout.details_dialog);
        detaliDialog.setCancelable(true);

        TextView name = detaliDialog.findViewById(R.id.videoname);
        name.setText("Name: " + mSampleData(position).getSongName());

        TextView path = detaliDialog.findViewById(R.id.videopath);
        path.setText("Path: " + mSampleData(position).getSampleVideo());

        TextView time = detaliDialog.findViewById(R.id.videotime);
        time.setText("Time: " + mSampleData(position).getVideoTime());

        TextView size = detaliDialog.findViewById(R.id.videosize);
        size.setText("Size: " + mSampleData(position).getVideoSize() + "MB");

        TextView date = detaliDialog.findViewById(R.id.videodatemodified);
        date.setText("Date Modified : " + mSampleData(position).getVideoDateModi());

        TextView reso = detaliDialog.findViewById(R.id.videoresolution);
        reso.setText("Resolution: " + mSampleData(position).getVideoResolution());

        detaliDialog.findViewById(R.id.img_ok).setOnClickListener(v -> detaliDialog.dismiss());

        if (!isFinishing()) {

            detaliDialog.show();
        }
    }

    private void ShowPopUpmenu(View v, int position) {

        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.home, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.addtofavourite) {

                boolean isPresent = false;
                for (int i = 0; i < DBHandler.getAllSampleDatas().size(); i++) {

                    Logger.Print(">>Id >> " + DBHandler.getAllSampleDatas().get(i).getKeyID() + " >> key >>> " + List.get(position).get(AppData.VideoId));
                    if (DBHandler.getAllSampleDatas().get(i).getKeyID().equalsIgnoreCase(List.get(position).get(AppData.VideoId))) {

                        isPresent = true;
                        break;
                    }
                }

                if (isPresent) {
                    Toast.makeText(SearchingActivity.this, "Already in favourite..!!", Toast.LENGTH_LONG).show();
                } else {

                    DBHandler.addSampleData(mSampleData(position));
                    Toast.makeText(SearchingActivity.this, "Added in favourite..!!", Toast.LENGTH_LONG).show();
                }

            } else if (item.getItemId() == R.id.play) {

                PlayClick(position);
            } else if (item.getItemId() == R.id.details) {

                ShowDetailDailogue(position);
            } else if (item.getItemId() == R.id.delete) {

                ShowDeteteDailogue(position);
            } else if (item.getItemId() == R.id.share) {

                Uri photoURI;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {

                    photoURI = Uri.fromFile(new File(Objects.requireNonNull(List.get(position).get(AppData.VideoPath))));
                } else {

                    photoURI = FileProvider.getUriForFile(SearchingActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(Objects.requireNonNull(List.get(position).get(AppData.VideoPath))));
                }
                Intent videoshare = new Intent(Intent.ACTION_SEND);
                videoshare.setType("*/*");
                videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                videoshare.putExtra(Intent.EXTRA_STREAM, photoURI);

                try {
                    startActivity(videoshare);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        });
    }

    private void PlayClick(int position) {

        boolean isPresent = false;
        for (int i = 0; i < DBHandler.getAllRecentDatas().size(); i++) {
            if (DBHandler.getAllRecentDatas().get(i).getKeyID().equalsIgnoreCase(mSampleData(position).getKeyID())) {

                isPresent = true;
                break;
            }
        }

        if (!isPresent) {

            DBHandler.addRecentviewdata(mSampleData(position));
        }

        if (AppData.VideoListForContinuePlay != null) {
            AppData.VideoListForContinuePlay.clear();
            AppData.VideoListForContinuePlay.addAll(List);
        }
        Intent intent = new Intent(SearchingActivity.this, VideoPlayActivity.class);
        intent.putExtra("songLink", List.get(position).get(AppData.VideoPath));
        intent.putExtra("VideoPosition", position);
        startActivity(intent);
    }

    private void ShowDeteteDailogue(int position) {

        if (deleteDialog != null && deleteDialog.isShowing()) {
            deleteDialog.dismiss();
            deleteDialog = null;
        }

        deleteDialog = new Dialog(SearchingActivity.this, R.style.Transparent);
        deleteDialog.setContentView(R.layout.delete_dialog);
        deleteDialog.setCancelable(true);

        TextView name = deleteDialog.findViewById(R.id.txt_videoname);
        name.setText(List.get(position).get(AppData.VideoName));


        deleteDialog.findViewById(R.id.okbtn).setOnClickListener(v -> {


            File fdelete = new File(Objects.requireNonNull(List.get(position).get(AppData.VideoPath)));

            if (fdelete.exists()) {

                if (fdelete.delete()) {

                    boolean isPresent = false;
                    for (int i = 0; i < DBHandler.getAllRecentDatas().size(); i++) {
                        if (DBHandler.getAllRecentDatas().get(i).getKeyID().equalsIgnoreCase(List.get(position).get(AppData.VideoId))) {

                            isPresent = true;
                            break;
                        }
                    }

                    if (!isPresent) {

                        DBHandler.deleteRecentData(List.get(position).get(AppData.VideoId));
                    }

                    boolean isPresent1 = false;
                    for (int i = 0; i < DBHandler.getAllSampleDatas().size(); i++) {
                        if (DBHandler.getAllSampleDatas().get(i).getKeyID().equalsIgnoreCase(List.get(position).get(AppData.VideoId))) {

                            isPresent1 = true;
                            break;
                        }
                    }

                    if (!isPresent1) {

                        DBHandler.deleteSampleData(List.get(position).get(AppData.VideoId));
                    }
                    List.remove(position);
                    adpter.notifyDataSetChanged();

                    RefreshGallery();
                    Logger.Print(">>> Data 33 >> " + List);
                }
            }
            deleteDialog.dismiss();
        });
        deleteDialog.findViewById(R.id.cancel).setOnClickListener(v -> deleteDialog.dismiss());

        if (!isFinishing()) {

            deleteDialog.show();
        }
    }

    private void RefreshGallery() {

        MediaScannerConnection.scanFile(SearchingActivity.this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, (path, uri) -> {
            Logger.Print("Scanned " + path + ":");
            Logger.Print("-> uri=" + uri);
        });
    }

    private class SearchListAdapter extends BaseAdapter {

        private SearchListAdapter() {

        }

        @Override
        public int getCount() {
            if (List != null && List.size() > 0) {
                return List.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = LayoutInflater.from(SearchingActivity.this).inflate(R.layout.video_list_item, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);

            } else {

                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.videoname.setText(List.get(position).get(AppData.VideoName));
            viewHolder.videotime.setText(List.get(position).get(AppData.VideoTime));
            viewHolder.videosize.setText(List.get(position).get(AppData.VideoSize) + " MB");

            Glide.with(SearchingActivity.this).load(List.get(position).get(AppData.VideoPath)).centerCrop().into(viewHolder.videoimage);

            viewHolder.menuitem.setOnClickListener(v -> ShowPopUpmenu(v, position));

            viewHolder.constraintLayout.setOnClickListener(v -> PlayClick(position));

            return convertView;
        }

        void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            List.clear();
            if (charText.length() == 0) {
                List.addAll(DummyList);
            } else {
                if (DummyList != null && DummyList.size() > 0) {
                    for (HashMap<String, String> wp : DummyList) {

                        Logger.Print(">>> Name >> " + wp.get(AppData.VideoName));
                        if (Objects.requireNonNull(wp.get(AppData.VideoName)).toLowerCase(Locale.getDefault()).contains(charText)) {
                            List.add(wp);
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }

        private class ViewHolder {

            TextView videoname, videotime, videosize;
            ImageView videoimage, menuitem;
            ConstraintLayout constraintLayout;

            ViewHolder(View root) {

                menuitem = root.findViewById(R.id.menuitem);
                videoname = root.findViewById(R.id.txt_video_name);
                videotime = root.findViewById(R.id.txt_video_time);
                videosize = root.findViewById(R.id.txt_video_size);
                videoimage = root.findViewById(R.id.image);
                constraintLayout = root.findViewById(R.id.constraintLayout);
            }
        }
    }
}
