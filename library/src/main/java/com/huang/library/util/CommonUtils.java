package com.huang.library.util;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.ColorInt;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

/**
 * Created by south_wind on 15/3/9.
 */
public class CommonUtils {
    private static final String TAG = "CommonUtils";

    private static Application app = null;
    private static Resources resources = null;
    private static LayoutInflater layoutInflater = null;
    private static DisplayMetrics dm = null;



	public static Resources getResources(Context context) {
		if (null != context) {
			return resources = context.getResources();
		} else {
            return null;
        }
	}

    public static LayoutInflater getLayoutInflater(Context context) {
        if (null == layoutInflater) {
            layoutInflater = (LayoutInflater)(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        }
        return layoutInflater;
    }



    private static DisplayMetrics getDisplayMetrics(Context context) {
        if (null != context) {
            return  context.getResources().getDisplayMetrics();
        } else {
            return null;
        }
    }

    public static float getDensity(Context context) {
	    if (context != null){
	        return context.getResources().getDisplayMetrics().density;
        }
        return 1.0f;
    }
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        Configuration config = getResources(context).getConfiguration();
        int oldResult = (Configuration.ORIENTATION_PORTRAIT == config.orientation
                ? dm.widthPixels : dm.heightPixels);
        int newResult = dm.widthPixels < dm.heightPixels ? dm.widthPixels
                : dm.heightPixels;
        if (oldResult != newResult) {
            Log.e(TAG,
                    "getScreenWidth's old logic error! oldResult = "
                            + String.valueOf(oldResult) + ", newResult = "
                            + String.valueOf(newResult));
        }
        return newResult;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        Configuration config = getResources(context).getConfiguration();
        int oldResult = (Configuration.ORIENTATION_PORTRAIT == config.orientation ?
                dm.heightPixels : dm.widthPixels);
        int newResult = dm.widthPixels < dm.heightPixels ? dm.heightPixels : dm.widthPixels;
        if (oldResult != newResult) {
            Log.e(TAG,
                    "getScreenHeight's old logic error! oldResult = "
                            + String.valueOf(oldResult) + ", newResult = "
                            + String.valueOf(newResult));
        }
        return newResult;
    }

    public static void showToast(Context context, String toastString) {
        Toast theToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        theToast.setText(toastString);
        theToast.show();
    }
    public static int getSize(Context context, int dp) {
	    if (context != null) {
	        Resources resources = getResources(context);
	        if (resources != null) {
	           DisplayMetrics dm = resources.getDisplayMetrics();
	           if (dm != null) {
	               return (int)(dp * dm.density);
               }
            }
            return (int)(getResources(context).getDisplayMetrics().density * dp);
        } else {
	        return dp;
        }

    }

    public static ShapeDrawable createRoundCornorDrawable(int cornor) {
	    int defaultColor = 0xffF6F6F6;
        return createRoundCornorDrawable(cornor,defaultColor);
    }

    public static ShapeDrawable createRoundCornorDrawable(int cornor, @ColorInt int backgroundColor) {

        //外矩形 左上、右上、右下、左下的圆角半径
        float[] outerRadii = {cornor, cornor, cornor, cornor, cornor, cornor, cornor, cornor};
        //内矩形距外矩形，左上角x,y距离， 右下角x,y距离
        //RectF inset = new RectF(100, 100, 100, 100);
        //内矩形 圆角半径
        //float[] innerRadii = {20, 20, 20, 20, 20, 20, 20, 20};

        RoundRectShape roundRectShape = new RoundRectShape(outerRadii, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
        shapeDrawable.setAlpha(0xff);

        shapeDrawable.getPaint().setColor(backgroundColor);
        shapeDrawable.getPaint().setAntiAlias(true);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);//描边

        return shapeDrawable;
    }


}
