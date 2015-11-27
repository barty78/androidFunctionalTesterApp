package server.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorFromServer {

@SerializedName("error")
@Expose
private boolean error;
@SerializedName("message")
@Expose
private String message;

/**
* 
* @return
* The error
*/
public boolean isError() {
return error;
}

/**
* 
* @param error
* The error
*/
public void setError(boolean error) {
this.error = error;
}

/**
* 
* @return
* The message
*/
public String getMessage() {
return message;
}

/**
* 
* @param message
* The message
*/
public void setMessage(String message) {
this.message = message;
}

}

