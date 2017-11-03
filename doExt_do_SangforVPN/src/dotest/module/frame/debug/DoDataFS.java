package dotest.module.frame.debug;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import core.interfaces.DoIApp;
import core.interfaces.DoIDataFS;

public class DoDataFS implements DoIDataFS {

	public DoDataFS(Context context, DoIApp _doApp) {
		this.currentApp = _doApp;
		this.onInit(context);
	}

	private void onInit(Context context) {
		this.rootPath = getDataRootPath(context) + "/" + this.currentApp.getAppID();
		this.pathSys = this.rootPath + "/sys";
		this.pathSysCache = this.pathSys + "/cache";
		this.pathPublic = this.rootPath + "/public";
		this.pathSecurity = this.rootPath + "/security";
	}

	public void dispose() {
		this.rootPath = null;
		this.pathSys = null;
		this.pathSysCache = null;
		this.pathPublic = null;
		this.pathSecurity = null;
	}

	private DoIApp currentApp;
	private String rootPath;

	// 应用数据目录-系统
	private String pathSys;
	
	// 应用数据目录-系统-缓存
	private String pathSysCache;

	// 应用数据目录-公共
	private String pathPublic;

	// 应用数据目录-安全
	private String pathSecurity;

	@Override
	public DoIApp getCurrentApp() {
		return this.currentApp;
	}

	@Override
	public String getRootPath() {
		return this.rootPath;
	}

	@Override
	public String getPathSys() {
		return this.pathSys;
	}

	@Override
	public String getPathPublic() {
		return this.pathPublic;
	}

	@Override
	public String getPathSysCache() {
		return this.pathSysCache;
	}

	@Override
	public String getPathSecurity() {
		return this.pathSecurity;
	}

	// 获取完整的路径
	@Override
	public String getFileFullPathByName(String _fileName) throws Exception {
		if (!_fileName.startsWith("data://")) {
			return null;
		}
		if (_fileName.indexOf("..") >= 0 || _fileName.indexOf("~") >= 0)
			throw new Exception("..~等符号在路径中被禁止!");
		String _related_url = _fileName.substring(7);
		return this.rootPath + "/" + _related_url;
	}
	
	public static String getDataRootPath(Context _ctx) {
		if (existSDCard() && getSDFreeSize() > 5) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return _ctx.getFilesDir().getAbsolutePath();
	}

	public static boolean existSDCard() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return true;
		} else
			return false;
	}

	public static long getSDFreeSize() {
		// 取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize();
		// 空闲的数据块的数量
		long freeBlocks = sf.getAvailableBlocks();
		// 返回SD卡空闲大小
		return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
	}
}
