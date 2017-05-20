package com.jinr.core.security.lockpanttern.pattern;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinr.core.MainActivity;
import com.jinr.core.R;
import com.jinr.core.base.BaseActivity;
import com.jinr.core.config.AppManager;
import com.jinr.core.security.lockpanttern.view.LockPatternUtils;
import com.jinr.core.security.lockpanttern.view.LockPatternView;
import com.jinr.core.security.lockpanttern.view.LockPatternView.Cell;
import com.jinr.core.security.lockpanttern.view.LockPatternView.DisplayMode;
import com.jinr.core.utils.MyLog;
import com.jinr.core.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置图形
 *
 * @author fym creatOn 2014.11.3
 */

public class CreateGesturePasswordActivity extends BaseActivity implements OnClickListener {
    private static final int ID_EMPTY_MESSAGE = -1;
    private static final String KEY_UI_STAGE = "uiStage";
    private static final String KEY_PATTERN_CHOICE = "chosenPattern";
    private LockPatternView mLockPatternView;
    // private Button mFooterRightButton; //去掉底部右侧按钮
    private Button mFooterLeftButton;
    protected TextView mHeaderText;
    protected List<LockPatternView.Cell> mChosenPattern = null;
    private Toast mToast;
    private Stage mUiStage = Stage.Introduction;
    private View mPreviewViews[][] = new View[3][3];
    private ImageView title_left_img; // title左边图片
    /**
     * The patten used during the help screen to show how to draw a pattern.
     */
    private final List<LockPatternView.Cell> mAnimatePattern = new ArrayList<>();

    /**
     * The states of the left footer button.
     */
    enum LeftButtonMode {
        Cancel(R.string.lockpattern_cancel_button_text, true), CancelDisabled(
                R.string.lockpattern_cancel_button_text, false), Retry(
                R.string.lockpattern_retry_button_text, true), RetryDisabled(
                R.string.lockpattern_retry_button_text, false), Gone(
                ID_EMPTY_MESSAGE, false);

        /**
         * @param text    The displayed text for this mode.
         * @param enabled Whether the button should be enabled.
         */
        LeftButtonMode(int text, boolean enabled) {
            this.text = text;
            this.enabled = enabled;
        }

        final int text;
        final boolean enabled;
    }

    /**
     * Keep track internally of where the user is in choosing a pattern.
     */
    protected enum Stage {
        Introduction(R.string.lockpattern_recording_intro_header,
                LeftButtonMode.Cancel, ID_EMPTY_MESSAGE, true), HelpScreen(
                R.string.lockpattern_settings_help_how_to_record,
                LeftButtonMode.Gone, ID_EMPTY_MESSAGE, false), ChoiceTooShort(
                R.string.lockpattern_recording_incorrect_too_short,
                LeftButtonMode.Retry, ID_EMPTY_MESSAGE, true), FirstChoiceValid(
                R.string.lockpattern_pattern_entered_header,
                LeftButtonMode.Retry, ID_EMPTY_MESSAGE, false), NeedToConfirm(
                R.string.lockpattern_need_to_confirm, LeftButtonMode.Retry,
                ID_EMPTY_MESSAGE, true), ConfirmWrong(
                R.string.lockpattern_need_to_unlock_wrong,
                LeftButtonMode.Retry, ID_EMPTY_MESSAGE, true), ChoiceConfirmed(
                R.string.lockpattern_pattern_confirmed_header,
                LeftButtonMode.Retry, ID_EMPTY_MESSAGE, false);

        /**
         * @param headerMessage  The message displayed at the top.
         * @param leftMode       The mode of the left button.
         * @param footerMessage  The footer message.
         * @param patternEnabled Whether the pattern widget is enabled.
         */
        Stage(int headerMessage, LeftButtonMode leftMode, int footerMessage,
              boolean patternEnabled) {
            this.headerMessage = headerMessage;
            this.leftMode = leftMode;
            this.footerMessage = footerMessage;
            this.patternEnabled = patternEnabled;
        }

        final int headerMessage;
        final LeftButtonMode leftMode;
        final int footerMessage;
        final boolean patternEnabled;
    }

    private void showToast(CharSequence message) {
        if (null == mToast) {
            mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesturepassword_create);
        // 初始化演示动画
        mAnimatePattern.add(LockPatternView.Cell.of(0, 0));
        mAnimatePattern.add(LockPatternView.Cell.of(0, 1));
        mAnimatePattern.add(LockPatternView.Cell.of(1, 1));
        mAnimatePattern.add(LockPatternView.Cell.of(2, 1));
        mAnimatePattern.add(LockPatternView.Cell.of(2, 2));
        mLockPatternView = (LockPatternView) this.findViewById(R.id.gesturepwd_create_lockview);
        mHeaderText = (TextView) findViewById(R.id.gesturepwd_create_text);
        mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
        mLockPatternView.setTactileFeedbackEnabled(true);
        title_left_img = (ImageView) findViewById(R.id.title_left_img_lock);
        title_left_img.setOnClickListener(this);
        mFooterLeftButton = (Button) this.findViewById(R.id.reset_btn);
        mFooterLeftButton.setOnClickListener(this);
        initPreviewViews();
        if (savedInstanceState == null) {
            updateStage(Stage.Introduction);
            // updateStage(Stage.HelpScreen); //注释这行可以使解锁的帮助界面不出现
        } else {
            final String patternString = savedInstanceState.getString(KEY_PATTERN_CHOICE);
            if (patternString != null) {
                mChosenPattern = LockPatternUtils.stringToPattern(patternString);
            }
            updateStage(Stage.values()[savedInstanceState.getInt(KEY_UI_STAGE)]);
        }
    }

    private void initPreviewViews() {
        mPreviewViews = new View[3][3];
        mPreviewViews[0][0] = findViewById(R.id.gesturepwd_setting_preview_0);
        mPreviewViews[0][1] = findViewById(R.id.gesturepwd_setting_preview_1);
        mPreviewViews[0][2] = findViewById(R.id.gesturepwd_setting_preview_2);
        mPreviewViews[1][0] = findViewById(R.id.gesturepwd_setting_preview_3);
        mPreviewViews[1][1] = findViewById(R.id.gesturepwd_setting_preview_4);
        mPreviewViews[1][2] = findViewById(R.id.gesturepwd_setting_preview_5);
        mPreviewViews[2][0] = findViewById(R.id.gesturepwd_setting_preview_6);
        mPreviewViews[2][1] = findViewById(R.id.gesturepwd_setting_preview_7);
        mPreviewViews[2][2] = findViewById(R.id.gesturepwd_setting_preview_8);
    }

    // 重置预览九宫格
    private void resetPreviewViews() {
        for (int i = 0; i < mPreviewViews.length; i++) {
            View[] views = mPreviewViews[i];
            for (int j = 0; j < views.length; j++) {
                View view = views[j];
                view.setBackgroundResource(R.drawable.gesture_create_grid_unselected);
            }
        }
    }

    private void updatePreviewViews(List<Cell> pattern) {
        if (pattern == null)
            return;
        for (LockPatternView.Cell cell : pattern) {
            mPreviewViews[cell.getRow()][cell.getColumn()].setBackgroundResource(R.drawable.gesture_create_grid_selected);
        }
    }

    private void updatePreviewViews() {
        if (mChosenPattern == null)
            return;
        for (LockPatternView.Cell cell : mChosenPattern) {
            mPreviewViews[cell.getRow()][cell.getColumn()].setBackgroundResource(R.drawable.gesture_create_grid_selected);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_UI_STAGE, mUiStage.ordinal());
        if (mChosenPattern != null) {
            outState.putString(KEY_PATTERN_CHOICE, LockPatternUtils.patternToString(mChosenPattern));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mUiStage == Stage.HelpScreen) {
                updateStage(Stage.Introduction);
                return true;
            }
            sendBroadcast(new Intent().setAction("action.refresh_actdetail"));
            finish();
        }
        if (keyCode == KeyEvent.KEYCODE_MENU && mUiStage == Stage.Introduction) {
            updateStage(Stage.HelpScreen);
            return true;
        }
        return false;
    }

    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
            resetPreviewViews(); // 重置预览九宫格
        }
    };

    protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {
        public void onPatternStart() {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
        }

        public void onPatternCleared() {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
        }

        public void onPatternDetected(List<LockPatternView.Cell> pattern) {
            if (pattern == null)
                return;
            if (mUiStage == Stage.NeedToConfirm || mUiStage == Stage.ConfirmWrong) {
                if (mChosenPattern == null)
                    throw new IllegalStateException("null chosen pattern in stage 'need to confirm");
                if (mChosenPattern.equals(pattern)) {
                    saveChosenPatternAndFinish();
                } else {
                    updateStage(Stage.ConfirmWrong);
                }
            } else if (mUiStage == Stage.Introduction || mUiStage == Stage.ChoiceTooShort) {
                if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
                    updateStage(Stage.ChoiceTooShort);
                } else {
                    mChosenPattern = new ArrayList<>(pattern);
                    updateStage(Stage.NeedToConfirm);
                }
            } else {
                throw new IllegalStateException("Unexpected stage " + mUiStage + " when " + "entering the pattern.");
            }
        }

        public void onPatternCellAdded(List<Cell> pattern) {
            updatePreviewViews(pattern);
        }

        private void patternInProgress() {
            mHeaderText.setText(R.string.lockpattern_recording_inprogress);
            mFooterLeftButton.setEnabled(false);
        }
    };

    private void updateStage(Stage stage) {
        mUiStage = stage;
        if (stage == Stage.ChoiceTooShort) {
            mHeaderText.setText(getResources().getString(stage.headerMessage, LockPatternUtils.MIN_LOCK_PATTERN_SIZE));
        } else {
            mHeaderText.setText(stage.headerMessage);
        }

        if (stage.leftMode == LeftButtonMode.Gone) {
            mFooterLeftButton.setVisibility(View.GONE);
        } else {
            mFooterLeftButton.setVisibility(View.VISIBLE);
            mFooterLeftButton.setText(stage.leftMode.text);
            mFooterLeftButton.setEnabled(stage.leftMode.enabled);
        }

        if (stage.patternEnabled) {
            mLockPatternView.enableInput();
        } else {
            mLockPatternView.disableInput();
        }

        mLockPatternView.setDisplayMode(DisplayMode.Correct);
        switch (mUiStage) {
            case Introduction:
                mLockPatternView.clearPattern();
                mHeaderText.setText("请绘制手势密码");
                mHeaderText.setTextColor(getResources().getColor(R.color.black));
                break;
            case HelpScreen:
                mLockPatternView.setPattern(DisplayMode.Animate, mAnimatePattern);
                break;
            case ChoiceTooShort:
                mHeaderText.setTextColor(getResources().getColor(R.color.gesture_text_wrong_red));
                mLockPatternView.setDisplayMode(DisplayMode.Wrong);
                postClearPatternRunnable();
                break;
            case FirstChoiceValid:
                mHeaderText.setTextColor(getResources().getColor(R.color.white));
                break;
            case NeedToConfirm:
                mHeaderText.setTextColor(getResources().getColor(R.color.white));
                mLockPatternView.clearPattern();
                resetPreviewViews(); // 重置预览九宫格
                break;
            case ConfirmWrong:
                mHeaderText.setTextColor(getResources().getColor(R.color.gesture_text_wrong_red));
                mLockPatternView.setDisplayMode(DisplayMode.Wrong);
                postClearPatternRunnable();
                break;
            case ChoiceConfirmed:
                break;
        }

    }

    private void postClearPatternRunnable() {
        mLockPatternView.removeCallbacks(mClearPatternRunnable);
        mLockPatternView.postDelayed(mClearPatternRunnable, 2000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_btn:
                if (mUiStage.leftMode == LeftButtonMode.Retry) {
                    resetPreviewViews(); // 重置预览九宫格
                    mChosenPattern = null;
                    mLockPatternView.clearPattern();
                    updateStage(Stage.Introduction);
                } else if (mUiStage.leftMode == LeftButtonMode.Cancel) {
                    Intent intent = getIntent();
                    String str = intent.getStringExtra("gotoMainActive");
                    if (str != null && str.equals("gotoMainActive")) {
                        PreferencesUtils.putBooleanToSPMap(CreateGesturePasswordActivity.this, PreferencesUtils.Keys.IS_SETTING_CG, false);
                        Intent intent_MainActivity = new Intent(this, MainActivity.class);
                        startActivity(intent_MainActivity);
                    } else {
                        if (PreferencesUtils.Keys.BACK_TO_ACT != 0 || PreferencesUtils.Keys.BACK_TO_GIFT || PreferencesUtils.Keys.BACK_TO_ABOUT) {
                            PreferencesUtils.Keys.BACK_TO_ACT = 0;
                            PreferencesUtils.Keys.BACK_TO_GIFT = false;
                            PreferencesUtils.Keys.BACK_TO_ABOUT = false;
                            sendBroadcast(new Intent().setAction("action.refresh_actdetail"));
                        }
                    }
                    finish();
                } else {
                    throw new IllegalStateException("left footer button pressed, but stage of " + mUiStage + " doesn't make sense");
                }

                break;
            case R.id.title_left_img_lock:
                Intent intent = getIntent();
                String str = intent.getStringExtra("gotoMainActive");
                if (str != null && str.equals("gotoMainActive")) {
                    PreferencesUtils.putBooleanToSPMap(CreateGesturePasswordActivity.this, PreferencesUtils.Keys.IS_SETTING_CG, false);
                    Intent intent_MainActivity = new Intent(this, MainActivity.class);
                    startActivity(intent_MainActivity);
                } else {
                    if (PreferencesUtils.Keys.BACK_TO_ACT != 0 || PreferencesUtils.Keys.BACK_TO_GIFT || PreferencesUtils.Keys.BACK_TO_ABOUT) {
                        PreferencesUtils.Keys.BACK_TO_ACT = 0;
                        PreferencesUtils.Keys.BACK_TO_GIFT = false;
                        PreferencesUtils.Keys.BACK_TO_ABOUT = false;
                        sendBroadcast(new Intent().setAction("action.refresh_actdetail"));
                    }
                }
                finish();
                break;
        }
    }


    @SuppressWarnings("static-access")
    private void saveChosenPatternAndFinish() {
        LockPatternUtils set_lock = new LockPatternUtils(this);
        LockPatternUtils.clearLock();
        set_lock.saveLockPattern(mChosenPattern);
        set_lock.lockPattern_on(PreferencesUtils.getValueFromSPMap(this, PreferencesUtils.Keys.UID));
        // mFooterLeftButton.setText("密码设置成功");
        showToast("手势密码设置成功");
        if (PreferencesUtils.Keys.BACK_TO_ACT != 0 || PreferencesUtils.Keys.BACK_TO_GIFT || PreferencesUtils.Keys.BACK_TO_ABOUT) {
            PreferencesUtils.Keys.BACK_TO_ACT = 0;
            PreferencesUtils.Keys.BACK_TO_GIFT = false;
            PreferencesUtils.Keys.BACK_TO_ABOUT = true;
            sendBroadcast(new Intent().setAction("action.refresh_actdetail"));
        }
        finish();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void setListener() {

    }
}
