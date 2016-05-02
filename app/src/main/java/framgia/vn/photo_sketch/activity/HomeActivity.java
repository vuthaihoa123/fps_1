package framgia.vn.photo_sketch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import framgia.vn.photo_sketch.R;

/**
 * Created by nghicv on 22/04/2016.
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_edit_photo:
                startActivity(ChoosePhotoActivity.class);
                break;
            case R.id.linear_combine_photo:
                startActivity(CombinePhotoActivity.class);
                break;
        }
    }

    private void initView() {
        findViewById(R.id.linear_list_photo).setOnClickListener(this);
        findViewById(R.id.linear_edit_photo).setOnClickListener(this);
        findViewById(R.id.linear_combine_photo).setOnClickListener(this);
        findViewById(R.id.linear_make_video).setOnClickListener(this);
    }

    private void startActivity(Class<?> clazz) {
        Intent intent = new Intent(HomeActivity.this, clazz);
        startActivity(intent);
    }
}
