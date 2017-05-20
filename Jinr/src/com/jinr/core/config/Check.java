package com.jinr.core.config;

import android.content.Context;

import com.jinr.core.utils.PreferencesUtils;

/**
 * Check.java
 *
 * @author Andrew Lee
 * @version 2014-10-22 上午10:31:22
 * @description:
 */
public class Check {
    public static boolean is_login(Context context) {
        if (PreferencesUtils.getBooleanFromSPMap(context, PreferencesUtils.Keys.IS_LOGIN)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean has_card(Context context) {
        if (PreferencesUtils.getBooleanFromSPMap(context, PreferencesUtils.Keys.HAS_CARD)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean is_identify(Context context) {
        if (PreferencesUtils.getBooleanFromSPMap(context, PreferencesUtils.Keys.IS_IDENTIFY)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean has_deal_passwd(Context context) {
        if (PreferencesUtils.getBooleanFromSPMap(context, PreferencesUtils.Keys.HAS_DEAL_PASSWD)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean is_ges_locked(Context context) {
        return false;
    }
}
