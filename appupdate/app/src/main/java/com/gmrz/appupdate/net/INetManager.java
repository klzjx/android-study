package com.gmrz.appupdate.net;

import java.io.File;

public interface INetManager {
    void get(String url, INetCallback iNetCallback);

    void download(String url, File targetFile, INetCallDownLoad callDownLoad, Object tag);

    void cancel(Object tag);
}
