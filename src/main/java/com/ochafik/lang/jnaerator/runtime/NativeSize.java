//
// http://code.google.com/p/jnaerator/source/browse/trunk/jnaerator-runtime/src/main/java/com/ochafik/lang/jnaerator/runtime/NativeSize.java?r=950
//
package com.ochafik.lang.jnaerator.runtime;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;

/**
 * 'size_t' C type (32 bits on 32 bits platforms, 64 bits on 64 bits platforms).
 * Can be also used to model the 'long' C type for libraries known to be compiled with GCC or LLVM even on Windows.
 * (NativeLong on Windows is only okay with MSVC++ libraries, as 'long' on Windows 64 bits will be 32 bits with MSVC++ and 64 bits with GCC/mingw)
 * @author ochafik
 */
public class NativeSize extends IntegerType {
    /** Size of a size_t integer, in bytes. */
    public static int SIZE = Native.SIZE_T_SIZE;//Platform.is64Bit() ? 8 : 4;

    /** Create a zero-valued Size. */
    public NativeSize() {
        this(0);
    }

    /** Create a Size with the given value.
     * 
     * @param value
     */
    public NativeSize(long value) {
        super(SIZE, value);
    }
}

