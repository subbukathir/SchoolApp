package com.daemon.oxfordschool.components;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.EditText;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.Utils.Font;

import java.lang.reflect.Field;

/**
 * Created by daemonsoft on 21/12/15.
 */
public class CustomTextInputLayout extends TextInputLayout {

    Font font = MyApplication.getInstance().getFontInstance();

    public CustomTextInputLayout(Context context) {
        super(context);
        initFont(context);
    }

    public CustomTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFont(context);
    }

    private void initFont(Context context) {


        EditText editText = getEditText();
        if (editText != null) {
            editText.setTypeface(font.getHelveticaRegular());
            editText.setSingleLine(true);
        }
        try {
            // Retrieve the CollapsingTextHelper Field
            final Field cthf = TextInputLayout.class.getDeclaredField("mCollapsingTextHelper");
            cthf.setAccessible(true);

            // Retrieve an instance of CollapsingTextHelper and its TextPaint
            final Object cth = cthf.get(this);
            final Field tpf = cth.getClass().getDeclaredField("mTextPaint");
            tpf.setAccessible(true);

            // Apply your Typeface to the CollapsingTextHelper TextPaint
            ((TextPaint) tpf.get(cth)).setTypeface(font.getHelveticaRegular());
        } catch (Exception ignored) {
            // Nothing to do
        }
    }
}
