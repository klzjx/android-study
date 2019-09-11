package com.gmrz.appupdate.net;

import java.io.File;

public interface INetCallDownLoad {
    void success(File apkFile);

    void failed(Throwable throwable);

    void progress(String progress);
}
