package uz.scan_card.cardscan.base.ssd;

import android.graphics.RectF;

import org.json.JSONException;
import org.json.JSONObject;


public class DetectedSSDBox {
    public float confidence;
    public int label;
    public RectF rect;
    float XMin, YMin, XMax, YMax;
    int imageWidth, imageHeight;


    public DetectedSSDBox(float XMin, float YMin, float XMax, float YMax, float confidence,
                          int imageWidth, int imageHeight, int label) {

        this.XMin = XMin * imageWidth;
        this.XMax = XMax * imageWidth;
        this.YMin = YMin * imageHeight;
        this.YMax = YMax * imageHeight;
        this.confidence = confidence;
        this.label = label;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.rect = new RectF(this.XMin, this.YMin, this.XMax, this.YMax);
    }

    public JSONObject toJson() {
        try {
            JSONObject result = new JSONObject();
            result.put("x_min", this.XMin / imageWidth);
            result.put("y_min", this.YMin / imageHeight);
            result.put("width", (this.XMax - this.XMin) / imageWidth);
            result.put("height", (this.YMax - this.YMin) / imageHeight);
            result.put("label", this.label);
            result.put("confidence", this.confidence);
            return result;
        } catch (JSONException je) {
            je.printStackTrace();
            return new JSONObject();
        }
    }
}

