package gws.grottworkshop.gwsomega.graphics.drawable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;

import gws.grottworkshop.gwsomega.cache.BitmapLruCache;
import gws.grottworkshop.gwsomega.runnable.*;

public class CacheableBitmapDrawable extends BitmapDrawable {

	Logger GWSLOG = LoggerFactory.getLogger(CacheableBitmapDrawable.class);
	
	static final int UNUSED_DRAWABLE_RECYCLE_DELAY_MS = 2000;

    // URL Associated with this Bitmap
    private final String mUrl;

    private BitmapLruCache.RecyclePolicy mRecyclePolicy;

    // Number of Views currently displaying bitmap
    private int mDisplayingCount;

    // Has it been displayed yet
    private boolean mHasBeenDisplayed;

    // Number of caches currently referencing the wrapper
    private int mCacheCount;

    // The CheckStateRunnable currently being delayed
    private Runnable mCheckStateRunnable;

    // Throwable which records the stack trace when we recycle
    private Throwable mStackTraceWhenRecycled;

    // Handler which may be used later
    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    CacheableBitmapDrawable(String url, Resources resources, Bitmap bitmap,
            BitmapLruCache.RecyclePolicy recyclePolicy) {
        super(resources, bitmap);

        mUrl = url;
        mRecyclePolicy = recyclePolicy;
        mDisplayingCount = 0;
        mCacheCount = 0;
    }

    @Override
    public void draw(Canvas canvas) {
        try {
            super.draw(canvas);
        } catch (RuntimeException re) {
            // A RuntimeException has been thrown, probably due to a recycled Bitmap. If we have
            // one, print the method stack when the recycle() call happened
            if (null != mStackTraceWhenRecycled) {
                mStackTraceWhenRecycled.printStackTrace();
            }

            // Finally throw the original exception
            throw re;
        }
    }

    /**
     * @return Amount of heap size currently being used by {@code Bitmap}
     */
    int getMemorySize() {
        int size = 0;

        final Bitmap bitmap = getBitmap();
        if (null != bitmap && !bitmap.isRecycled()) {
            size = bitmap.getRowBytes() * bitmap.getHeight();
        }

        return size;
    }

    /**
     * @return the URL associated with the BitmapDrawable
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Returns true when this wrapper has a bitmap and the bitmap has not been recycled.
     *
     * @return true - if the bitmap has not been recycled.
     */
    public synchronized boolean hasValidBitmap() {
        Bitmap bitmap = getBitmap();
        return null != bitmap && !bitmap.isRecycled();
    }

    /**
     * @return true - if the bitmap is currently being displayed by a {@link CacheableImageView}.
     */
    public synchronized boolean isBeingDisplayed() {
        return mDisplayingCount > 0;
    }

    /**
     * @return true - if the wrapper is currently referenced by a cache.
     */
    public synchronized boolean isReferencedByCache() {
        return mCacheCount > 0;
    }

    /**
     * Used to signal to the Drawable whether it is being used or not.
     *
     * @param beingUsed - true if being used, false if not.
     */
    public synchronized void setBeingUsed(boolean beingUsed) {
        if (beingUsed) {
            mDisplayingCount++;
            mHasBeenDisplayed = true;
        } else {
            mDisplayingCount--;
        }
        checkState();
    }

    /**
     * Used to signal to the wrapper whether it is being referenced by a cache or not.
     *
     * @param added - true if the wrapper has been added to a cache, false if removed.
     */
    synchronized void setCached(boolean added) {
        if (added) {
            mCacheCount++;
        } else {
            mCacheCount--;
        }
        checkState();
    }

    private void cancelCheckStateCallback() {
        if (null != mCheckStateRunnable) {
            
                GWSLOG.debug( "Cancelling checkState() callback for: " + mUrl);
            
            sHandler.removeCallbacks(mCheckStateRunnable);
            mCheckStateRunnable = null;
        }
    }

    /**
     * Calls {@link #checkState(boolean)} with default parameter of <code>false</code>.
     */
    private void checkState() {
        checkState(false);
    }

    /**
     * Checks whether the wrapper is currently referenced by a cache, and is being displayed. If
     * neither of those conditions are met then the bitmap is ready to be recycled. Whether this
     * happens now, or is delayed depends on whether the Drawable has been displayed or not. <ul>
     * <li>If it has been displayed, it is recycled straight away.</li> <li>If it has not been
     * displayed, and <code>ignoreBeenDisplayed</code> is <code>false</code>, a call to
     * <code>checkState(true)</code> is queued to be called after a delay.</li> <li>If it has not
     * been displayed, and <code>ignoreBeenDisplayed</code> is <code>true</code>, it is recycled
     * straight away.</li> </ul>
     *
     * @param ignoreBeenDisplayed - Whether to ignore the 'has been displayed' flag when deciding
     *                            whether to recycle() now.
     * @see Constants#UNUSED_DRAWABLE_RECYCLE_DELAY_MS
     */
    private synchronized void checkState(final boolean ignoreBeenDisplayed) {
        
            GWSLOG.debug( String.format(
                    "checkState(). Been Displayed: %b, Displaying: %d, Caching: %d, URL: %s",
                    mHasBeenDisplayed, mDisplayingCount, mCacheCount, mUrl));
        

        // If the policy doesn't let us recycle, return now
        if (!mRecyclePolicy.canRecycle()) {
            return;
        }

        // Cancel the callback, if one is queued.
        cancelCheckStateCallback();

        // We're not being referenced or used anywhere
        if (mCacheCount <= 0 && mDisplayingCount <= 0 && hasValidBitmap()) {

            /**
             * If we have been displayed or we don't care whether we have
             * been or not, then recycle() now. Otherwise, we retry after a delay.
             */
            if (mHasBeenDisplayed || ignoreBeenDisplayed) {
                
                   GWSLOG.debug( "Recycling bitmap with url: " + mUrl);
                
                // Record the current method stack just in case
                mStackTraceWhenRecycled = new Throwable("Recycled Bitmap Method Stack");

                getBitmap().recycle();
            } else {
                
                    GWSLOG.debug(
                            "Unused Bitmap which hasn't been displayed, delaying recycle(): "
                                    + mUrl);
                
                mCheckStateRunnable = new CheckStateRunnable(this);
                sHandler.postDelayed(mCheckStateRunnable,
                        UNUSED_DRAWABLE_RECYCLE_DELAY_MS);
            }
        }
    }

    /**
     * Runnable which run a {@link CacheableBitmapDrawable#checkState(boolean) checkState(false)}
     * call.
     *
     * @author chrisbanes
     */
    private static final class CheckStateRunnable
            extends WeakReferenceRunnable<CacheableBitmapDrawable> {

        public CheckStateRunnable(CacheableBitmapDrawable object) {
            super(object);
        }

        @Override
        public void run(CacheableBitmapDrawable object) {
            object.checkState(true);
        }
    }

}
