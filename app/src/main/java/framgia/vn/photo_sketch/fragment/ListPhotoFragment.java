package framgia.vn.photo_sketch.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import framgia.vn.photo_sketch.R;
import framgia.vn.photo_sketch.adapter.ListPhotoAdapter;
import framgia.vn.photo_sketch.bitmaputil.BitmapUtil;
import framgia.vn.photo_sketch.models.Photo;

/**
 * Created by nghicv on 23/04/2016.
 */
public class ListPhotoFragment extends Fragment {

    private final int NUM_COLUMN = 2;
    private RecyclerView mRecyclerListPhoto;
    private View mView;
    private List<Photo> mListPhoto = new ArrayList<>();
    private ListPhotoAdapter mListPhotoAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_list_photo, container, false);
        initView();
        return mView;
    }

    private void initView() {
        mRecyclerListPhoto = (RecyclerView) mView.findViewById(R.id.recycler_list_photo);
        mRecyclerListPhoto.setLayoutManager(new GridLayoutManager(getActivity(), NUM_COLUMN));
        mListPhotoAdapter = new ListPhotoAdapter(mListPhoto);
        mRecyclerListPhoto.setAdapter(mListPhotoAdapter);
        loadPhotoPaths();
        mListPhotoAdapter.notifyDataSetChanged();
    }

    private void loadPhotoPaths() {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + BitmapUtil.FOLDER_NAME;
        File file = new File(dirPath);
        if(file != null && file.isDirectory()) {
            File[] listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                mListPhoto.add(new Photo(listFile[i].getAbsolutePath()));
            }
        }
    }
}
