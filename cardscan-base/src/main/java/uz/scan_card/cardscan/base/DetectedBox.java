package uz.scan_card.cardscan.base;


import androidx.annotation.NonNull;

public class DetectedBox implements Comparable {
    private final float confidence;
    private final int numRows;
    private final int numCols;
    private final CGSize boxSize;
    private final CGSize cardSize;
    private final CGSize imageSize;
    CGRect rect;
    int row;
    int col;

    DetectedBox(int row, int col, float confidence, int numRows, int numCols,
                CGSize boxSize, CGSize cardSize, CGSize imageSize) {

        // Resize the box to transform it from the model's coordinates into
        // the image's coordinates
        float w = boxSize.width * imageSize.width / cardSize.width;
        float h = boxSize.height * imageSize.height / cardSize.height;
        float x = (imageSize.width - w) / ((float) (numCols - 1)) * ((float) col);
        float y = (imageSize.height - h) / ((float) (numRows - 1)) * ((float) row);
        this.rect = new CGRect(x, y, w, h);
        this.row = row;
        this.col = col;
        this.confidence = confidence;
        this.numRows = numRows;
        this.numCols = numCols;
        this.boxSize = boxSize;
        this.cardSize = cardSize;
        this.imageSize = imageSize;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return Float.compare(this.confidence, ((DetectedBox) o).confidence);
    }

    public float getMinX() {
        return this.rect.x;
    }

    public float getMinY() {
        return this.rect.y;
    }

    public float getMaxX() {
        return this.rect.x + this.rect.width;
    }

    public float getMaxY() {
        return this.rect.y + this.rect.height;
    }
}
