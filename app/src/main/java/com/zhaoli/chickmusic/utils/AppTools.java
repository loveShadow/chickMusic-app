package com.zhaoli.chickmusic.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


/**
 * Created by zhaoli on 2015/10/17.
 */
public class AppTools {
    public static void hideInputSoft(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static String toUTF8(String text) {
        String strUTF8;
        try {
            strUTF8 = URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return text;
        }
        return strUTF8;
    }

    public static void showToast(String content, Context context) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
