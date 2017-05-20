package com.jinr.core.gift.share;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * 将bitmap对象转化成byte数据字节的对象
 * 
 * @author CQJ
 * 
 */
public class BitmapToByteUtil {
	/**
	 * 
	 * @param bmp
	 *            Bitmap对象
	 * @param needRecycle
	 * @return byte[]
	 */
	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 不行就用这个
	 * 
	 * @param bmp
	 * @param needRecycle
	 * @return
	 */
	public static byte[] bmpToByteArrays(final Bitmap bmp,
			final boolean needRecycle) {

		int i;
		int j;
		if (bmp.getHeight() > bmp.getWidth()) {
			i = bmp.getWidth();
			j = bmp.getWidth();
		} else {
			i = bmp.getHeight();
			j = bmp.getHeight();
		}

		Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
		Canvas localCanvas = new Canvas(localBitmap);

		while (true) {
			localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i,
					j), null);
			if (needRecycle)
				bmp.recycle();
			ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
					localByteArrayOutputStream);
			localBitmap.recycle();
			byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
			try {
				localByteArrayOutputStream.close();
				return arrayOfByte;
			} catch (Exception e) {
				// F.out(e);
			}
			i = bmp.getHeight();
			j = bmp.getHeight();
		}
	}
}
