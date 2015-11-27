package com.pietrantuono.uploadfirmware;

import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressAndTextView {
	
	private ProgressBar progress;
	private TextView text;
	
	public ProgressAndTextView(ProgressBar progress,TextView text){
		this.progress=progress;
		this.text=text;
	}
	
	public ProgressBar getProgress(){
		return progress;
	}
	
	public TextView getTextView(){
		return text;
	}
	
	public void setProgress(ProgressBar progress){
		this.progress=progress;
	}
	
	public void setTextView(TextView text){
		this.text=text;
	}
}
