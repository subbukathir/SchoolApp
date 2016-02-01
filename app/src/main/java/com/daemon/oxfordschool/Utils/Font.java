package com.daemon.oxfordschool.Utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by daemonsoft on 20/9/15.
 */
public class Font {

    private Typeface type;

    private Context context;

    public Font(Context context) {
        this.context = context;
    }

    public Typeface getHelveticaRegular() {
        type = Typeface.createFromAsset(context.getAssets(),
                "fonts/Roboto-Regular.ttf");
        return type;
    }

    public Typeface getTrajanRegular()
    {
        type = Typeface.createFromAsset(context.getAssets(),"fonts/TrajanPro-Regular.ttf");
        return type;
    }

    public Typeface getHelveticaBold() {
        type = Typeface.createFromAsset(context.getAssets(),
                "fonts/Roboto-Bold.ttf");
        return type;
    }
}
