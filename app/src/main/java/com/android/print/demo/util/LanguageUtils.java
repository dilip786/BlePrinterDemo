package com.android.print.demo.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

public class LanguageUtils {

    private static final String TAG = "LanguageUtils";

    public static final String SELECT_LANGUAGE = "select_language";
    // 中文
    public static final String CHINESE = "简体中文";
    // 英文
    public static final String ENGLISH = "English";


    /**
     * 主动点击切换语言
     */
    public static void changeLanguage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(LanguageUtils.SELECT_LANGUAGE, Context.MODE_PRIVATE);
        String selectedLanguage = preferences.getString(LanguageUtils.SELECT_LANGUAGE, "");
        if (CHINESE.equals(selectedLanguage)) {
            selectedLanguage = ENGLISH;
        } else {
            selectedLanguage = CHINESE;
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECT_LANGUAGE, selectedLanguage);
        editor.apply();

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale;
        if (CHINESE.equals(selectedLanguage)) {
            locale = Locale.ENGLISH;
        } else {
            locale = Locale.CHINESE;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // apply locale
            configuration.setLocale(locale);
        } else {
            // updateConfiguration
            configuration.locale = locale;
            DisplayMetrics dm = resources.getDisplayMetrics();
            resources.updateConfiguration(configuration, dm);
        }
    }

    /**
     * 页面加载
     *
     * @param context
     * @return
     */

    @SuppressLint("ObsoleteSdkInt")
    public static Context attachBaseContext(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(LanguageUtils.SELECT_LANGUAGE,
                Context.MODE_PRIVATE);
        String selectedLanguage = preferences.getString(LanguageUtils.SELECT_LANGUAGE, "");
        Log.d(TAG, "attachBaseContext: 语言" + selectedLanguage);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale;
        if (CHINESE.equals(selectedLanguage)) {

            locale = Locale.ENGLISH;
        } else {
            System.out.println("here");
            locale = Locale.CHINESE;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // apply locale
            configuration.setLocale(locale);
        } else {
            // updateConfiguration
            configuration.locale = locale;
            DisplayMetrics dm = resources.getDisplayMetrics();
            resources.updateConfiguration(configuration, dm);
        }

        return context;
    }

    /**
     * 获取选择的语言
     */
    public static String getSelectLanguage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(LanguageUtils.SELECT_LANGUAGE,
                Context.MODE_PRIVATE);
        return preferences.getString(LanguageUtils.SELECT_LANGUAGE, "");
    }

    /**
     * 判断是中文还是英语
     */
    public static Boolean isChinese(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(LanguageUtils.SELECT_LANGUAGE,
                Context.MODE_PRIVATE);
        String selectLanguage = preferences.getString(LanguageUtils.SELECT_LANGUAGE, "");
        return CHINESE.equals(selectLanguage);
    }

    /***
     *自定义获取对应语言字符串
     * @param context
     * @param language 语言(如：zh)
     * @return
     */
    public static String getLanguage(Context context, String language, int resourcesId) {
        Locale locale = new Locale(language);
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration).getResources().getString(resourcesId);
    }

    /**
     * 获取系统首选语言
     *
     * @return Locale
     */
    public static Locale getSystemPreferredLanguage() {
        return Locale.getDefault();
    }

    /**
     * 获取支持语言
     *
     * @param language language
     * @return
     */
    public static Locale getSupportLanguage(String language) {
        if (TextUtils.isEmpty(language)) {
            return Locale.CHINESE;
        }
        if (language.equals(CHINESE)) {
            return Locale.CHINESE;
        }
        if (language.equals(ENGLISH)) {
            return Locale.ENGLISH;
        }
        return Locale.CHINESE;
    }

    /**
     * 是否支持此语言
     *
     * @param language language
     * @return true:支持 false:不支持
     */
    public static boolean isSupportLanguage(String language) {
        return language.equals(Locale.CHINESE.getLanguage()) || language.equals(Locale.ENGLISH.getLanguage());
    }

}
