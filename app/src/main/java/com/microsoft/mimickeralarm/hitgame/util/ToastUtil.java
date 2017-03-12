/**
 * Copyright (C) 2012 Guangzhou JHComn Technologies Ltd.
 *
 * 本代码版权归广州佳和立创科技发展有限公司所有，且受到相关的法律保护。
 * 没有经过版权所有者的书面同意，
 * 任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。

 */
package com.microsoft.mimickeralarm.hitgame.util;

import android.content.Context;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;


/**
 * <p>信息提示框类
 * </p>
 *
 * @author ljmat
 * @date 2012-3-9 下午01:25:41
 *
 */
public class ToastUtil {

	/**
	 * 短时显示
	 * @param context	上下文
	 * @param msg	提示内容
	 */
	public static void showShort(Context context, String msg){
		Toast toast = new Toast(context);
		TextView view = new TextView(context);
		view.setText(msg);
		view.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
		toast.setView(view);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
		
	}
	
	/**
	 * 长时显示
	 * @param context	上下文
	 * @param msg	提示内容
	 */
	public static void showLong(Context context, String msg){
		Toast toast = new Toast(context);
		TextView view = new TextView(context);
		view.setText(msg);
		view.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
		toast.setView(view);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();
	}
	
}
