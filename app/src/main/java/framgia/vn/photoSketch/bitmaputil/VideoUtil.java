package framgia.vn.photoSketch.bitmaputil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.view.Surface;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import framgia.vn.photoSketch.constants.ConstActivity;
import framgia.vn.photoSketch.models.Photo;

/**
 * Created by nghicv on 14/04/2016.
 */
public class VideoUtil {
    private static final String MIME_TYPE = "video/avc";
    private static final int BIT_RATE = 2000000;
    public static final int FRAMES_PER_SECOND = 60;
    private static final int IFRAME_INTERVAL = 10;
    private static final int TIME_OUT_USEC = 10000;
    private static final String VIDEO_OUTPUT_TYPE = ".mp4";
    private static final String EXCEPTION_FORMAT = "format changed twice";
    private static final String EXCEPTION_NULL = "data null";
    private static final String EXCEPTION_NOT_START = "muxer has not started";
    private static final int VIDEO_WIDTH = 1280;
    private static final int VIDEO_HEIGHT = 720;
    public static final String FOLDER_NAME = "video";
    public static final String FILE_NAME = "video_";
    private MediaCodec.BufferInfo mBufferInfo;
    private MediaCodec mEncoder;
    private MediaMuxer mMuxer;
    private Surface mInputSurface;
    public List<Bitmap> mBitmaps;
    private List<Photo> mPhotos ;
    private File mOutPutFile;
    private int mTrackIndex;
    private boolean mMuxerStarted;
    private long mFakePts;
    private Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private float dx;
    private float dy;
    private int mCurrentIndex = 0;
    private int mOldIndex = -1;
    private boolean mInitFinish = false;
    private OnUpdateProgressDialog mOnUpdateProgressDialog;

    public VideoUtil(List<Photo> listImage, OnUpdateProgressDialog onUpdateProgressDialog) {
        mPhotos = listImage;
        mOnUpdateProgressDialog = onUpdateProgressDialog;
        try {
            prepareEncoder();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String makeVideo() {
        try {
            int maxFrame = FRAMES_PER_SECOND * mPhotos.size();
            for (int i = 0; i < maxFrame; i++) {
                encode(false);
                int percent = (int)(100.0f * i / (float) maxFrame);
                mOnUpdateProgressDialog.update(percent);
                while (!mInitFinish) {
                    generateFrame(i);
                }
                mInitFinish = false;
            }
            encode(true);
        } finally {
            release();
        }
        return mOutPutFile.getAbsolutePath();
    }

    private void prepareEncoder() throws IOException {
        mBitmaps = new ArrayList<>();
        createOutputFile();
        for (int i = 0; i < mPhotos.size(); i++) {
            Bitmap bitmap = BitmapUtil.resize(mPhotos.get(i).getUri(), (int)VIDEO_WIDTH/4, (int)VIDEO_HEIGHT/4);
            mBitmaps.add(bitmap);
        }
        mBufferInfo = new MediaCodec.BufferInfo();
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, (int) VIDEO_WIDTH, (int) VIDEO_HEIGHT);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAMES_PER_SECOND);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mInputSurface = mEncoder.createInputSurface();
        mEncoder.start();
        mMuxer = new MediaMuxer(mOutPutFile.toString(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        mTrackIndex = -1;
        mMuxerStarted = false;
    }

    private void release() {
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mInputSurface != null) {
            mInputSurface.release();
            mInputSurface = null;
        }
        if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
    }

    private void encode(boolean isEnd) {
        if (isEnd) {
            mEncoder.signalEndOfInputStream();
        }
        ByteBuffer[] byteBuffers = mEncoder.getOutputBuffers();
        while (true) {
            int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIME_OUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!isEnd) {
                    break;
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                byteBuffers = mEncoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (mMuxerStarted) {
                    throw new RuntimeException(EXCEPTION_FORMAT);
                }
                MediaFormat newFormat = mEncoder.getOutputFormat();
                mTrackIndex = mMuxer.addTrack(newFormat);
                mMuxer.start();
                mMuxerStarted = true;
            } else if (encoderStatus >= 0) {
                ByteBuffer encodedData = byteBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException(EXCEPTION_NULL);
                }
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    mBufferInfo.size = 0;
                }
                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw new RuntimeException(EXCEPTION_NOT_START);
                    }
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
                    mBufferInfo.presentationTimeUs = mFakePts;
                    mFakePts += 1000000L / FRAMES_PER_SECOND;
                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                }
                mEncoder.releaseOutputBuffer(encoderStatus, false);
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    break;
                }
            }
        }
    }

    private void generateFrame(int position) {
        Canvas canvas = mInputSurface.lockCanvas(null);
        if (position % FRAMES_PER_SECOND == FRAMES_PER_SECOND - 1) {
            mOldIndex = mCurrentIndex;
            mCurrentIndex++;
            if (mCurrentIndex == mPhotos.size()) {
                mInitFinish = true;
                return;
            }
            dx = 0 - VIDEO_WIDTH;
        }
        translate(canvas, mBitmaps, mCurrentIndex, mOldIndex, dx, dy, paint, VIDEO_WIDTH, VIDEO_HEIGHT);
        dx += (int)(VIDEO_WIDTH/FRAMES_PER_SECOND);
        mInputSurface.unlockCanvasAndPost(canvas);
        mInitFinish = true;
    }

    private void translate(Canvas canvas, List<Bitmap> bitmaps, int currentIndex, int
            oldIndex, float dx, float dy, Paint paint, float screenWidth, float screenHeight) {
        canvas.drawARGB(255, 0, 0, 0);
        Matrix matrix = new Matrix();

        matrix.setTranslate(dx, dy);
        if (oldIndex != -1) {
            Matrix matrix2 = new Matrix();
            matrix2.setTranslate((dx + VIDEO_WIDTH)/2, dy);
            canvas.drawBitmap(bitmaps.get(oldIndex), matrix2, paint);
        }
        canvas.drawBitmap(bitmaps.get(currentIndex), matrix, paint);
    }

    public interface OnUpdateProgressDialog{
        public void update(int value);
    }

    private void createOutputFile() {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + ConstActivity.ROOT_FOLDER + "/" + FOLDER_NAME;
        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdir();
        String fileName = FILE_NAME + String.valueOf(System.currentTimeMillis()) + VIDEO_OUTPUT_TYPE;
        mOutPutFile = new File(dir, fileName);
    }
}
