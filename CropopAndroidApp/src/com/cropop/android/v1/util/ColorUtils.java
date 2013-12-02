package com.cropop.android.v1.util;

import android.graphics.Color;

public class ColorUtils {
	public static int setTransparency(int color, int alpha){
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		return Color.HSVToColor(alpha,hsv);
	}
}
