package com.pietrantuono.fragments.sequence;

import android.support.annotation.IntDef;

import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.constants.NewSequenceInterface;

import server.pojos.Test;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
@SuppressWarnings("unused")
public class SequenceRowElement {

    public static abstract class RowElement{
        final NewSequenceInterface sequence;

        public RowElement(NewSequenceInterface sequence) {
            this.sequence = sequence;
        }

        @SuppressWarnings("unused")
        public NewSequenceInterface getSequence(){
            return sequence;
        };
    }

    public static class TestRowElement extends RowElement{
        private final boolean istest;
        private final boolean success;
        private final String reading;
        private final String otherreading;
        private final String description;
        private final boolean isSensorTest;
        private final Test testToBeParsed;

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

        @SuppressWarnings("unused")
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

        public SensorTestRowElement(NewSequenceInterface sequence) {
            super(sequence);
        }

        public NewMSensorResult getmSensorResult() {
            return mSensorResult;
        }

        public void setmSensorResult(NewMSensorResult mSensorResult) {
            this.mSensorResult = mSensorResult;
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
        private final boolean istest;
        private final boolean success;
        private String failReason;

        public UploadRowElement(String description, boolean istest, boolean success,NewSequenceInterface sequence, String failReason) {
            super(sequence);
            this.description = description;
            this.istest = istest;
            this.success = success;
            this.failReason=failReason;
        }

        public String getDescription() {
            return description;
        }

        @SuppressWarnings("unused")
        public boolean istest() {
            return istest;
        }

        @SuppressWarnings("unused")
        public boolean isSuccess() {
            return success;
        }

        private final String description;

        @Override
        public NewSequenceInterface getSequence() {
            return sequence;
        }

        public String getFailReason() {
            return failReason;
        }

        public void setFailReason(String failReason) {
            this.failReason = failReason;
        }
    }
}
