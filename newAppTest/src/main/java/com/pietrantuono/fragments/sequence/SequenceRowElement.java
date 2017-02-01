package com.pietrantuono.fragments.sequence;

import com.pietrantuono.constants.SensorResult;
import com.pietrantuono.constants.SequenceInterface;

import server.pojos.Test;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
@SuppressWarnings("unused")
public class SequenceRowElement {

    public static abstract class RowElement{
        final SequenceInterface sequence;

        public RowElement(SequenceInterface sequence) {
            this.sequence = sequence;
        }

        @SuppressWarnings("unused")
        public SequenceInterface getSequence(){
            return sequence;
        }
    }

    public static class TestRowElement extends RowElement{
        private final boolean istest;
        private final boolean success;
        private final String reading;
        private final String otherreading;
        private final String description;
        private final boolean isSensorTest;
        private final Test testToBeParsed;

        public TestRowElement(Boolean istest, Boolean success, String reading, String otherreading, String description, boolean isSensorTest, Test testToBeParsed, SequenceInterface sequence) {
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
        public SequenceInterface getSequence() {
            return sequence;
        }
    }

    public static class SensorTestRowElement extends RowElement{
        private SensorResult mSensorResult;
        private Test testToBeParsed;

        public SensorTestRowElement(SensorResult mSensorResult, Test testToBeParsed, SequenceInterface sequence) {
            super(sequence);
            this.mSensorResult = mSensorResult;
            this.testToBeParsed = testToBeParsed;

        }

        public SensorTestRowElement(SequenceInterface sequence) {
            super(sequence);
        }

        public SensorResult getmSensorResult() {
            return mSensorResult;
        }

        public void setmSensorResult(SensorResult mSensorResult) {
            this.mSensorResult = mSensorResult;
        }

        public Test getTestToBeParsed() {
            return testToBeParsed;
        }

        @Override
        public SequenceInterface getSequence() {
            return sequence;
        }
    }

    public static class UploadRowElement extends RowElement{
        private final boolean istest;
        private final boolean success;
        private String failReason;

        public UploadRowElement(String description, boolean istest, boolean success, SequenceInterface sequence, String failReason) {
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
        public SequenceInterface getSequence() {
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
