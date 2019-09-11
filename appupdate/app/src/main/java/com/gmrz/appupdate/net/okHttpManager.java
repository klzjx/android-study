package com.gmrz.appupdate.net;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class okHttpManager implements INetManager {
    private static final String TAG = "okHttpManager";

    private static OkHttpClient okHttpClient;
    private static Handler mhandler;

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(100, TimeUnit.SECONDS);
        okHttpClient = builder.build();
        mhandler = new Handler(Looper.getMainLooper());
    }


    @Override
    public void get(String url, final INetCallback iNetCallback) {
        final Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (iNetCallback != null) {
                            iNetCallback.failure(e);
                        }
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String rsp = response.body().string();
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (iNetCallback != null) {
                            iNetCallback.success(rsp);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void download(String url, final File targetFile, final INetCallDownLoad callDownLoad, Object tag) {
        if(!targetFile.exists())
            targetFile.getParentFile().mkdirs();

        Request request = new Request.Builder().url(url).tag(tag).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callDownLoad != null) {
                            callDownLoad.failed(e);
                        }
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    inputStream = response.body().byteStream();
                    outputStream = new FileOutputStream(targetFile);

                    final long currentlen = response.body().contentLength();
                    int len = 0;
                    int sum = 0;
                    byte[] bytes = new byte[8 * 1024];

                    while ((len = inputStream.read(bytes)) > 0)
                    {
                        sum += len;
                        outputStream.write(bytes, 0, len);
                        outputStream.flush();
                        final int finalSum = sum;
                        mhandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callDownLoad.progress((int)((finalSum * 1.0f / currentlen * 100)) + "%");
                            }
                        });
                    }

                    targetFile.setExecutable(true, false);
                    targetFile.setReadable(true, false);
                    targetFile.setWritable(true, false);

                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callDownLoad.success(targetFile);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    callDownLoad.failed(e);
                }finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            }
        });
    }

    @Override
    public void cancel(Object tag) {
        List<Call> queuedCalls = okHttpClient.dispatcher().queuedCalls();
        for (Call call : queuedCalls) {
            if (tag.equals(call.request().tag())) {
                Log.e(TAG, "queuedCalls tag :" + tag);
                call.cancel();
            }
        }

        List<Call> runningCalls = okHttpClient.dispatcher().runningCalls();
        for (Call call : runningCalls) {
            if (tag.equals(call.request().tag())) {
                Log.e(TAG, "runningCalls tag" + tag);
                call.cancel();
            }
        }
    }
}
