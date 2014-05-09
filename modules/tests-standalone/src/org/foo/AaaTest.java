/**
 * 
 */
package org.foo;

import android.test.AndroidTestCase;
import android.util.Log;

public class AaaTest extends AndroidTestCase {
    public void testA() throws Exception {
        Log.wtf("foobar", "loggableE = " + Log.isLoggable("foobar", Log.ERROR));
        Log.wtf("foobar", "loggableW = " + Log.isLoggable("foobar", Log.WARN));
        Log.wtf("foobar", "loggableI = " + Log.isLoggable("foobar", Log.INFO));
        Log.wtf("foobar", "loggableD = " + Log.isLoggable("foobar", Log.DEBUG));
        Log.wtf("foobar", "loggableV = " + Log.isLoggable("foobar", Log.VERBOSE));
        Log.e("foobar", "this is error");
        Log.w("foobar", "this is warning");
        Log.i("foobar", "this is info");
        Log.d("foobar", "this is debug");
        Log.v("foobar", "this is verbose");
        Log.e("foobar", "this is error again");
        Log.wtf("foobar", "this is wtf");
    }
}
