package com.jinr.core.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinr.core.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import model.Feddbackinfo;

public class Detial_fbckActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detial_fbck);
        Intent intent = getIntent();
        Feddbackinfo fc = (Feddbackinfo) intent.getSerializableExtra("feedback");
        findViewById(R.id.title_left_img).setOnClickListener(this);
        ((TextView) findViewById(R.id.title_center_text)).setText("反馈详情");
        TextView tv_problem = (TextView) findViewById(R.id.tv_problem);
        tv_problem.setText(fc.problem);
        ImageView img_pic = (ImageView) findViewById(R.id.img_pic);
        if (!fc.image.equals("null") && !fc.image.equals("xxx") && !fc.image.equals("")) {
            ImageLoader.getInstance().displayImage(fc.image, img_pic);
        } else {
            img_pic.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_img:
                finish();
                break;
            default:
                break;
        }
    }
}
