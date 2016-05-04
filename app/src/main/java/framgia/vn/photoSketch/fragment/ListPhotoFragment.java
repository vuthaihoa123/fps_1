package framgia.vn.photoSketch.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import framgia.vn.photoSketch.R;
import framgia.vn.photoSketch.adapter.ListPhotoAdapter;
import framgia.vn.photoSketch.bitmaputil.BitmapUtil;
import framgia.vn.photoSketch.library.LoadPhoto;
import framgia.vn.photoSketch.models.Photo;

/**
 * Created by nghicv on 23/04/2016.
 */
public class ListPhotoFragment extends Fragment {

    private final int NUM_COLUMN = 2;
    private RecyclerView mRecyclerListPhoto;
    private View mView;
    private List<Photo> mListPhoto;
    private ListPhotoAdapter mListPhotoAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_list_photo, container, false);
        initView();
        return mView;
    }

    private void initView() {
        loadPhotoPaths();
        mRecyclerListPhoto = (RecyclerView) mView.findViewById(R.id.recycler_list_photo);
        mRecyclerListPhoto.setLayoutManager(new GridLayoutManager(getActivity(), NUM_COLUMN));
        mListPhotoAdapter = new ListPhotoAdapter(mListPhoto);
        mRecyclerListPhoto.setAdapter(mListPhotoAdapter);
        mListPhotoAdapter.notifyDataSetChanged();
    }

    private void loadPhotoPaths() {
        mListPhoto = LoadPhoto.loadPhotoPaths(BitmapUtil.FOLDER_NAME);
    }
}
