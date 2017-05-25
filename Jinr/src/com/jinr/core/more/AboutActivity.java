package com.jinr.core.more;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.Check;
import com.jinr.new_mvp.ui.activity.NewLoginActivity;
import com.jinr.core.utils.PreferencesUtils;

public class AboutActivity extends BaseActivity implements OnClickListener {

    private TextView tv_version;
    private String version;
    private boolean is_ts = true;//是否开启推送  true false
    private ImageView push_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initData();
        findViewById();
        initUI();
        setListener();
    }

    @Override
    protected void initData() {
        is_ts = PreferencesUtils.getCBooleanFromSPMap(this, "is_ts");
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packInfo;
            packInfo = packageManager.getPackageInfo(AboutActivity.this.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void findViewById() {
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText("v" + version);
        TextView title_center_text = (TextView) findViewById(R.id.title_center_text);
        push_img = (ImageView) findViewById(R.id.push_img);
        title_center_text.setText("关于鲸鱼");
        findViewById(R.id.title_left_img).setOnClickListener(this);//左上角关闭键
        findViewById(R.id.the_more_cqwenti).setOnClickListener(this);//常见问题
        findViewById(R.id.the_more_feedback).setOnClickListener(this);//意见反馈
        findViewById(R.id.the_more_about).setOnClickListener(this);    //关于我们
    }

    @Override
    protected void initUI() {
        if (is_ts) {
            push_img.setBackgroundResource(R.drawable.an_on);
        } else {
            push_img.setBackgroundResource(R.drawable.an_off);
        }
    }

    @Override
    protected void setListener() {
        findViewById(R.id.the_more_website).setOnClickListener(this);//公司网址
        push_img.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
                break;
            case R.id.the_more_website:
                String url = "http://www.jinr.com/"; // web address
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;
            case R.id.the_more_cqwenti://常见问题
                startActivity(new Intent(AboutActivity.this, CjWentiActivity.class));
                break;
            case R.id.the_more_feedback://意见反馈
                if (Check.is_login(AboutActivity.this)) {
                    startActivity(new Intent(AboutActivity.this, FeedbackActivity.class));
                } else {
                    PreferencesUtils.Keys.BACK_TO_ABOUT = true;
                    Intent mintent = new Intent();
                    mintent.putExtra("about","about");
                    mintent.setClass(AboutActivity.this, NewLoginActivity.class);
                    startActivity(mintent);
                }
                break;
            case R.id.the_more_about://关于我们
                startActivity(new Intent(AboutActivity.this, AboutUsActivity.class));
                break;
            case R.id.push_img:
                if (is_ts) {
                    is_ts = false;
                    PreferencesUtils.putCBooleanToSPMap(this, "is_ts", false);
                    push_img.setBackgroundResource(R.drawable.an_off);
                } else {
                    is_ts = true;
                    PreferencesUtils.putCBooleanToSPMap(this, "is_ts", true);
                    push_img.setBackgroundResource(R.drawable.an_on);
                }
                break;
            default:
                break;
        }
    }
}
