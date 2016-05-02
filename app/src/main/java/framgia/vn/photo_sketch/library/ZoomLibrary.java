package framgia.vn.photo_sketch.library;

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
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;
    // These matrices will be used to scale points of the image
    private Matrix mMatrix = new Matrix();
    private Matrix mSavedMatrix = new Matrix();
    private int mMode = NONE;
    // tThese PointF objects are used to record the point(s) the user is touching
    private PointF mStartPoint = new PointF();
    private PointF mMidPoint = new PointF();
    private float mOldDist = 1f;

    public void zoom(ImageView ivTarget) {
//        ivTarget.setImageURI(Uri.fromFile(new File("/storage/emulated/0/DCIM/Camera/IMG_20160211_150305.jpg")));
        ivTarget.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                view.setScaleType(ImageView.ScaleType.MATRIX);
                float scale;
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
                            float newDist = spacing(event);
                            if (newDist > 5f) {
                                mMatrix.set(mSavedMatrix);
                                scale = newDist / mOldDist; // setting the scaling of the
                                // mMatrix...if scale > 1 means
                                // zoom in...if scale < 1 means
                                // zoom out
                                mMatrix.postScale(scale, scale, mMidPoint.x, mMidPoint.y);
                            }
                        }
                        break;
                }
                view.setImageMatrix(mMatrix); // display the transformation on screen
                return true; // indicate event was handled
            }
        });
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
