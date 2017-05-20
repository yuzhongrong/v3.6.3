package com.jinr.core.updata;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.utils.HttpClientHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

@SuppressLint({"NewApi", "ValidFragment"})
public class UpdataDownloadDialog extends DialogFragment implements OnKeyListener {
    private MyProgressBar mProgressBar;
    private TextView tv_progress;
    private TextView tv_max;
    private String mUrl;
    private TextView tv_percent;
    private Context mContext;

    public UpdataDownloadDialog(Context context) {
        mContext = context;
    }

    public static UpdataDownloadDialog getInstance(String url, Context context) {
        UpdataDownloadDialog instance = new UpdataDownloadDialog(context);
        Bundle args = new Bundle();
        args.putString("mUrl", url);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_updata_downlode, null);
        initUI(view);
        Bundle args = getArguments();
        if (args != null) {
            mUrl = args.getString("mUrl");
        }
        Dialog dialog = new Dialog(getActivity(), R.style.Dialog_Fullscreen);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(this);
        dialog.setContentView(view);
        initData();
        return dialog;
    }

    private void initUI(View view) {
        mProgressBar = (MyProgressBar) view.findViewById(R.id.mProgressBar);
        tv_progress = (TextView) view.findViewById(R.id.tv_progress);
        tv_max = (TextView) view.findViewById(R.id.tv_max);
        tv_percent = (TextView) view.findViewById(R.id.tv_percent);
    }

    public void setProgress(float percent) {
        mProgressBar.setPersent(percent);
    }

    // 文件下载 Ysw 2016.09.28
    public void initData() {
        final Handler handler = new Handler();
        final DecimalFormat df = new DecimalFormat(".0");
        new Thread() {
            public void run() {
                HttpClient client = HttpClientHelper.getNewHttpClient();
                if (mUrl == null)
                    return;
                HttpGet get = new HttpGet(mUrl);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    // 获取文件大小
                    final float length = (float) entity.getContentLength();
                    final float max = length / 1000 / 1024f;
                    handler.post(new Runnable() {
                        public void run() {
                            tv_max.setText("/" + df.format(max) + "MB");
                        }
                    });
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file = new File(Environment.getExternalStorageDirectory(), "Jinr.apk");
                        fileOutputStream = new FileOutputStream(file);
                        // 这个是缓冲区，即一次读取1024个比特
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            process += ch;
                            final float ps = process;
                            mProgressBar.setPersent(process / length * 100);
                            handler.post(new Runnable() {
                                public void run() {
                                    int percent = (int) (ps / length * 100);
                                    float a = ps / 1000 / 1024;
                                    String text = "";
                                    if (a < 0.9) {
                                        text = "0" + df.format(a);
                                    } else {
                                        text = df.format(a);
                                    }
                                    tv_percent.setText(percent + "%");
                                    tv_progress.setText(text);
                                }
                            });
                        }
                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    UpdataDownloadDialog.this.dismiss();
                    handler.post(new Runnable() {
                        public void run() {
                            update();
                        }
                    });
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // 下载结束之后直接跳转到安装页面 Ysw 2016.09.28
    public void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "Jinr.apk")),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        } else {
            return false;
        }
    }
}
