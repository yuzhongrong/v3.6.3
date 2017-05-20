package com.jinr.core.more;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.UrlConfig;
import com.jinr.core.ui.CustomDialog;
import com.jinr.core.utils.CommonUtil;
import com.jinr.core.utils.MyDES;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.MyhttpClient;
import com.jinr.core.utils.PopupWindowUtil;
import com.jinr.core.utils.PreferencesUtils;
import com.jinr.core.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.BaseModel;

public class FeedbackActivity extends BaseActivity implements OnClickListener {

    private ImageView img_feedback;
    private TextView tv_kefu;
    private WindowManager wm;
    private Bitmap bitmap;
    private Bitmap blankBitmap;
    private String user_id;
    private EditText edit_detail;
    private InputStream is;
    private File file;
    private PopupWindow popupwindow;
    private Uri photoUri;
    private CustomDialog dialog;
    private Button btn_submit;
    private static final int MSG_SUCCESS = 0;//


    private Handler mHandler = new Handler() {
        // 重写handleMessage()方法，此方法在UI线程运行
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 如果成功，则显示从网络获取到的图片
                case MSG_SUCCESS:
                    dismissWaittingDialog();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initData();
        findViewById();
        initUI();
        setListener();
    }

    @Override
    protected void initData() {
        wm = getWindowManager();
        user_id = PreferencesUtils.getValueFromSPMap(this,
                PreferencesUtils.Keys.UID);
    }

    @Override
    protected void findViewById() {
        final TextView tv_result = (TextView) findViewById(R.id.tv_result);
        findViewById(R.id.title_left_img).setOnClickListener(this);
        img_feedback = (ImageView) findViewById(R.id.img_feedback);
        img_feedback.setOnClickListener(this);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        edit_detail = (EditText) findViewById(R.id.edit_detail);
        TextView title = (TextView) findViewById(R.id.title_center_text);
        findViewById(R.id.tv_myfeedback).setOnClickListener(this);
        tv_kefu = (TextView) findViewById(R.id.tv_kefu);
        tv_kefu.setOnClickListener(this);
        if (!PreferencesUtils.getCValueFromSPMap(FeedbackActivity.this,
                PreferencesUtils.Keys.KEFU_PHONE).equals("")) {
            tv_kefu.setText(PreferencesUtils.getCValueFromSPMap(FeedbackActivity.this,
                    PreferencesUtils.Keys.KEFU_PHONE));
        }
        title.setText("意见反馈");
        findViewById(R.id.tv_wxservice).setOnClickListener(this);
        edit_detail.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = edit_detail.getText().toString();
                int result = 300 - content.length();
                tv_result.setText("还可输入" + result + "个字");
                if (content.length() > 0) {
                    btn_submit.setClickable(true);
                    btn_submit.setBackgroundResource(R.drawable.btn_bg_select);
                } else {
                    btn_submit.setClickable(false);
                    btn_submit.setBackgroundResource(R.drawable.btn_blue_light_bg);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    protected void initUI() {
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
                break;
            case R.id.img_feedback:// 选择图片
                showfeedbackWindow(v);
                break;
            case R.id.btn_submit:
                final String content = edit_detail.getText().toString().trim();
                if (content.equals("")) {
                    ToastUtil.show(FeedbackActivity.this, "请填写反馈内容");
                } else {
                    try {
                        send(user_id, content, file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.tv_myfeedback:
                startActivity(new Intent(FeedbackActivity.this, MyFeedbackActivity.class));
                break;
            case R.id.ly_all_share:
                popupwindow.dismiss();
                break;

            case R.id.tv_share_2:// 打开相机
                Intent intel = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                String filename = timeStampFormat.format(new Date());
                ContentValues values = new ContentValues();
                values.put(Media.TITLE, filename);
                photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intel.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intel, 10);
                popupwindow.dismiss();
                break;
            case R.id.tv_share_1:// 打开相册
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_PICK);
                gallery.setType("image/*");
                // 以需要返回值的模式开启一个Activity
                startActivityForResult(gallery, 0);
                popupwindow.dismiss();
                break;
            case R.id.tv_kefu:// 拨打客服电话
                dialog = new CustomDialog(FeedbackActivity.this, getString(R.string.warning), getString(R.string.dialog_call),
                        PreferencesUtils.getCValueFromSPMap(FeedbackActivity.this, PreferencesUtils.Keys.KEFU_PHONE));
                dialog.dialog_sure.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                                + PreferencesUtils.getCValueFromSPMap(FeedbackActivity.this, PreferencesUtils.Keys.KEFU_PHONE)));
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.tv_wxservice:// 微信客服
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                if (intent != null) {// 这里如果intent为空，就说明没有安装要跳转的应用嘛
                    startActivity(intent);
                } else {// 没有安装要跳转的app应用，提醒一下
                    ToastUtil.show(getApplicationContext(), "哟，赶紧下载安装微信客户端！");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 如果获取成功，resultCode为-1
        MyLog.i(TAG, "resultCode:" + resultCode);
        if (requestCode == 0 && resultCode == -1) {
            Uri uri = data.getData();
            DspPicture(data, uri);
        }
        if (requestCode == 10) {
            Uri uri = photoUri;
            DspPicture(data, uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void DspPicture(Intent data, Uri uri) {
        try {
            is = getContentResolver().openInputStream(uri);
            // 得到屏幕的宽和高
            int windowWidth = wm.getDefaultDisplay().getWidth();
            int windowHeight = wm.getDefaultDisplay().getHeight();
            // 实例化一个Options对象
            BitmapFactory.Options opts = new BitmapFactory.Options();
            // 指定它只读取图片的信息而不加载整个图片
            opts.inJustDecodeBounds = true;
            // 通过这个Options对象，从输入流中读取图片的信息
            BitmapFactory.decodeStream(is, null, opts);
            // 得到Uri地址的图片的宽和高
            int bitmapWidth = opts.outWidth;
            int bitmapHeight = opts.outHeight;
            // 分析图片的宽高比，用于进行优化
            if (bitmapHeight > windowHeight || bitmapWidth > windowWidth) {
                int scaleX = bitmapWidth / windowWidth;
                int scaleY = bitmapHeight / windowHeight;
                if (scaleX > scaleY) {
                    opts.inSampleSize = scaleX;
                } else {
                    opts.inSampleSize = scaleY;
                }
            } else {
                opts.inSampleSize = 1;
            }
            // 设定读取完整的图片信息
            opts.inJustDecodeBounds = false;
            is = getContentResolver().openInputStream(uri);
            // 如果没有被系统回收，就强制回收它
            if (blankBitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = BitmapFactory.decodeStream(is, null, opts);
            // 如果没有被系统回收，就强制回收它
            if (blankBitmap != null && !blankBitmap.isRecycled()) {
                blankBitmap.recycle();
            }
            // 在内存中创建一个可以操作的Bitmap对象
            blankBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            // 为图片添加一个画板
            Canvas canvas = new Canvas(blankBitmap);
            // 把读取的图片画到新创建的Bitmap对象中
            canvas.drawBitmap(bitmap, new Matrix(), new Paint());
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setTextSize(30);
            // 通过创建的画笔，在Bitmap上写入水印
            // canvas.drawText("我是水印", 10, 50, paint);
            img_feedback.setImageBitmap(blankBitmap);
            String[] proj = {MediaStore.Images.Media.DATA};// 获取图片路径
            @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);// 获取图片地址
            showWaittingDialog(FeedbackActivity.this);
            Compresspic(path);
        } catch (Exception e) {
            ToastUtil.show(FeedbackActivity.this, "获取图片失败");
        }
    }

    public void Compresspic(final String path) {
        new Thread(new Runnable() {// 开启多线程进行压缩处理
            private int options;

            @Override
            public void run() {
                Looper.prepare();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                options = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 质量压缩方法，把压缩后的数据存放到baos中(100表示不压缩，0表示压缩到最小)
                while (baos.toByteArray().length / 1024 > 60) {// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                    baos.reset();// 重置baos即让下一次的写入覆盖之前的内容
                    options -= 10;// 图片质量每次减少10
                    if (options < 0)
                        options = 0;// 如果图片质量小于10，则将图片的质量压缩到最小值
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 将压缩后的图片保存到baos中
                    if (options == 0)
                        break;// 如果图片的质量已降到最低则，不再进行压缩
                }
                try {
                    FileOutputStream fos = new FileOutputStream(new File(path));// 将压缩后的图片保存的本地上指定路径中
                    fos.write(baos.toByteArray());
                    fos.flush();
                    fos.close();
                    file = new File(path);
                    if (!file.exists()) {
                        mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
                        ToastUtil.show(FeedbackActivity.this, "未找到上传文件");
                        Looper.loop();
                        return;
                    }
                } catch (Exception e) {
                    mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
                    e.printStackTrace();
                    Looper.loop();
                }
                mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
                Looper.loop();
            }
        }).start();
    }

    protected void send(final String user_id, final String content, File picture) throws Exception {
        showWaittingDialog(FeedbackActivity.this);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uid", user_id);
        jsonObject.put("problem", content);
        RequestParams params = new RequestParams();
        params.put("cipher", MyDES.encrypt(jsonObject.toString()));
        params.put("image", picture);
        MyhttpClient.desPost(UrlConfig.FEEDBACK, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                dismissWaittingDialog();
                ToastUtil.show(FeedbackActivity.this, R.string.default_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                super.onSuccess(arg0, arg1, arg2);
                dismissWaittingDialog();
                try {
                    String response = new String(arg2, "UTF-8");
                    response = CommonUtil.transResponse(response);
                    JSONObject jsonObject = new JSONObject(response);
                    String cipher = jsonObject.getString("cipher");
                    String desc = MyDES.decrypt(cipher);
                    if (TextUtils.isEmpty(response))
                        return;
                    BaseModel<?> result = new Gson().fromJson(desc, BaseModel.class);
                    if (result.isSuccess()) {
                        ToastUtil.show(FeedbackActivity.this, "提交成功,感谢您的反馈");
                        finish();
                    } else {
                        ToastUtil.show(FeedbackActivity.this, result.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 选择分享方式
     */
    private void showfeedbackWindow(View v) {
        if (popupwindow == null) {
            View view = getLayoutInflater().inflate(R.layout.pop_feedback, null);
            view.findViewById(R.id.ly_all_share).setOnClickListener(this);
            view.findViewById(R.id.tv_share_1).setOnClickListener(this);
            view.findViewById(R.id.tv_share_2).setOnClickListener(this);
            popupwindow = PopupWindowUtil.getPopupWindow(FeedbackActivity.this, view);
            popupwindow.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                }
            });
            popupwindow.setTouchInterceptor(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popupwindow.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
        popupwindow.update();
        popupwindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);

    }
}
