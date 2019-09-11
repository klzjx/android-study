package com.gmrz.appupdate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gmrz.appupdate.bean.DownloadBean;
import com.gmrz.appupdate.dialog.UpdateVersionShowDialog;
import com.gmrz.appupdate.net.INetCallback;
import com.gmrz.appupdate.net.INetManager;
import com.gmrz.appupdate.updateutil.AppUpdate;
import com.gmrz.appupdate.updateutil.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Button btn_get;
    Button btn_download;
    AppUpdate appUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appUpdate = AppUpdate.getInstance();
        final INetManager okhttpManager = appUpdate.getManager();

        btn_get = findViewById(R.id.btn_get);
        btn_download = findViewById(R.id.btn_download);

        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okhttpManager.get(Constants.APP_UPDATE_URL, new INetCallback() {
                    @Override
                    public void success(String response) {
                        Log.e(TAG, "res-----" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            DownloadBean downLoadBean = DownloadBean.parse(jsonObject);

                            if (downLoadBean == null) {
                                Toast.makeText(MainActivity.this, "接口返回数据异常", Toast.LENGTH_LONG).show();
                                return;
                            }


                            long versionCode = Long.parseLong(downLoadBean.versionCode);
                            if (versionCode > AppUtils.getVersionCode(MainActivity.this)) {
                                UpdateVersionShowDialog.show(MainActivity.this, downLoadBean);
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(Throwable throwable) {
                        throwable.printStackTrace();
                    }

                });
            }
        });
    }
}
