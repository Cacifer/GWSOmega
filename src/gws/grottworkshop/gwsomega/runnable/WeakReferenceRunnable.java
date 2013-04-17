/*******************************************************************************
 * Copyright (c) 2013 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gws.grottworkshop.gwsomega.runnable;

import java.lang.ref.WeakReference;

// TODO: Auto-generated Javadoc
/**
 * The Class WeakReferenceRunnable, from Chris Banes AndroidBitmapCache.
 *
 * @param <T> the generic type
 */
public abstract class WeakReferenceRunnable<T> implements Runnable {

    /** The m object ref. */
    private final WeakReference<T> mObjectRef;

    /**
     * Instantiates a new weak reference runnable.
     *
     * @param object the object
     */
    public WeakReferenceRunnable(T object) {
        mObjectRef = new WeakReference<T>(object);
    }

    /**
     * Run.
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public final void run() {
        T object = mObjectRef.get();

        if (null != object) {
            run(object);
        }
    }

    /**
     * Run.
     *
     * @param object the object
     */
    public abstract void run(T object);

}
