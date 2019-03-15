// ISaltGeneratorInterface.aidl
package com.github.ggaier.wb_binder;

// Declare any non-default types here with import statements
import com.github.ggaier.wb_binder.ISaltListener;

interface ISaltGeneratorInterface {

    oneway void generateSalt(String prefix, in ISaltListener listener) = 10;
}
