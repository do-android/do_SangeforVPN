package doext.implement;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.sangfor.ssl.IVpnDelegate;
import com.sangfor.ssl.SFException;
import com.sangfor.ssl.SangforAuth;
import com.sangfor.ssl.common.VpnCommon;

import core.DoServiceContainer;
import core.helper.DoJsonHelper;
import core.interfaces.DoIScriptEngine;
import core.object.DoInvokeResult;
import core.object.DoSingletonModule;
import doext.define.do_SangforVPN_IMethod;

/**
 * 自定义扩展SM组件Model实现，继承DoSingletonModule抽象类，并实现do_SangforVPN_IMethod接口方法；
 * #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象； 获取DoInvokeResult对象方式new
 * DoInvokeResult(this.getUniqueKey());
 */
public class do_SangforVPN_Model extends DoSingletonModule implements do_SangforVPN_IMethod, IVpnDelegate {

	private SangforAuth sfAuth;
	private Activity ctx;

	private static int CODE = 0;
	private static String MSG = "";
	private String username;
	private String password;

	private DoIScriptEngine scriptEngine;
	private String callbackFuncName;

	public do_SangforVPN_Model() throws Exception {
		super();
		ctx = DoServiceContainer.getPageViewFactory().getAppContext();
		sfAuth = SangforAuth.getInstance();
		sfAuth.init(ctx, this, SangforAuth.AUTH_MODULE_EASYAPP);
		sfAuth.setLoginParam(AUTH_CONNECT_TIME_OUT, String.valueOf(25));
	}

	/**
	 * 同步方法，JS脚本调用该组件对象方法时会被调用，可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public boolean invokeSyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		// ...do something
		return super.invokeSyncMethod(_methodName, _dictParas, _scriptEngine, _invokeResult);
	}

	/**
	 * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用， 可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
	 * @_scriptEngine 当前page JS上下文环境
	 * @_callbackFuncName 回调函数名 #如何执行异步方法回调？可以通过如下方法：
	 *                    _scriptEngine.callback(_callbackFuncName,
	 *                    _invokeResult);
	 *                    参数解释：@_callbackFuncName回调函数名，@_invokeResult传递回调函数参数对象；
	 *                    获取DoInvokeResult对象方式new
	 *                    DoInvokeResult(this.getUniqueKey());
	 */
	@Override
	public boolean invokeAsyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
		this.scriptEngine = _scriptEngine;
		this.callbackFuncName = _callbackFuncName;
		if ("login".equals(_methodName)) {
			this.login(_dictParas, _scriptEngine, _callbackFuncName);
			return true;
		} else if ("logout".equals(_methodName)) {
			this.logout(_dictParas, _scriptEngine, _callbackFuncName);
			return true;
		}
		return super.invokeAsyncMethod(_methodName, _dictParas, _scriptEngine, _callbackFuncName);
	}

	/**
	 * 登录VPN
	 * 
	 * @throws JSONException
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void login(JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
		final String _host = DoJsonHelper.getString(_dictParas, "host", "");
		final int _port = DoJsonHelper.getInt(_dictParas, "port", 443);
		username = DoJsonHelper.getString(_dictParas, "username", "");
		password = DoJsonHelper.getString(_dictParas, "password", "");
		ctx.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					initSslVpn(_host, _port);
				} catch (Exception e) {
					DoServiceContainer.getLogEngine().writeError("SnagforVPN login \n\t", e);
				}
			}
		});
	}

	/**
	 * 开始初始化VPN，该初始化为异步接口，后续动作通过回调函数通知结果
	 * 
	 * @return 成功返回true，失败返回false，一般情况下返回true
	 * @throws UnknownHostException
	 */
	private boolean initSslVpn(String _hostStr, int _port) throws Exception {
		InetAddress m_iAddr = InetAddress.getByName(_hostStr);
		if (m_iAddr == null || m_iAddr.getHostAddress() == null) {
			CODE = -1;
			MSG = "vpn host error";
			callback();
			DoServiceContainer.getLogEngine().writeError("SnagforVPN login \n\t", new Exception("vpn host error"));
			return false;
		}
		long _host = VpnCommon.ipToLong(m_iAddr.getHostAddress());
		if (sfAuth.vpnInit(_host, _port) == false) {
			CODE = -1;
			MSG = "vpn init fail, errno is " + sfAuth.vpnGeterr();
			callback();
			DoServiceContainer.getLogEngine().writeError("SnagforVPN login \n\t", new Exception("vpn init fail, errno is " + sfAuth.vpnGeterr()));
			return false;
		}
		return true;
	}

	private void callback() {
		try {
			DoInvokeResult _invokeResult = new DoInvokeResult(this.getUniqueKey());
			JSONObject _node = new JSONObject();
			_node.put("result", CODE == 0);
			_node.put("code", CODE);
			_node.put("msg", MSG);
			_invokeResult.setResultNode(_node);
			scriptEngine.callback(callbackFuncName, _invokeResult);
		} catch (Exception e) {
			DoServiceContainer.getLogEngine().writeError("SangforVPN callback \n\t", e);
		}
	}

	/**
	 * 退出VPN
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void logout(JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) {
		ctx.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					sfAuth.vpnLogout();
				} catch (Exception e) {
					DoServiceContainer.getLogEngine().writeError("SnagforVPN login \n\t", e);
				}
			}
		});
	}

	@Override
	public void vpnCallback(int vpnResult, int authType) {

		switch (vpnResult) {
		case IVpnDelegate.RESULT_VPN_INIT_FAIL:
			/**
			 * 初始化vpn失败
			 */
			CODE = -1;
			MSG = "RESULT_VPN_INIT_FAIL, error is " + sfAuth.vpnGeterr();
			callback();
			break;

		case IVpnDelegate.RESULT_VPN_INIT_SUCCESS:
			/**
			 * 初始化vpn成功，接下来就需要开始认证工作了
			 */
			// 初始化成功，进行认证操作
			doVpnLogin(IVpnDelegate.AUTH_TYPE_PASSWORD);
			break;

		case IVpnDelegate.RESULT_VPN_AUTH_FAIL:
			/**
			 * 认证失败，有可能是传入参数有误，具体信息可通过sfAuth.vpnGeterr()获取
			 */
			CODE = -1;
			MSG = "RESULT_VPN_AUTH_FAIL, error is " + sfAuth.vpnGeterr();
			callback();
			break;

		case IVpnDelegate.RESULT_VPN_AUTH_SUCCESS:
			/**
			 * 认证成功，认证成功有两种情况，一种是认证通过，可以使用sslvpn功能了，另一种是前一个认证（如：用户名密码认证）通过，
			 * 但需要继续认证（如：需要继续证书认证）
			 */
			if (authType == IVpnDelegate.AUTH_TYPE_NONE) {
				// 若为L3vpn流程，认证成功后开启自动开启l3vpn服务
				if (sfAuth.getModuleUsed() == SangforAuth.AUTH_MODULE_EASYAPP) {
					// EasyApp流程，认证流程结束，可访问资源。
					CODE = 0;
					MSG = "";
				} else {
					CODE = -1;
					MSG = "vpn nonsupport";
					DoServiceContainer.getLogEngine().writeError("SnagforVPN login \n\t", new Exception("vpn nonsupport"));
				}
			} else {
				CODE = -1;
				MSG = "vpn nonsupport";
				DoServiceContainer.getLogEngine().writeError("SnagforVPN login \n\t", new Exception("vpn nonsupport"));
			}
			callback();
			break;
//		case IVpnDelegate.RESULT_VPN_AUTH_CANCEL:
//			Log.i(TAG, "RESULT_VPN_AUTH_CANCEL");
//			displayToast("RESULT_VPN_AUTH_CANCEL");
//			break;
		case IVpnDelegate.RESULT_VPN_AUTH_LOGOUT:
			/**
			 * 主动注销（自己主动调用logout接口）或者被动注销（通过控制台把用户踢掉）均会调用该接口
			 */
			CODE = 0;
			MSG = "";
			callback();
			break;
//		case IVpnDelegate.VPN_STATUS_ONLINE:
//			/**
//			 * 与设备连接建立
//			 */
//			Log.i(TAG, "online");
//			displayToast("online");
//			break;
//		case IVpnDelegate.VPN_STATUS_OFFLINE:
//			/**
//			 * 与设备连接断开
//			 */
//			Log.i(TAG, "offline");
//			displayToast("offline");
//			break;
//		default:
//			/**
//			 * 其它情况，不会发生，如果到该分支说明代码逻辑有误
//			 */
//			Log.i(TAG, "default result, vpn result is " + vpnResult);
//			displayToast("default result, vpn result is " + vpnResult);
//			break;
		}
	}

	/**
	 * 处理认证，通过传入认证类型（需要的话可以改变该接口传入一个hashmap的参数用户传入认证参数）.
	 * 也可以一次性把认证参数设入，这样就如果认证参数全满足的话就可以一次性认证通过，可见下面屏蔽代码
	 * 
	 * @param authType
	 *            认证类型
	 * @throws SFException
	 */
	private void doVpnLogin(int authType) {
		switch (authType) {
//		case IVpnDelegate.AUTH_TYPE_CERTIFICATE:
//			String certPasswd = edt_certPasswd.getText().toString();
//			String certName = edt_certName.getText().toString();
//			sfAuth.setLoginParam(IVpnDelegate.CERT_PASSWORD, certPasswd);
//			sfAuth.setLoginParam(IVpnDelegate.CERT_P12_FILE_NAME, certName);
//			ret = sfAuth.vpnLogin(IVpnDelegate.AUTH_TYPE_CERTIFICATE);
//			break;
		case IVpnDelegate.AUTH_TYPE_PASSWORD:
			sfAuth.setLoginParam(IVpnDelegate.PASSWORD_AUTH_USERNAME, username);
			sfAuth.setLoginParam(IVpnDelegate.PASSWORD_AUTH_PASSWORD, password);
			sfAuth.vpnLogin(IVpnDelegate.AUTH_TYPE_PASSWORD);
			break;
//		case IVpnDelegate.AUTH_TYPE_SMS:
//			String smsCode = edt_sms.getText().toString();
//			sfAuth.setLoginParam(IVpnDelegate.SMS_AUTH_CODE, smsCode);
//			ret = sfAuth.vpnLogin(IVpnDelegate.AUTH_TYPE_SMS);
//			break;
//		case IVpnDelegate.AUTH_TYPE_SMS1:
//			ret = sfAuth.vpnLogin(IVpnDelegate.AUTH_TYPE_SMS1);
//			break;
//		default:
//			Log.w(TAG, "default authType " + authType);
//			break;
		}
	}

	@Override
	public void reloginCallback(int arg0, int arg1) {
	}

	@Override
	public void vpnRndCodeCallback(byte[] arg0) {
	}
}