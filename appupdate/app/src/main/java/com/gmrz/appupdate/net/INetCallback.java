package com.gmrz.appupdate.net;

public interface INetCallback {
    void success(String response);
    void failure(Throwable throwable);
}
