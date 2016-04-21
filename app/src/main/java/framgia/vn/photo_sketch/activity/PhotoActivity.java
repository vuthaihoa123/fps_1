package framgia.vn.photo_sketch.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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

import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.List;

import framgia.vn.photo_sketch.R;
import framgia.vn.photo_sketch.asynctask.ApplyEffectAsync;
import framgia.vn.photo_sketch.asynctask.DisplayBitmapAsync;
import framgia.vn.photo_sketch.asynctask.SaveImageAsync;
import framgia.vn.photo_sketch.constants.ConstEffects;
import framgia.vn.photo_sketch.library.UriLibrary;
import framgia.vn.photo_sketch.models.Effect;

public class PhotoActivity extends AppCompatActivity implements ConstEffects {
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
    private TextView mTextViewValueInvert;
    private SeekBar mSeekBarInvert;
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
    ApplyEffectAsync mApplyEffectAsync;

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
        super.onBackPressed();
        mImageView.setImageURI(null);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
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
        mImageView = (ImageView) findViewById(R.id.source_image);
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
        /* Invert */
        mTextViewValueInvert = (TextView) findViewById(R.id.textView_value_invert);
        mSeekBarInvert = (SeekBar) findViewById(R.id.seekBar_invert);
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
    }

    private void setEvents() {
        mImageViewCancelEffect.setOnClickListener(new ImageEvents());
        mImageViewSelectEffect.setOnClickListener(new ImageEvents());
        mImageView.setOnClickListener(new ImageEvents());
        mImageViewSave.setOnClickListener(new ImageEvents());
        mImageViewUndo.setOnClickListener(new ImageEvents());
    }

    private void loadImage() {
        String urlImage = UriLibrary.UriToUrl(getApplicationContext(), mImageUri);
        DisplayBitmapAsync bitmap = new DisplayBitmapAsync(this);
        bitmap.execute(urlImage);
    }

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
                mTextViewValueInvert.setText(String.valueOf(mSeekBarInvert.getProgress()));
                mSeekBarInvert.setOnSeekBarChangeListener(new seekBarEvents());
                break;
            case FILTER_SKETCH:
                mLinearLayoutFilterSketch.setVisibility(View.VISIBLE);
                applyEffect();
                break;
            case FILTER_VIGNETTE:
                mLinearLayoutFilterVignette.setVisibility(View.VISIBLE);
                applyEffect();
                break;
            case FILTER_SEPIA:
                mLinearLayoutFilterSepia.setVisibility(View.VISIBLE);
                applyEffect();
                break;
            case FILTER_GREY_SCALE:
                mLinearLayoutFilterGreyScale.setVisibility(View.VISIBLE);
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
                case R.id.seekBar_invert:
                    mTextViewValueInvert.setText(String.valueOf(mSeekBarInvert.getProgress()));
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
                    mBitmap = getBitmap();
                    cancelEffectOut();
                    displayListEffects();
                    break;
                case R.id.imageView_undo:
                    // TODO Undo Effect
                    break;
                case R.id.imageView_save:
                    mBitmap = getBitmap();
                    SaveImageAsync saveImageAsync = new SaveImageAsync(PhotoActivity.this);
                    saveImageAsync.execute(mBitmap);
                    break;
            }
        }
    }
}
