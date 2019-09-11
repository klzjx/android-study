package com.gmrz.appupdate.dialog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmrz.appupdate.R;
import com.gmrz.appupdate.bean.DownloadBean;
import com.gmrz.appupdate.net.INetCallDownLoad;
import com.gmrz.appupdate.updateutil.AppUpdate;
import com.gmrz.appupdate.updateutil.AppUtils;

import java.io.File;

public class UpdateVersionShowDialog extends DialogFragment {

    private static final String TAG = "UpdateVersionShowDialog";
    private DownloadBean downloadBean ;
    private TextView title ,content,update;

    public static void show(FragmentActivity fragmentActivity, DownloadBean downloadBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("download_bean", downloadBean);
        UpdateVersionShowDialog dialog = new UpdateVersionShowDialog();
        dialog.setArguments(bundle);
        dialog.show(fragmentActivity.getSupportFragmentManager(), TAG);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        downloadBean = (DownloadBean) bundle.getSerializable("download_bean");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fregementdialog, container, false);
        initView(view);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initEvent();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        AppUpdate.getInstance().getManager().cancel(UpdateVersionShowDialog.this);
    }

    private void initView(View view )
    {
        title = view.findViewById(R.id.title);
        content = view.findViewById(R.id.content);
        update = view.findViewById(R.id.update);
        title.setText(downloadBean.title);
        content.setText(downloadBean.content);
    }

    private void initEvent() {
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File targetFile = new File(getActivity().getCacheDir(), "update.apk");
                AppUpdate.getInstance().getManager().download(downloadBean.url, targetFile, new INetCallDownLoad() {
                    @Override
                    public void success(File apkFile) {
                        Log.e(TAG,"apkFile ::"+apkFile.getPath());
                        dismiss();
                        AppUtils.installApk(getActivity(),apkFile.getPath());
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        throwable.printStackTrace();
                    }

                    @Override
                    public void progress(final String progress) {
                        Log.e(TAG,"progress ::"+progress);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                update.setText(progress);
                            }
                        });

                    }
                }, UpdateVersionShowDialog.this);
            }
        });
    }


}
