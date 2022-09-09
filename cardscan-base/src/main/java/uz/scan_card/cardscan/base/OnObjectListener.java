package uz.scan_card.cardscan.base;

import android.graphics.Bitmap;

import uz.scan_card.cardscan.base.ssd.DetectedSSDBox;

import java.util.List;

interface OnObjectListener {
    public void onPrediction(final Bitmap bitmap, List<DetectedSSDBox> boxes, int imageWidth,
                      int imageHeight, final Bitmap fullScreenBitmap);

    public void onObjectFatalError();
}
