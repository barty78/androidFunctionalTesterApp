package hydrix.pfmat.generic;

import java.util.ArrayList;

/**
 * Created by mauriziopietrantuono on 09/12/15.
 */
public interface SessionRunner {
	// Constants
    int SAMPLING_HZ = 10;

	float sensorAvg(int sensor, int windowSize);

    void beginBaselineCalibration(Device device);

    boolean endBaselineCalibration();

    void sampleAll(float sensor0, float sensor1, float sensor2,
                   int numSamples);

    void sampleIndividually(float sensor0, float sensor1,
                            float sensor2, int numSamples, Boolean display);

    Boolean clearLast();

    Result compareForces();

    void nextTest();

    void finaliseTesting();

    boolean start(Device device);

    void stop();

    ArrayList<Boolean> prepareResults(
            ArrayList<TestLimits> testLimits, TestSamples testSamples);

    void removeLast();

    String getDeviceId();

    SessionSamples getSamples();

    TestSamples getTestSamples();

    ArrayList<Result[]> getTestResults();

    ArrayList<DisplaySample> getDisplaySamples();

    Force getUserBaseline();

    Force getUserMaxForce();

    Force getUserMinForce();

    long getStartTimeMS();

    Integer getInputCount();

    void setInputCount(Integer inputCount);

    Integer getTestCount();

    void setTestCount(Integer testCount);

    void onSample(int timeOffsetMS, short sensor0, short sensor1,
                  short sensor2);

    int getmTestCounter();

    void loadTestresults();

    void setCounteToZero();

    void updateSamples();

    void startSession();

    void setmTestCounter(int i);

    Boolean clearLastOpenTest();
}
