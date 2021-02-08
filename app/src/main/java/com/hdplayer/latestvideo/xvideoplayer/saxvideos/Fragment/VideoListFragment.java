package com.hdplayer.latestvideo.xvideoplayer.saxvideos.Fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdplayer.latestvideo.xvideoplayer.saxvideos.Adapter.VideoListAdapter;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.R;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.AppData;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.Logger;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.PreferenceManager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VideoListFragment extends Fragment {

    private RecyclerView recyclerView;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = root.findViewById(R.id.recyclerview);

        UpdateFragment();
        return root;
    }

    public void UpdateFragment() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);

        fn_videoData();
    }

    @SuppressLint("Recycle")
    private void fn_videoData() {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        Uri uri;
        Cursor cursor;
        int column_index_data, duration, thum, size, id, date, resolution;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");


        String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";

        int sort = PreferenceManager.getSorting();

        if (sort == 0) {

            orderBy = MediaStore.Images.Media.DISPLAY_NAME + " ASC";
        } else if (sort == 1) {

            orderBy = MediaStore.Images.Media.DISPLAY_NAME + " DESC";
        } else if (sort == 2) {

            orderBy = MediaStore.Images.Media.DATE_TAKEN + " ASC";
        } else if (sort == 3) {

            orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";
        } else if (sort == 4) {

            orderBy = MediaStore.Images.Media.SIZE + " ASC";
        } else if (sort == 5) {

            orderBy = MediaStore.Images.Media.SIZE + " DESC";
        }

        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.TITLE, MediaStore.Video.Media._ID, MediaStore.MediaColumns.SIZE,
                MediaStore.Video.Media.DATE_MODIFIED, MediaStore.Video.Media.RESOLUTION, MediaStore.Video.Media.DURATION, MediaStore.Video.Thumbnails.DATA};

        cursor = Objects.requireNonNull(getActivity()).getContentResolver().query(uri, projection, null, null, orderBy);

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

                list.add(map);
            } while (cursor.moveToNext());
        }


        if (cursor != null) {
            cursor.close();
        }
        VideoListAdapter videoListAdapter = new VideoListAdapter(getContext(), list, VideoListFragment.this);
        recyclerView.setAdapter(videoListAdapter);
    }
}