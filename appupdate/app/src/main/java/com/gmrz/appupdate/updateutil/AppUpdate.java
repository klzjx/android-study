package com.gmrz.appupdate.updateutil;

import com.gmrz.appupdate.net.INetManager;
import com.gmrz.appupdate.net.okHttpManager;

public class AppUpdate {
    static AppUpdate appUpdate = null;
    private INetManager manager = new okHttpManager();

    public void setManager(INetManager manager) {
        this.manager = manager;
    }

    public INetManager getManager() {
        return manager;
    }

    private AppUpdate() {
    }

    public static AppUpdate getInstance() {
        if (appUpdate == null) {
            synchronized (AppUpdate.class) {
                if (appUpdate == null)
                    return new AppUpdate();
            }
        }
        return appUpdate;
    }

}
