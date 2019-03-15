// ISaltListener.aidl
package com.github.ggaier.wb_binder;

// Declare any non-default types here with import statements

interface ISaltListener {

    oneway void onSaltGened(String salt);

}
