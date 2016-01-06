package com.pietrantuono.uploadfirmware;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.pietrantuono.pericoach.newtestapp.R;

public class ProgressAndTextView {
	
	private ProgressBar progress;
	private TextView text;
	private TextView descriptionTextView;

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

	public void setDescriptionTextView(TextView descriptionTextView) {
		this.descriptionTextView = descriptionTextView;
	}

	public TextView getDescriptionTextView() {
		return descriptionTextView;
	}
	public void setDescriptionTextViewText(String string){
		descriptionTextView.setText(string);
	}
}
