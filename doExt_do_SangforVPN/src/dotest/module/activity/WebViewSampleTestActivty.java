package dotest.module.activity;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.doext.module.activity.R;

import core.DoServiceContainer;
import core.object.DoInvokeResult;
import core.object.DoUIModule;
import dotest.module.frame.debug.DoService;
import extimplement.Do_WebView_Model;
import extimplement.Do_WebView_View;
/**
 * webview组件测试样例
 */
public class WebViewSampleTestActivty extends DoTestActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void initModuleModel() throws Exception {
		this.model = new Do_WebView_Model();
	}
	
	@Override
	protected void initUIView() throws Exception {
		Do_WebView_View view = new Do_WebView_View(this);
        ((DoUIModule)this.model).setCurrentUIModuleView(view);
        ((DoUIModule)this.model).setCurrentPage(currentPage);
        view.loadView((DoUIModule)this.model);
        LinearLayout uiview = (LinearLayout)findViewById(R.id.uiview);
        uiview.addView(view);
	}

	@Override
	public void doTestProperties(View view) {
		 DoService.setPropertyValue(this.model, "url", "https://www.baidu.com");
	}

	@Override
	protected void doTestSyncMethod() {
		Map<String, String> _paras_back = new HashMap<String, String>();
        DoService.syncMethod(this.model, "back", _paras_back);
	}

	@Override
	protected void doTestAsyncMethod() {
		Map<String, String>  _paras_loadString = new HashMap<String, String>();
        _paras_loadString.put("text", "<b>百度</b>");
        DoService.asyncMethod(this.model, "loadString", _paras_loadString, new DoService.EventCallBack() {
			@Override
			public void eventCallBack(String _data) {//回调函数
				DoServiceContainer.getLogEngine().writeDebug("异步方法回调：" + _data);
			}
		});
	}

	@Override
	protected void onEvent() {
		DoService.subscribeEvent(this.model, "loaded", new DoService.EventCallBack() {
			@Override
			public void eventCallBack(String _data) {
				DoServiceContainer.getLogEngine().writeDebug("事件回调：" + _data);
			}
		});
	}

	@Override
	public void doTestFireEvent(View view) {
		DoInvokeResult invokeResult = new DoInvokeResult(this.model.getUniqueKey());
		this.model.getEventCenter().fireEvent("_messageName", invokeResult);
	}

}
