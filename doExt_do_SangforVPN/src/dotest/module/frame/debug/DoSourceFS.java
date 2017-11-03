package dotest.module.frame.debug;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Environment;
import core.helper.DoIOHelper;
import core.interfaces.DoIApp;
import core.interfaces.DoISourceFS;
import core.object.DoSourceFile;

public class DoSourceFS implements DoISourceFS {

	public DoSourceFS(Context context,DoIApp _app) {
		this.currentApp = _app;
		this.onInit(context);
	}

	private void onInit(Context context) {
		this.rootPath = getSourceRootPath(context) + "/apps/" + this.currentApp.getAppID() + "/default";
		this.dictSourceFiles = new HashMap<String, DoSourceFile>();
	}

	public static String getSourceRootPath(Context _ctx) {
		return _ctx.getFilesDir().getAbsolutePath();
	}

	public static boolean existSDCard() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return true;
		} else
			return false;
	}


	private DoIApp currentApp;
	private String rootPath;
	private Map<String, DoSourceFile> dictSourceFiles;

	public void dispose() {
		this.currentApp = null;
		this.rootPath = null;
		if (this.dictSourceFiles != null) {
			for (String _key : this.dictSourceFiles.keySet()) {
				this.dictSourceFiles.get(_key).dispose();
			}
			this.dictSourceFiles.clear();
			this.dictSourceFiles = null;
		}
	}

	@Override
	public DoIApp getCurrentApp() {
		return this.currentApp;
	}

	@Override
	public String getRootPath() {
		return this.rootPath;
	}

	public DoSourceFile getSourceByFileName(String _fileName) throws Exception {
		String _fileFullName = this.getFileFullPathByName(_fileName);
		return this.getSourceByFileFullName(_fileFullName);
	}

	public DoSourceFile getSourceByFileFullName(String _fileFullName) {
		if (!dictSourceFiles.containsKey(_fileFullName)) {
			if (DoIOHelper.existFile(_fileFullName)) {
				DoSourceFile _newAppFile = new DoSourceFile(this, _fileFullName);
				this.dictSourceFiles.put(_fileFullName, _newAppFile);
			}
		}
		return this.dictSourceFiles.get(_fileFullName);
	}

	// 获取完整的路径
	public String getFileFullPathByName(String _fileName) throws Exception {
		if (!_fileName.startsWith("source://")) {
			return null;
		}
		if (_fileName.indexOf("..") >= 0 || _fileName.indexOf("~") >= 0)
			throw new Exception("..~等符号在路径中被禁止!");
		String _related_url = _fileName.substring(9);
		return this.rootPath + "/" + _related_url;
	}

}
