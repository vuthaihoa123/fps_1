package framgia.vn.photoSketch.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.soundcloud.android.crop.Crop;

import java.util.ArrayList;
import java.util.List;

import framgia.vn.photoSketch.R;
import framgia.vn.photoSketch.asynctask.ApplyEffectAsync;
import framgia.vn.photoSketch.asynctask.DisplayBitmapAsync;
import framgia.vn.photoSketch.asynctask.SaveImageAsync;
import framgia.vn.photoSketch.bitmaputil.BitmapUtil;
import framgia.vn.photoSketch.constants.ConstEffects;
import framgia.vn.photoSketch.library.CropLibrary;
import framgia.vn.photoSketch.library.DialogUtils;
import framgia.vn.photoSketch.library.UriLibrary;
import framgia.vn.photoSketch.library.ZoomLibrary;
import framgia.vn.photoSketch.models.Effect;

public class PhotoActivity extends AppCompatActivity implements ConstEffects {
    private ApplyEffectAsync mApplyEffectAsync;
    /* Declare layout */
    private ImageView mImageView;
    private ImageView mImageViewSave;
    private ImageView mImageViewCancelEffect;
    private ImageView mImageViewSelectEffect;
    private ImageView mImageViewUndo;
    /* Filter effect */
    private TextView mTextViewValueHue;
    private SeekBar mSeekBarHue;
    private TextView mTextViewValueBright;
    private SeekBar mSeekBarBright;
    private TextView mTextViewValueContrast;
    private SeekBar mSeekBarContrast;
    private TextView mTextViewValueHighlight;
    private SeekBar mSeekBarHighlight;
    private TextView mTextViewValueGreyScale;
    private SeekBar mSeekBarGreyScale;
    private TextView mTextViewValueSepia;
    private SeekBar mSeekBarSepia;
    /* Layout */
    private LinearLayout mLayoutListEffect;
    private LinearLayout mLinearLayoutSaveUndo;
    private LinearLayout mLinearLayoutCancelEffect;
    private LinearLayout mLinearLayoutFilterHue;
    private LinearLayout mLinearLayoutFilterBright;
    private LinearLayout mLinearLayoutFilterContrast;
    private LinearLayout mLinearLayoutFilterHighlight;
    private LinearLayout mLinearLayoutFilterInvert;
    private LinearLayout mLinearLayoutFilterSketch;
    private LinearLayout mLinearLayoutFilterVignette;
    private LinearLayout mLinearLayoutFilterSepia;
    private LinearLayout mLinearLayoutFilterGreyScale;
    private RelativeLayout mRelativeToolbox;
    private HorizontalScrollView mHorizontalScrollViewEffects;
    private Animation mAnimation;
    private Bitmap mBitmap;
    private Uri mImageUri;
    private Effect mEffectSelect;
    private int mValueEffect;
    private List<Effect> mEffects;
    private List<Bitmap> mBitmaps;
    // crop and rotate fab
    private FloatingActionButton mFabCrop = null, mFabRotate = null;
    private CropLibrary mCropLib = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        FacebookSdk.sdkInitialize(getApplicationContext());
        getControl();
        setEvents();
        loadImage();
        displayListEffects();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        backToChooseImage();
//        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.share_facebook) {
            SharePhoto photo = new SharePhoto.Builder().setBitmap(mBitmap).build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo).build();
            CallbackManager callbackManager = CallbackManager.Factory.create();
            ShareDialog shareDialog = new ShareDialog(this);
            shareDialog.registerCallback(callbackManager, null);
            shareDialog.show(content);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getControl() {
        mCropLib = new CropLibrary();
        mImageView = (ImageView) findViewById(R.id.source_image);
        ZoomLibrary zoomLib = new ZoomLibrary();
        zoomLib.zoom(mImageView);
        mImageViewSave = (ImageView) findViewById(R.id.imageView_save);
        mImageViewUndo = (ImageView) findViewById(R.id.imageView_undo);
        mImageViewCancelEffect = (ImageView) findViewById(R.id.imageView_cancel_effect);
        mImageViewSelectEffect = (ImageView) findViewById(R.id.imageView_select_effect);
        /**
         * Filter effect
         */
        /* HUE */
        mTextViewValueHue = (TextView) findViewById(R.id.textView_value_hue);
        mSeekBarHue = (SeekBar) findViewById(R.id.seekBar_hue);
        /* Bright */
        mTextViewValueBright = (TextView) findViewById(R.id.textView_value_bright);
        mSeekBarBright = (SeekBar) findViewById(R.id.seekBar_bright);
        /* Contrast */
        mTextViewValueContrast = (TextView) findViewById(R.id.textView_value_contrast);
        mSeekBarContrast = (SeekBar) findViewById(R.id.seekBar_contrast);
        /* High light */
        mTextViewValueHighlight = (TextView) findViewById(R.id.textView_value_highlight);
        mSeekBarHighlight = (SeekBar) findViewById(R.id.seekBar_highlight);
        /* Grey Scale */
        mTextViewValueGreyScale = (TextView) findViewById(R.id.textView_value_grey_scale);
        mSeekBarGreyScale = (SeekBar) findViewById(R.id.seekBar_grey_scale);
        /* Sepia */
        mTextViewValueSepia = (TextView) findViewById(R.id.textView_value_sepia);
        mSeekBarSepia = (SeekBar) findViewById(R.id.seekBar_sepia);
        /**
         * Layout
         */
        mLayoutListEffect = (LinearLayout) findViewById(R.id.list_effect);
        mRelativeToolbox = (RelativeLayout) findViewById(R.id.relative_toolbox);
        mLinearLayoutSaveUndo = (LinearLayout) findViewById(R.id.linearLayout_save_undo);
        mLinearLayoutCancelEffect = (LinearLayout) findViewById(R.id.linearLayout_cancel_effect);
        mLinearLayoutFilterHue = (LinearLayout) findViewById(R.id.linearLayout_filter_hue);
        mLinearLayoutFilterBright = (LinearLayout) findViewById(R.id.linearLayout_filter_bright);
        mLinearLayoutFilterContrast = (LinearLayout) findViewById(R.id.linearLayout_filter_contrast);
        mLinearLayoutFilterHighlight = (LinearLayout) findViewById(R.id.linearLayout_filter_highlight);
        mLinearLayoutFilterInvert = (LinearLayout) findViewById(R.id.linearLayout_filter_invert);
        mLinearLayoutFilterSketch = (LinearLayout) findViewById(R.id.linearLayout_filter_skech);
        mLinearLayoutFilterVignette = (LinearLayout) findViewById(R.id.linearLayout_filter_vignette);
        mLinearLayoutFilterSepia = (LinearLayout) findViewById(R.id.linearLayout_filter_sepia);
        mLinearLayoutFilterGreyScale = (LinearLayout) findViewById(R.id.linearLayout_filter_grey_scale);
        mHorizontalScrollViewEffects = (HorizontalScrollView) findViewById(R.id.horizontal_scroll_view_effects);
        Intent intent = getIntent();
        mImageUri = intent.getData();
        mEffects = new ArrayList<Effect>();
        mBitmaps = new ArrayList<Bitmap>();
        // Crop and rotate fab:
        mFabCrop = (FloatingActionButton) findViewById(R.id.fab_crop);
        mFabRotate = (FloatingActionButton) findViewById(R.id.fab_rotate);
    }

    private void setEvents() {
        mImageViewCancelEffect.setOnClickListener(new ImageEvents());
        mImageViewSelectEffect.setOnClickListener(new ImageEvents());
        mImageView.setOnClickListener(new ImageEvents());
        mImageViewSave.setOnClickListener(new ImageEvents());
        mImageViewUndo.setOnClickListener(new ImageEvents());
        mFabCrop.setOnClickListener(new ImageEvents());
        mFabRotate.setOnClickListener(new ImageEvents());
    }

    private void loadImage() {
        String urlImage = UriLibrary.UriToUrl(getApplicationContext(), mImageUri);
        DisplayBitmapAsync bitmap = new DisplayBitmapAsync(this);
        bitmap.execute(urlImage);
    }

    @Nullable
    private Bitmap getBitmap() {
        try {
            return ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * List Effects
     */
    private void displayListEffects() {
        hideAllEditEffect();
        mLayoutListEffect.setVisibility(View.VISIBLE);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.list_effect_in);
        mLayoutListEffect.startAnimation(mAnimation);
        saveUndoIn();
        mFabCrop.setVisibility(View.VISIBLE);
        mFabRotate.setVisibility(View.VISIBLE);
    }

    private void hideListEffects() {
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.list_effect_out);
        mLayoutListEffect.startAnimation(mAnimation);
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLayoutListEffect.clearAnimation();
                mLayoutListEffect.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void onClickEffectButton(View view) {
        if (mEffects.size() == 0) mLinearLayoutSaveUndo.setVisibility(View.GONE);
        displayEditEffect(view);
    }

    /**
     * Edit Effect
     */
    private void displayEditEffect(View view) {
        hideListEffects();
        hideAllEditEffect();
        mBitmap = getBitmap();
        String effect = view.getTag().toString();
        mEffectSelect = new Effect();
        mEffectSelect.setName(effect);
        switch (effect) {
            case FILTER_HUE:
                mLinearLayoutFilterHue.setVisibility(View.VISIBLE);
                mSeekBarHue.setProgress(VALUE_PROGRESS_HUE);
                mTextViewValueHue.setText(String.valueOf(mSeekBarHue.getProgress() - VALUE_PROGRESS_HUE));
                mSeekBarHue.setOnSeekBarChangeListener(new seekBarEvents());
                break;
            case FILTER_BRIGHT:
                mLinearLayoutFilterBright.setVisibility(View.VISIBLE);
                mSeekBarBright.setProgress(VALUE_PROGRESS_BRIGHT);
                mTextViewValueBright.setText(String.valueOf(mSeekBarBright.getProgress() - VALUE_PROGRESS_BRIGHT));
                mSeekBarBright.setOnSeekBarChangeListener(new seekBarEvents());
                break;
            case FILTER_CONTRAST:
                mLinearLayoutFilterContrast.setVisibility(View.VISIBLE);
                mSeekBarContrast.setProgress(VALUE_PROGRESS_CONTRAST);
                mTextViewValueContrast.setText(String.valueOf(mSeekBarContrast.getProgress() - VALUE_PROGRESS_CONTRAST));
                mSeekBarContrast.setOnSeekBarChangeListener(new seekBarEvents());
                break;
            case FILTER_HIGHLIGHT:
                mLinearLayoutFilterHighlight.setVisibility(View.VISIBLE);
                mTextViewValueHighlight.setText(String.valueOf(mSeekBarHighlight.getProgress()));
                mSeekBarHighlight.setOnSeekBarChangeListener(new seekBarEvents());
                break;
            case FILTER_INVERT:
                mLinearLayoutFilterInvert.setVisibility(View.VISIBLE);
                applyEffect();
                break;
            case FILTER_SKETCH:
                mLinearLayoutFilterSketch.setVisibility(View.VISIBLE);
                applyEffect();
                break;
            case FILTER_VIGNETTE:
                mLinearLayoutFilterVignette.setVisibility(View.VISIBLE);
                applyEffect();
                break;
            case FILTER_GREY_SCALE:
                mLinearLayoutFilterGreyScale.setVisibility(View.VISIBLE);
                mSeekBarGreyScale.setProgress(VALUE_PROGRESS_GREY_SCALE);
                mTextViewValueGreyScale.setText(String.valueOf(mSeekBarGreyScale.getProgress()));
                mSeekBarGreyScale.setOnSeekBarChangeListener(new seekBarEvents());
                break;
            case FILTER_SEPIA:
                mLinearLayoutFilterSepia.setVisibility(View.VISIBLE);
                mSeekBarSepia.setProgress(VALUE_PROGRESS_SEPIA);
                mTextViewValueSepia.setText(String.valueOf(VALUE_MAX_SEPIA - mSeekBarSepia.getProgress()));
                mSeekBarSepia.setOnSeekBarChangeListener(new seekBarEvents());
                mEffectSelect.setValue(100);
                applyEffect();
                break;
        }
        saveUndoOut();
        cancelEffectIn();
    }

    private void applyEffect() {
        mApplyEffectAsync = new ApplyEffectAsync(PhotoActivity.this, mBitmap);
        mApplyEffectAsync.execute(mEffectSelect);
    }

    private void hideAllEditEffect() {
        mLinearLayoutFilterHue.setVisibility(View.GONE);
        mLinearLayoutFilterBright.setVisibility(View.GONE);
        mLinearLayoutFilterContrast.setVisibility(View.GONE);
        mLinearLayoutFilterHighlight.setVisibility(View.GONE);
        mLinearLayoutFilterInvert.setVisibility(View.GONE);
        mLinearLayoutFilterSketch.setVisibility(View.GONE);
        mLinearLayoutFilterVignette.setVisibility(View.GONE);
        mLinearLayoutFilterSepia.setVisibility(View.GONE);
        mLinearLayoutFilterGreyScale.setVisibility(View.GONE);
        mFabCrop.setVisibility(View.GONE);
        mFabRotate.setVisibility(View.GONE);
    }

    /**
     * Save undo
     */
    private void saveUndoIn() {
        mLinearLayoutSaveUndo.setVisibility(View.VISIBLE);
        mImageViewSave.setVisibility(View.VISIBLE);
        mImageViewUndo.setVisibility(View.VISIBLE);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.save_undo_in);
        mLinearLayoutSaveUndo.startAnimation(mAnimation);
    }

    private void saveUndoOut() {
        mLinearLayoutSaveUndo.setVisibility(View.GONE);
        mImageViewSave.setVisibility(View.GONE);
        mImageViewUndo.setVisibility(View.GONE);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.save_undo_out);
        mLinearLayoutSaveUndo.startAnimation(mAnimation);
    }

    private void cancelEffectIn() {
        mLinearLayoutCancelEffect.setVisibility(View.VISIBLE);
        mImageViewCancelEffect.setVisibility(View.VISIBLE);
        mImageViewSelectEffect.setVisibility(View.VISIBLE);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.save_undo_in);
        mLinearLayoutCancelEffect.startAnimation(mAnimation);
    }

    private void cancelEffectOut() {
        mLinearLayoutCancelEffect.setVisibility(View.GONE);
        mImageViewCancelEffect.setVisibility(View.GONE);
        mImageViewSelectEffect.setVisibility(View.GONE);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.save_undo_out);
        mLinearLayoutCancelEffect.startAnimation(mAnimation);
    }

    private void backToChooseImage() {
        DialogInterface.OnClickListener positiveClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        };
        DialogInterface.OnClickListener negativeClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearData();
                finish();
            }
        };
        DialogUtils.showDialog(this,
                getResources().getString(R.string.dialog_title_back_to_choose_image),
                getResources().getString(R.string.dialog_message_back_to_choose_image),
                getResources().getString(R.string.dialog_message_no),
                getResources().getString(R.string.dialog_message_yes), positiveClickListener, negativeClickListener);
    }

    private void clearData() {
        mImageView.setImageURI(null);
        Intent intent = new Intent(PhotoActivity.this, ChoosePhotoActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        mEffectSelect = null;
        if (mBitmap != null && !mBitmap.isRecycled()) mBitmap.recycle();
        if (mBitmaps.size() > 0) mBitmaps.clear();
        if (mEffects.size() > 0) mEffects.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Crop.REQUEST_CROP) {
            mCropLib.handleCrop(this, mImageView, resultCode, data);
        }
    }

    /**
     * Class Events
     */
    private class seekBarEvents implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.seekBar_hue:
                    mValueEffect = mSeekBarHue.getProgress() - VALUE_PROGRESS_HUE;
                    mTextViewValueHue.setText(String.valueOf(mValueEffect));
                    break;
                case R.id.seekBar_bright:
                    mValueEffect = mSeekBarBright.getProgress() - VALUE_PROGRESS_BRIGHT;
                    mTextViewValueBright.setText(String.valueOf(mValueEffect));
                    break;
                case R.id.seekBar_contrast:
                    mValueEffect = mSeekBarContrast.getProgress() - VALUE_PROGRESS_CONTRAST;
                    mTextViewValueContrast.setText(String.valueOf(mValueEffect));
                    break;
                case R.id.seekBar_highlight:
                    mTextViewValueHighlight.setText(String.valueOf(mSeekBarHighlight.getProgress()));
                    break;
                case R.id.seekBar_grey_scale:
                    mValueEffect = VALUE_MAX_GREY_SCALE - mSeekBarGreyScale.getProgress();
                    mTextViewValueGreyScale.setText(String.valueOf(mSeekBarGreyScale.getProgress()));
                    break;
                case R.id.seekBar_sepia:
                    mValueEffect = VALUE_MAX_SEPIA - mSeekBarSepia.getProgress();
                    mTextViewValueSepia.setText(String.valueOf(mSeekBarSepia.getProgress()));
                    break;
            }
            mEffectSelect.setValue(mValueEffect);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            applyEffect();
        }
    }

    private class ImageEvents implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageView_cancel_effect:
                    mEffectSelect.setName("");
                    mEffectSelect.setValue(0);
                    mImageView.setImageBitmap(mBitmap);
                    cancelEffectOut();
                    displayListEffects();
                    break;
                case R.id.imageView_select_effect:
                    mEffects.add(mEffectSelect);
                    mBitmaps.add(mBitmap);
                    mBitmap = getBitmap();
                    cancelEffectOut();
                    displayListEffects();
                    break;
                case R.id.imageView_undo:
                    if (mBitmaps.size() <= 0)
                        Toast.makeText(PhotoActivity.this, R.string.toast_end_effect_undo, Toast.LENGTH_SHORT).show();
                    else {
                        int indexEnd = mBitmaps.size() - 1;
                        mImageView.setImageBitmap(mBitmaps.get(indexEnd));
                        mBitmaps.remove(indexEnd);
                    }
                    break;
                case R.id.imageView_save:
                    mBitmap = getBitmap();
                    SaveImageAsync saveImageAsync = new SaveImageAsync(PhotoActivity.this);
                    saveImageAsync.execute(mBitmap);
                    break;
                case R.id.fab_crop:
                    mBitmap = getBitmap();
                    mCropLib.beginCrop(PhotoActivity.this, mCropLib.bmpToUri(PhotoActivity.this, mBitmap));
                    break;
                case R.id.fab_rotate:
                    mBitmap = getBitmap();
                    String urlImage = UriLibrary.UriToUrl(getApplicationContext(), mImageUri);
                    mBitmap = BitmapUtil.rotate(mBitmap, BitmapUtil.ORIENTATION_ROTATE_90);
                    mImageView.setImageBitmap(mBitmap);
                    break;
            }
        }
    }
}
