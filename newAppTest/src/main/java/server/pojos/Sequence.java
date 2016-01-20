
package server.pojos;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class Sequence {

    @Expose
    private boolean error;
    @Expose
    private List<Test> tests = new ArrayList<Test>();
    
    private boolean log;

    public boolean isLog() {
		return log;
	}

	public void setLog(boolean log) {
		this.log = log;
	}

	/**
     * 
     * @return
     *     The error
     */
    public boolean isError() {
        return error;
    }

    /**
     * 
     * @param error
     *     The error
     */
    public void setError(boolean error) {
        this.error = error;
    }

    /**
     * 
     * @return
     *     The tests
     */
    public List<Test> getTests() {
        return tests;
    }

    /**
     * 
     * @param tests
     *     The tests
     */
    public void setTests(List<Test> tests) {
        this.tests = tests;
    }


}
