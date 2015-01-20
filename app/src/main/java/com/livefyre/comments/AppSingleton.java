package com.livefyre.comments;

public class AppSingleton{
	private static AppSingleton appSingleton = new AppSingleton();

	private LFCApplication application;

	private AppSingleton() {
	}

	public static AppSingleton getInstance() {
		if (appSingleton == null)
			appSingleton = new AppSingleton();
		return appSingleton;
	}

	public LFCApplication getApplication() {
		return application;
	}

	public void setApplication(LFCApplication application) {
		this.application = application;
	}

}
