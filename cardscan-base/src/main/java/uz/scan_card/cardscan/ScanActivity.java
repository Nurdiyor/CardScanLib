package uz.scan_card.cardscan;

import static uz.scan_card.cardscan.base.image.BitmapHelper.getByteArray;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import uz.scan_card.cardscan.base.ScanActivityImpl;
import uz.scan_card.cardscan.base.ScanBaseActivity;


/**
 * The ScanActivity class provides the main interface to the scanning functionality. To use this
 * activity, call the {@link ScanActivity#buildIntent(Activity, Boolean, String, String, Drawable, Drawable)} method and override
 * onActivityResult in your own activity to get the result of the scan.
 */
public class ScanActivity {
    private static final String TAG = "ScanActivity";
    public static final int SCAN_REQUEST_CODE = 51234;
    public static final String CARD_NUMBER = "card_number";
    public static final String EXPIRY_MONTH = "expiry_month";
    public static final String EXPIRY_YEAR = "expiry_year";

    /**
     * Starts a ScanActivityImpl activity, using {@param activity} as a parent.
     *
     * @param activity               the parent activity that is waiting for the result of the ScanActivity
     * @param delayShowingExpiration true if the scan activity should delay showing the expiration
     */
    public static Intent buildIntent(@NonNull Activity activity, Boolean delayShowingExpiration, @Nullable String titleText, @Nullable String hintText, @Nullable Drawable torchOnIcon, @Nullable Drawable torchOffIcon) {
        ScanBaseActivity.warmUp(activity.getApplicationContext());
        Intent intent = new Intent(activity, ScanActivityImpl.class);
        intent.putExtra(ScanBaseActivity.DELAY_SHOWING_EXPIRATION, delayShowingExpiration);

        if (titleText != null) {
            intent.putExtra(ScanActivityImpl.SCAN_CARD_TEXT, titleText);
        }

        if (hintText != null) {
            intent.putExtra(ScanActivityImpl.POSITION_CARD_TEXT, hintText);
        }

        if (torchOnIcon != null) {
            intent.putExtra(ScanActivityImpl.SCAN_CARD_TORCH_ON, getByteArray(torchOnIcon));
        }
        if (torchOffIcon != null) {
            intent.putExtra(ScanActivityImpl.SCAN_CARD_TORCH_OFF, getByteArray(torchOffIcon));
        }

//        intent.putExtra(ScanActivityImpl.SCAN_CARD_TEXT, activity.getString(R.string.card_scan_scan_card));
//        intent.putExtra(ScanActivityImpl.POSITION_CARD_TEXT, activity.getString(R.string.card_scan_position_card));
//        activity.startActivityForResult(intent, REQUEST_CODE);

        return intent;
    }


    /**
     * Initializes the machine learning models and GPU hardware for faster scan performance.
     * <p>
     * This optional static method initializes the machine learning models and GPU hardware in a
     * background thread so that when the ScanActivity starts it can complete its first scan
     * quickly. App builders can choose to not call this method and they can call it multiple
     * times safely.
     * <p>
     * This method is thread safe.
     *
     * @param activity the activity that invokes this method, which the library uses to get
     *                 an application context.
     */
    public static void warmUp(@NonNull Activity activity) {
        ScanBaseActivity.warmUp(activity.getApplicationContext());
    }

    /**
     * Starts the scan activity and turns on a small debugging window in the bottom left.
     * <p>
     * This debugging activity helps designers see some of the machine learning model's internals
     * by showing boxes around digits and expiry dates that it detects.
     *
     * @param activity the parent activity that is waiting for the result of the ScanActivity
     */
    public static void startDebug(@NonNull Activity activity) {
        startDebug(activity, null);
    }

    public static void startDebug(@NonNull Activity activity,
                                  @Nullable TestingImageReader imageReader) {
        if (imageReader != null) {
            ScanBaseActivity.sTestingImageReader = new TestingImageBridge(imageReader);
        }
        ScanBaseActivity.warmUp(activity.getApplicationContext());
        Intent intent = new Intent(activity, ScanActivityImpl.class);
        intent.putExtra("debug", true);
        activity.startActivityForResult(intent, SCAN_REQUEST_CODE);
    }

    /**
     * A helper method to use within your onActivityResult method to check if the result is from our
     * scan activity.
     *
     * @param requestCode the requestCode passed into the onActivityResult method
     * @return true if the requestCode matches the requestCode we use for ScanActivity instances
     */
    public static boolean isScanResult(int requestCode) {
        return requestCode == SCAN_REQUEST_CODE;
    }

    public static @Nullable
    CreditCard creditCardFromResult(Intent intent) {
        String number = intent.getStringExtra(ScanActivityImpl.RESULT_CARD_NUMBER);
        String month = intent.getStringExtra(ScanActivityImpl.RESULT_EXPIRY_MONTH);
        String year = intent.getStringExtra(ScanActivityImpl.RESULT_EXPIRY_YEAR);

        if (TextUtils.isEmpty(number)) {
            return null;
        }

        return new CreditCard(number, month, year);
    }
}
