package dotest.module.frame.debug;

import android.content.Context;
import core.interfaces.DoIApp;
import core.interfaces.DoIConfig;
import core.interfaces.DoIDataFS;
import core.interfaces.DoIScriptEngine;
import core.interfaces.DoISourceFS;
import core.object.DoMultitonModule;

public class DoApp implements DoIApp{
	
	private DoIDataFS dataFS;
	private DoISourceFS sourceFS;
	
	
	public DoApp(Context context){
		this.dataFS = new DoDataFS(context,this);
		this.sourceFS = new DoSourceFS(context,this);
	}

	@Override
	public DoMultitonModule createMultitonModule(String arg0, String arg1)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteMultitonModule(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAppID() {
		// TODO Auto-generated method stub
		return "dotest";
	}

	@Override
	public DoIConfig getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DoIConfig getConfig(String arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DoIDataFS getDataFS() {
		// TODO Auto-generated method stub
		return dataFS;
	}

	@Override
	public DoMultitonModule getMultitonModuleByAddress(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DoIScriptEngine getScriptEngine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DoISourceFS getSourceFS() {
		// TODO Auto-generated method stub
		return sourceFS;
	}

}
