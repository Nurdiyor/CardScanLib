package uz.scan_card.cardscan.base.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

public class BitmapHelper {
    public static Bitmap byteArrayToBitmap(byte[] b) {
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }
    public static Drawable byteArrayToDrawable(Resources resources, byte[] b) {
        return new BitmapDrawable(resources,BitmapFactory.decodeByteArray(b, 0, b.length));
    }


    public static byte[] getByteArray(Drawable drawable) {

        Bitmap bitmap;

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

}
