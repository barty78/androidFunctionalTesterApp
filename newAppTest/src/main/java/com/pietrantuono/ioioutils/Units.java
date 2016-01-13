package com.pietrantuono.ioioutils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
@IntDef({Units.mA, Units.uA, Units.nA, Units.V, Units.percent, Units.NULL})
@Retention(RetentionPolicy.SOURCE)
public @interface Units {
	int mA = 3;
	int uA = 6;
	int nA = 9;
	int V = -1;
	int percent = -2;
	int NULL = -3;
}
