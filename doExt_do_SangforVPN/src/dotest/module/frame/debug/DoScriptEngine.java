package dotest.module.frame.debug;

import java.util.HashMap;
import java.util.Map;

import core.interfaces.DoIApp;
import core.interfaces.DoIPage;
import core.interfaces.DoIScriptEngine;
import core.object.DoCallBackTask;
import core.object.DoInvokeResult;
import dotest.module.frame.debug.DoService.EventCallBack;

public class DoScriptEngine implements DoIScriptEngine {
	
	private Map<String,  EventCallBack> dictCallBack;
	private DoIApp currentApp;
	private DoIPage currentPage;
	
	public DoScriptEngine(){
		dictCallBack = new HashMap<String, DoService.EventCallBack>();
	}
	 
	@Override
	public void callLoadScriptsAsModel(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void callLoadScriptsAsModelWithPreDefine(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void callback(String _methodName, DoInvokeResult _invokeResult) {
		if (!this.dictCallBack.containsKey(_methodName)) {
			return;
		}
        try {
			this.dictCallBack.get(_methodName).eventCallBack(_invokeResult.getResult());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public DoCallBackTask createCallBackTask(String _callbackMethodName) {
		DoCallBackTask _callbackTask = new DoCallBackTask(this, _callbackMethodName);
        return _callbackTask;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public DoIApp getCurrentApp() {
		// TODO Auto-generated method stub
		return currentApp;
	}

	@Override
	public DoIPage getCurrentPage() {
		// TODO Auto-generated method stub
		return currentPage;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadScripts(String arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCurrentApp(DoIApp arg0) {
		// TODO Auto-generated method stub
		this.currentApp = arg0;
	}

	@Override
	public void setCurrentPage(DoIPage arg0) {
		this.currentPage = arg0;
	}
	
	public DoInvokeResult CreateInvokeResult(String _uniqueKey){
        DoInvokeResult _invokeResult = new DoInvokeResult(_uniqueKey);
        return _invokeResult;
    }

    public void AddCallBack(String _eventID, EventCallBack _eventCallBack){
        this.dictCallBack.put(_eventID, _eventCallBack);
    }

}
