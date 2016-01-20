package com.pietrantuono.activities.fragments.sequence;

import com.pietrantuono.constants.NewMSensorResult;

import server.pojos.Test;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class SequenceRowElement {

    public interface RowElement{}


    static class TestRowElement implements RowElement{
        private boolean istest;
        private boolean success;
        private String reading;
        private String otherreading;
        private String description;
        private boolean isSensorTest;
        private Test testToBeParsed;

        public TestRowElement(Boolean istest, Boolean success, String reading, String otherreading, String description, boolean isSensorTest, Test testToBeParsed) {
            this.description = description;
            this.isSensorTest = isSensorTest;
            this.istest = istest;
            this.otherreading = otherreading;
            this.reading = reading;
            this.success = success;
            this.testToBeParsed = testToBeParsed;
        }

        public String getDescription() {
            return description;
        }

        public boolean isSensorTest() {
            return isSensorTest;
        }

        public boolean istest() {
            return istest;
        }

        public String getOtherreading() {
            return otherreading;
        }

        public String getReading() {
            return reading;
        }

        public boolean isSuccess() {
            return success;
        }

        public Test getTestToBeParsed() {
            return testToBeParsed;
        }
    }

    static class SensorTestRowElement implements RowElement{
        private NewMSensorResult mSensorResult;
        private Test testToBeParsed;

        public SensorTestRowElement(NewMSensorResult mSensorResult, Test testToBeParsed) {
            this.mSensorResult = mSensorResult;
            this.testToBeParsed = testToBeParsed;
        }

        public NewMSensorResult getmSensorResult() {
            return mSensorResult;
        }

        public Test getTestToBeParsed() {
            return testToBeParsed;
        }
    }

    static class UploadRowElement implements RowElement{
        private boolean istest;
        private boolean success;

        public UploadRowElement(String description, boolean istest, boolean success) {
            this.description = description;
            this.istest = istest;
            this.success = success;
        }

        public String getDescription() {
            return description;
        }

        public boolean istest() {
            return istest;
        }

        public boolean isSuccess() {
            return success;
        }

        private String description;

    }
}
