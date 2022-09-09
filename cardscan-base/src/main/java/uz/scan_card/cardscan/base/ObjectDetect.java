package uz.scan_card.cardscan.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import uz.scan_card.cardscan.base.ssd.ArrUtils;
import uz.scan_card.cardscan.base.ssd.DetectedSSDBox;
import uz.scan_card.cardscan.base.ssd.PredictionAPI;
import uz.scan_card.cardscan.base.ssd.PriorsGen;
import uz.scan_card.cardscan.base.ssd.Result;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ObjectDetect {
    /**
     * We don't use the following two for now
     */
    public static boolean USE_GPU = false;
    private static SSDDetect ssdDetect = null;
    private static float[][] priors = null;
    private final File ssdModelFile;
    public List<DetectedSSDBox> objectBoxes = new ArrayList<>();
    boolean hadUnrecoverableException = false;

    public ObjectDetect(File modelFile) {
        this.ssdModelFile = modelFile;
    }

    static boolean isInit() {
        return ssdDetect != null;
    }

    private void ssdOutputToPredictions(Bitmap image) {
        ArrUtils arrUtils = new ArrUtils();

        float[][] k_boxes = arrUtils.rearrangeArray(ssdDetect.outputLocations, SSDDetect.featureMapSizes,
                SSDDetect.NUM_OF_PRIORS_PER_ACTIVATION, SSDDetect.NUM_OF_CORDINATES);
        k_boxes = arrUtils.reshape(k_boxes, SSDDetect.NUM_OF_PRIORS, SSDDetect.NUM_OF_CORDINATES);
        k_boxes = arrUtils.convertLocationsToBoxes(k_boxes, priors,
                SSDDetect.CENTER_VARIANCE, SSDDetect.SIZE_VARIANCE);
        k_boxes = arrUtils.centerFormToCornerForm(k_boxes);
        float[][] k_scores = arrUtils.rearrangeArray(ssdDetect.outputClasses, SSDDetect.featureMapSizes,
                SSDDetect.NUM_OF_PRIORS_PER_ACTIVATION, SSDDetect.NUM_OF_CLASSES);
        k_scores = arrUtils.reshape(k_scores, SSDDetect.NUM_OF_PRIORS, SSDDetect.NUM_OF_CLASSES);
        k_scores = arrUtils.softmax2D(k_scores);

         PredictionAPI predAPI = new PredictionAPI();
        Result result = predAPI.predictionAPI(k_scores, k_boxes, SSDDetect.PROB_THRESHOLD,
                SSDDetect.IOU_THRESHOLD, SSDDetect.CANDIDATE_SIZE, SSDDetect.TOP_K);
        if (result.pickedBoxProbs.size() != 0 && result.pickedLabels.size() != 0) {
            for (int i = 0; i < result.pickedBoxProbs.size(); ++i) {
                DetectedSSDBox ssdBox = new DetectedSSDBox(
                        result.pickedBoxes.get(i)[0], result.pickedBoxes.get(i)[1],
                        result.pickedBoxes.get(i)[2], result.pickedBoxes.get(i)[3], result.pickedBoxProbs.get(i),
                        image.getWidth(), image.getHeight(), result.pickedLabels.get(i));
                objectBoxes.add(ssdBox);
            }
        }


    }

    private String runModel(Bitmap image) {
        final long startTime = SystemClock.uptimeMillis();

        /**Run SSD Model and use the prediction API to post process
         * the model output */

        ssdDetect.classifyFrame(image);
        if (GlobalConfig.PRINT_TIMING) {
            Log.e("Before SSD Post Process", String.valueOf(SystemClock.uptimeMillis() - startTime));
        }
        ssdOutputToPredictions(image);
        if (GlobalConfig.PRINT_TIMING) {
            Log.e("After SSD Post Process", String.valueOf(SystemClock.uptimeMillis() - startTime));
        }

        return "Success";
    }

    public synchronized String predictOnCpu(Bitmap image, Context context) {
        final int NUM_THREADS = 4;
        try {
            boolean createdNewModel = false;

            try {
                if (ssdDetect == null) {
                    ssdDetect = new SSDDetect(context, ssdModelFile);
                    ssdDetect.setNumThreads(NUM_THREADS);
                    /** Since all the frames use the same set of priors
                     * We generate these once and use for all the frame
                     */
                    if (priors == null) {
                        priors = PriorsGen.combinePriors();
                    }

                }
            } catch (Error | Exception e) {
                Log.e("SSD", "Couldn't load ssd", e);
            }


            try {
                return runModel(image);
            } catch (Error | Exception e) {
                Log.i("ObjectDetect", "runModel exception, retry object detection", e);
                ssdDetect = new SSDDetect(context, ssdModelFile);
                return runModel(image);
            }
        } catch (Error | Exception e) {
            Log.e("ObjectDetect", "unrecoverable exception on ObjectDetect", e);
            hadUnrecoverableException = true;
            return null;
        }
    }
}
