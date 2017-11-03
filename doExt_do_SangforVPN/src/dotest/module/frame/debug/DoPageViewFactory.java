package dotest.module.frame.debug;

import android.app.Activity;
import core.interfaces.DoIPageViewFactory;

public class DoPageViewFactory implements DoIPageViewFactory {
	
	private Activity currentActivity;
	
	@Override
	public void closePage(String arg0, String arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Activity getAppContext() {
		// TODO Auto-generated method stub
		return currentActivity;
	}

	
	
	public void setCurrentActivity(Activity currentActivity) {
		this.currentActivity = currentActivity;
	}

	@Override
	public void closePage(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openPage(String arg0, String arg1, String arg2, String arg3,
			String arg4, String arg5, String arg6, String arg7, String arg8) {
		// TODO Auto-generated method stub
		
	}

}
