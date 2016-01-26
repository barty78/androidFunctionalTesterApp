package server.pojos.records.response;

import com.google.gson.annotations.Expose;

public class Response {

    @Expose
    private boolean error;
    @Expose
    private String message;

    /**
     * 
     * @return
     *     The error
     */
    @SuppressWarnings("unused")
    public boolean isError() {
        return error;
    }

    /**
     * 
     * @param error
     *     The error
     */
    @SuppressWarnings("unused")
    public void setError(boolean error) {
        this.error = error;
    }

    /**
     * 
     * @return
     *     The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * 
     * @param message
     *     The message
     */
    @SuppressWarnings("unused")
    public void setMessage(String message) {
        this.message = message;
    }

}
