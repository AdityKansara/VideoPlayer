package com.hdplayer.latestvideo.xvideoplayer.saxvideos.Fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdplayer.latestvideo.xvideoplayer.saxvideos.Adapter.FolderListAdapter;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.R;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.AppData;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.ImageData;
import com.hdplayer.latestvideo.xvideoplayer.saxvideos.utils.Logger;

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

public class FolderListFragment extends Fragment {
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

        AppData.contextFolder = getActivity();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);

        fn_video();

        return root;
    }

    @SuppressLint("Recycle")
    private void fn_video() {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        ArrayList<String> allFolder = new ArrayList<>();
        HashMap<String, ArrayList<ImageData>> allAlbum = new HashMap<>();
        Uri uri;
        Cursor cursor;
        int column_index_folder_name, column_id, id, thum, title, size, time, date, resolution;

        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.TITLE, MediaStore.Video.Media._ID, MediaStore.MediaColumns.SIZE, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATE_MODIFIED, MediaStore.Video.Media.RESOLUTION,
                MediaStore.Video.Media.BUCKET_ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = Objects.requireNonNull(getActivity()).getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        if (cursor != null) {
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
            column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID);
            thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
            title = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE);
            size = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE);
            time = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            date = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED);
            resolution = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION);
            id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);

            while (cursor.moveToNext()) {

                Logger.Print("Folder >> " + cursor.getString(column_index_folder_name));
                Logger.Print("column_id >> " + cursor.getString(column_id));
                Logger.Print("thum >> " + cursor.getString(thum));

                String value = new DecimalFormat("##.##").format(cursor.getFloat(size) / 1000000);

                ImageData data = new ImageData();
                data.imagePath = cursor.getString(thum);
                data.videoTitle = cursor.getString(title);
                data.videokeyid = cursor.getString(id);
                data.videoSize = value;
                data.videoTime = msToString(cursor.getInt(time));
                data.videoResolution = cursor.getString(resolution);
                data.videodateModified = sdf.format(cursor.getInt(date));

                if (data.imagePath.endsWith(".mp4")) {

                    String folderName = cursor.getString(column_index_folder_name);
                    String folderId = cursor.getString(column_id);
                    if (!allFolder.contains(folderId)) {
                        allFolder.add(folderId);
                    }

                    ArrayList<ImageData> imagePath = allAlbum.get(folderId);
                    if (imagePath == null) {
                        imagePath = new ArrayList<>();
                    }

                    data.folderName = folderName;
                    if (imagePath != null && imagePath.size() < 100) {
                        imagePath.add(data);
                    }
                    allAlbum.put(folderId, imagePath);
                }

                Logger.Print("thum 11 >> " + allAlbum);
                Logger.Print("thum 22 >> " + allFolder);

            }
        }
        if (cursor != null) {
            cursor.close();
        }
        FolderListAdapter videoListAdapter = new FolderListAdapter(allAlbum, allFolder, FolderListFragment.this);
        recyclerView.setAdapter(videoListAdapter);

    }
}
