package com.pietrantuono.constants;

import hydrix.pfmat.generic.TEST;
import hydrix.pfmat.generic.TestLimits;

import java.util.ArrayList;

import com.pietrantuono.application.PeriCoachTestApplication;

import analytica.pericoach.android.DBManager;

public class LimitsProvider {

	public static ArrayList<TestLimits> getTestLimits() {
			DBManager db = (new DBManager(PeriCoachTestApplication.getContext()));
			ArrayList<TestLimits> limits= db.getLimitsforTest(TEST.OPEN_TEST);
			return limits;
	}

}
