package gws.grottworkshop.gwsomega.cache;

import java.io.File;
import java.io.IOException;

import android.os.Build;

import com.wuman.twolevellrucache.TwoLevelLruCache;


// TODO: Auto-generated Javadoc
/**
 * The Class BitmapLruCache, modified from Chris Banes AndroidBitmapCache
 *
 * @param <V> the value type
 */
public class BitmapLruCache<V> extends TwoLevelLruCache<V> {
	
	/**
     * The recycle policy controls if the {@link android.graphics.Bitmap#recycle()} is automatically
     * called, when it is no longer being used. To set this, use the {@link
     * Builder#setRecyclePolicy(uk.co.senab.bitmapcache.BitmapLruCache.RecyclePolicy)
     * Builder.setRecyclePolicy()} method.
     */
    public static enum RecyclePolicy {
        /**
         * The Bitmap is never recycled automatically.
         */
        DISABLED,

        /**
         * The Bitmap is only automatically recycled if running on a device API v10 or earlier.
         */
        PRE_HONEYCOMB_ONLY,

        /**
         * The Bitmap is always recycled when no longer being used. This is the default.
         */
        ALWAYS;

        /**
         * Can recycle.
         *
         * @return true, if successful
         */
        public boolean canRecycle() {
            switch (this) {
                case DISABLED:
                    return false;
                case PRE_HONEYCOMB_ONLY:
                    return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;
                case ALWAYS:
                    return true;
            }

            return false;
        }
    }


	/**
	 * Instantiates a new bitmap lru cache.
	 *
	 * @param directory the directory
	 * @param appVersion the app version
	 * @param maxSizeMem the max size mem
	 * @param maxSizeDisk the max size disk
	 * @param converter the converter
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public  BitmapLruCache(File directory, int appVersion, int maxSizeMem,
			long maxSizeDisk, @SuppressWarnings("rawtypes") Converter converter) throws IOException {
		super(directory, appVersion, maxSizeMem, maxSizeDisk, converter);
		// TODO Auto-generated constructor stub
	}

}
