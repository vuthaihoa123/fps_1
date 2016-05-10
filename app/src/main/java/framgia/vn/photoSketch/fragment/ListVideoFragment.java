package framgia.vn.photoSketch.fragment;

import android.os.Bundle;
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

import framgia.vn.photoSketch.R;
import framgia.vn.photoSketch.adapter.ListVideoAdapter;
import framgia.vn.photoSketch.bitmaputil.VideoUtil;
import framgia.vn.photoSketch.constants.ConstActivity;
import framgia.vn.photoSketch.library.LoadVideo;
import framgia.vn.photoSketch.models.Video;

/**
 * Created by nghicv on 23/04/2016.
 */
public class ListVideoFragment extends Fragment {
    public final int NUM_COLUMN = 2;
    private RecyclerView mRecyclerListVideo;
    private View mView;
    private List<Video> mListVideo = new ArrayList<>();
    private ListVideoAdapter mListVideoAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_list_video, container, false);
        initView();
        return mView;
    }

    private void initView() {
        loadPhotoPaths();
        mRecyclerListVideo = (RecyclerView) mView.findViewById(R.id.recycler_list_video);
        mRecyclerListVideo.setLayoutManager(new GridLayoutManager(getActivity(), NUM_COLUMN));
        mListVideoAdapter = new ListVideoAdapter(getActivity(), mListVideo);
        mRecyclerListVideo.setAdapter(mListVideoAdapter);
        mListVideoAdapter.notifyDataSetChanged();
    }

    private void loadPhotoPaths() {
        String pathVideoFolder = ConstActivity.ROOT_FOLDER + File.separator + VideoUtil.FOLDER_NAME;
        mListVideo = LoadVideo.loadVideoPaths(pathVideoFolder);
    }
}
