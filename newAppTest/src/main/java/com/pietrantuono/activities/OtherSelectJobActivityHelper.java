package com.pietrantuono.activities;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.BuildConfig;
import com.pietrantuono.pericoach.newtestapp.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import server.RetrofitRestServices;
import server.pojos.records.response.Response;

/**
 * Created by mauriziopietrantuono on 06/01/16.
 */
public class OtherSelectJobActivityHelper {
    private static final String TAG = "OtherSelectJobActivityr";

    public static void postTestsAndSepsXML(OtherSelectJobActivity context) {
        //int[] registerSequenceFragment = context.getResources().get
        Field[] ID_Fields = R.integer.class.getFields();
        int[] resourcesIdsArray = new int[ID_Fields.length];
        int[] testIDs = new int[ID_Fields.length];
        String[] resourcesStringArray= new String[ID_Fields.length];
        for(int i = 0; i < ID_Fields.length; i++) {
            try {
                resourcesIdsArray[i] = ID_Fields[i].getInt(null);
                resourcesStringArray[i]=ID_Fields[i].getName();
                testIDs[i]=context.getResources().getInteger(resourcesIdsArray[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<XMLTestsList.XMLTest> xmlTests= new ArrayList<>();
        for(int i=0;i<ID_Fields.length;i++){
            XMLTestsList.XMLTest xmlTest= new XMLTestsList.XMLTest();
            xmlTest.setTestid(testIDs[i]);
            xmlTest.setTestname(resourcesStringArray[i]);
            xmlTests.add(xmlTest);
        }
        XMLTestsList xmlTestsList= new XMLTestsList();
        xmlTestsList.setXMLTests(xmlTests);
        Gson gson= new Gson();
        String json=gson.toJson(xmlTestsList,XMLTestsList.class);
        RetrofitRestServices.getRest(context).postXMLTests(PeriCoachTestApplication.getDeviceid(), xmlTestsList, new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {
                if(BuildConfig.DEBUG)Log.d(TAG, "Ok XML test posted successfully");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, retrofitError.toString());
            }
        });
        foo();
    }

    private static void foo() { }



    @SuppressWarnings("unused")
    public static class XMLTestsList {
        @SerializedName("tests")
        @Expose
        private List<XMLTest> XMLTests = new ArrayList<XMLTest>();

        public List<XMLTest> getXMLTests() {
            return XMLTests;
        }

        public void setXMLTests(List<XMLTest> XMLTests) {
            this.XMLTests = XMLTests;
        }

        public static class XMLTest {

            @SerializedName("testname")
            @Expose
            private String testname;
            @SerializedName("testid")
            @Expose
            private long testid;

            public String getTestname() {
                return testname;
            }

            public void setTestname(String testname) {
                this.testname = testname;
            }

            public long getTestid() {
                return testid;
            }

            public void setTestid(long testid) {
                this.testid = testid;
            }

        }

    }
}
