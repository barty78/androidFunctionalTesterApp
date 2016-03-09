package com.pietrantuono.pericoach.newtestapp.test.robotium;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import server.pojos.Job;

public class MockServer {
	private final static String response = "[ { \"id\": 1, \"testtype_id\": 1, \"test_id\": 1, \"jobno\": \"1234\", \"quantity\": 100, \"created_at\": \"2015-04-21T07:57:03+0000\", \"updated_at\": \"2015-08-28T01:43:16+0000\", \"islogging\": 1, \"active\": 0, \"description\": \"Standard Open TEST\" }, { \"id\": 5, \"testtype_id\": 1, \"test_id\": 1, \"jobno\": \"123\", \"quantity\": 1000, \"created_at\": \"2015-04-22T02:36:39+0000\", \"updated_at\": \"2015-08-28T01:43:21+0000\", \"islogging\": 1, \"active\": 1, \"description\": \"Standard Open TEST\" }, { \"id\": 6, \"testtype_id\": 1, \"test_id\": 9, \"jobno\": \"12345\", \"quantity\": 1000, \"created_at\": \"2015-04-22T04:24:08+0000\", \"updated_at\": \"2015-08-28T01:43:35+0000\", \"islogging\": 0, \"active\": 1, \"description\": \"Firmware Upload Only\" }, { \"id\": 11, \"testtype_id\": 1, \"test_id\": 1, \"jobno\": \"2000\", \"quantity\": 100, \"created_at\": \"2015-04-29T05:35:00+0000\", \"updated_at\": \"2015-08-28T01:43:44+0000\", \"islogging\": 1, \"active\": 1, \"description\": \"Standard Open TEST\" }, { \"id\": 14, \"testtype_id\": 1, \"test_id\": 1, \"jobno\": \"2222\", \"quantity\": 100, \"created_at\": \"2015-04-29T05:44:21+0000\", \"updated_at\": \"2015-08-28T01:43:52+0000\", \"islogging\": 1, \"active\": 1, \"description\": \"Standard Open TEST\" }, { \"id\": 15, \"testtype_id\": 1, \"test_id\": 1, \"jobno\": \"2221\", \"quantity\": 100, \"created_at\": \"2015-04-29T05:44:53+0000\", \"updated_at\": \"2015-07-07T23:14:58+0000\", \"islogging\": 0, \"active\": 1, \"description\": \"Standard Open TEST\" }, { \"id\": 16, \"testtype_id\": 1, \"test_id\": 1, \"jobno\": \"123456\", \"quantity\": 1000, \"created_at\": \"2015-06-17T02:18:49+0000\", \"updated_at\": \"2015-08-28T01:44:02+0000\", \"islogging\": 1, \"active\": 1, \"description\": \"Standard Open TEST\" }, { \"id\": 18, \"testtype_id\": 1, \"test_id\": 1, \"jobno\": \"3456\", \"quantity\": 1000, \"created_at\": \"2015-06-17T13:18:40+0000\", \"updated_at\": \"2015-07-07T23:15:07+0000\", \"islogging\": 0, \"active\": 1, \"description\": \"Standard Open TEST\" }, { \"id\": 19, \"testtype_id\": 1, \"test_id\": 1, \"jobno\": \"987654321\", \"quantity\": 900, \"created_at\": \"2015-06-18T06:01:42+0000\", \"updated_at\": \"2015-08-28T01:44:09+0000\", \"islogging\": 1, \"active\": 1, \"description\": \"Standard Open TEST\" }, { \"id\": 20, \"testtype_id\": 1, \"test_id\": 1, \"jobno\": \"33333\", \"quantity\": 2000, \"created_at\": \"2015-06-25T02:27:41+0000\", \"updated_at\": \"2015-07-07T23:15:17+0000\", \"islogging\": 0, \"active\": 1, \"description\": \"Standard Open TEST\" } ]";

	public static ArrayList<Job> getGoodResponse() {
		Gson gson = new Gson();
		Type listType = new TypeToken<ArrayList<Job>>() {
		}.getType();
		return gson.fromJson(response, listType);

	}

}
