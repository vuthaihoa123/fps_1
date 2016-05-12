package framgia.vn.photoSketch.library;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by hoavt on 4/21/2016.
 */
public class ZoomLibrary {
    // The 3 states (events) which the user is trying to perform
    public static final int NONE = 0;
    public static final int DRAG = 1;
    public static final int ZOOM = 2;
    @SuppressWarnings("unused")
    public static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;
    public static final int VALUES_OF_MATRIX = 9;
    // These matrices will be used to scale points of the image
    private Matrix mMatrix = new Matrix();
    private Matrix mSavedMatrix = new Matrix();
    private int mMode = NONE;
    // These PointF objects are used to record the point(s) the user is touching
    private PointF mStartPoint = new PointF();
    private PointF mMidPoint = new PointF();
    private float mOldDist = 1f;
    private float mOriginWidth = 0, mOriginHeight = 0;
    private boolean mIsFirstAction = false;

    public void zoom(ImageView ivTarget) {
//        ivTarget.setImageURI(Uri.fromFile(new File("/storage/emulated/0/DCIM/Camera/IMG_20160211_150305.jpg")));
        ivTarget.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                view.setScaleType(ImageView.ScaleType.MATRIX);
                float scale = 0;
                // Handle touch events here...
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:   // first finger down only
                        mMatrix.set(view.getImageMatrix());
                        mSavedMatrix.set(mMatrix);
                        mStartPoint.set(event.getX(), event.getY());
                        mMode = DRAG;
                        break;
                    case MotionEvent.ACTION_UP: // first finger lifted
                    case MotionEvent.ACTION_POINTER_UP: // second finger lifted
                        mMode = NONE;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down
                        float newDist = -1;
                        mOldDist = spacing(event);
                        if (mOldDist > 5f) {
                            mSavedMatrix.set(mMatrix);
                            midPoint(mMidPoint, event);
                            mMode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mMode == DRAG) {
                            float pointX = event.getX() - mStartPoint.x;
                            float pointY = event.getY() - mStartPoint.y;
                            mMatrix.set(mSavedMatrix);
                            // create the transformation in the mMatrix  of points
                            mMatrix.postTranslate(pointX, pointY);
                        } else if (mMode == ZOOM) {
                            // pinch zooming
                            newDist = spacing(event);
                            if (newDist > 5f) {
                                mMatrix.set(mSavedMatrix);
                                scale = newDist / mOldDist; // setting the scaling of the
                                // mMatrix
                                // zoom in...if scale < 1 means
                                // zoom out...if scale > 1 means
                                mMatrix.postScale(scale, scale, mMidPoint.x, mMidPoint.y);
                            }
                        }
                        break;
                }
                if ((!restrictZoom(view))) {
                    view.setImageMatrix(mMatrix); // display the transformation on screen
                }
                return true; // indicate event was handled
            }
        });
    }

    /**
     * If zooming-in make size of image decreased , then reset image to the origin image
     *
     * @param view : image view
     * @return boolean : true if reset size of image, false if otherwise
     */
    private boolean restrictZoom(View view) {
        int imageWidth = view.getWidth();
        int imageHeight = view.getHeight();
        float[] values = new float[VALUES_OF_MATRIX];
        mMatrix.getValues(values);
        float width = values[Matrix.MSCALE_X] * imageWidth;
        float height = values[Matrix.MSCALE_Y] * imageHeight;
        // Get origin values of matrix
        if (!mIsFirstAction) {
            mOriginWidth = width;
            mOriginHeight = height;
            mIsFirstAction = true;
        }
        // Reset image to origin size when zoom in
        if (width < (mOriginWidth / 2) || height < (mOriginHeight / 2))
            return true;
        return false;
    }

    private boolean restrictDrag(View view) {
        int imageWidth = view.getWidth();
        int imageHeight = view.getHeight();
        float[] values = new float[VALUES_OF_MATRIX];
        mMatrix.getValues(values);
//        float MPERSP_0 = values[Matrix.MPERSP_0];
//        float MPERSP_1 = values[Matrix.MPERSP_1];
//        float MPERSP_2 = values[Matrix.MPERSP_2];
//        float MSKEW_X = values[Matrix.MSKEW_X];
//        float MSKEW_Y = values[Matrix.MSKEW_Y];
        float globalX = values[Matrix.MTRANS_X];
        float globalY = values[Matrix.MTRANS_Y];
        if (globalX >= imageWidth / 2 || (globalX + imageWidth / 2) <= 0
                || globalY >= imageHeight / 2 || (globalY + imageHeight / 2) <= 0)
            return true;
        return false;
    }

    /**
     * Description: checks the spacing between the two fingers on touch
     *
     * @param : MotionEvent
     * @return : float Description:
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Description: calculates the midpoint between the two fingers
     *
     * @param: PointF object
     * @param: MotionEvent
     * @return: void
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
