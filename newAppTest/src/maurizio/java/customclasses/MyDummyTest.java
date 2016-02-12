package customclasses;

import android.app.Activity;

import com.pietrantuono.tests.superclass.Test;

import ioio.lib.api.IOIO;

public class MyDummyTest extends Test{


    MyDummyTest(Activity activity, IOIO ioio, String description, Boolean isBlockingTest) {
        super(activity, ioio, description, false, isBlockingTest, 0, 0, 0);
    }

    @Override
    public void execute() {
        try {
            Thread.sleep(2*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        activityListener.addFailOrPass("",istest,success,description);
    }

    public static class Builder {
        private Activity activity;
        private IOIO ioio;
        private String description;
        private Boolean isBlockingTest;

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setIoio(IOIO ioio) {
            this.ioio = ioio;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setIsBlockingTest(Boolean isBlockingTest) {
            this.isBlockingTest = isBlockingTest;
            return this;
        }

        public MyDummyTest createMyDummyTest() {
            return new MyDummyTest(activity, ioio, description, isBlockingTest);
        }
    }
}
