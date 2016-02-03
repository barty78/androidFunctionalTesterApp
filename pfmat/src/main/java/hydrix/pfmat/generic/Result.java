package hydrix.pfmat.generic;

public class Result {
	
	//Members
	private int mResult;
	
	public Result() {
		this.mResult = TEST.CLEAR;
	}

	public Result(int result) {
		this.mResult = result;
	}
	
	public Result(String result) {
		
		if (result != null){
			if (result == "PASS") {
				this.mResult = TEST.PASS;
			}
			if (result == "FAIL") {
				this.mResult = TEST.FAIL;
			}
		}
	}
	
	public void pass(){
		this.mResult = TEST.PASS;
	}
	
	public Boolean isPass(){
		if (mResult == TEST.PASS) {
			return true;
		}
		return false;
	}

	public void fail() {
		this.mResult = TEST.FAIL;
	}

	public Boolean isFail(){
		if (mResult == TEST.FAIL) {
			return true;
		}
		return false;
	}

	
	public void clear() {
		this.mResult = TEST.CLEAR;
	}
	
	public int getResult() {
		return mResult;
	}
	
	@Override
	public String toString(){
		String s = new String();
		
		switch(mResult){
		case TEST.CLEAR:
			s = "CLEAR";
			break;
		case TEST.PASS:
			s = "PASS";
			break;
		case TEST.FAIL:
			s = "FAIL";
			break;
		}
		
		return s;
	}
}
