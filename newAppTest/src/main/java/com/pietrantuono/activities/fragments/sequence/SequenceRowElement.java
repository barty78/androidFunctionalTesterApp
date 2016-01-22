package com.pietrantuono.activities.fragments.sequence;

import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.constants.NewSequenceInterface;

import server.pojos.Test;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class SequenceRowElement {

    public static abstract class RowElement{
        NewSequenceInterface sequence;

        public RowElement(NewSequenceInterface sequence) {
            this.sequence = sequence;
        }

        public NewSequenceInterface getSequence(){
            return sequence;
        };
    }

    public static class TestRowElement extends RowElement{
        private boolean istest;
        private boolean success;
        private String reading;
        private String otherreading;
        private String description;
        private boolean isSensorTest;
        private Test testToBeParsed;

        public TestRowElement(Boolean istest, Boolean success, String reading, String otherreading, String description, boolean isSensorTest, Test testToBeParsed, NewSequenceInterface sequence) {
            super(sequence);
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

        @Override
        public NewSequenceInterface getSequence() {
            return sequence;
        }
    }

    public static class SensorTestRowElement extends RowElement{
        private NewMSensorResult mSensorResult;
        private Test testToBeParsed;

        public SensorTestRowElement(NewMSensorResult mSensorResult, Test testToBeParsed,NewSequenceInterface sequence) {
            super(sequence);
            this.mSensorResult = mSensorResult;
            this.testToBeParsed = testToBeParsed;

        }

        public NewMSensorResult getmSensorResult() {
            return mSensorResult;
        }

        public Test getTestToBeParsed() {
            return testToBeParsed;
        }

        @Override
        public NewSequenceInterface getSequence() {
            return sequence;
        }
    }

    public static class UploadRowElement extends RowElement{
        private boolean istest;
        private boolean success;

        public UploadRowElement(String description, boolean istest, boolean success,NewSequenceInterface sequence) {
            super(sequence);
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

        @Override
        public NewSequenceInterface getSequence() {
            return sequence;
        }
    }
}
