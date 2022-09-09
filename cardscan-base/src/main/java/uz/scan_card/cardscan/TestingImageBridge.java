package uz.scan_card.cardscan;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import uz.scan_card.cardscan.base.TestingImageReaderInternal;

class TestingImageBridge implements TestingImageReaderInternal {
    private final TestingImageReader testingImageReader;

    TestingImageBridge(@NonNull TestingImageReader testingImageReader) {
        this.testingImageReader = testingImageReader;
    }

    @Override
    public Bitmap nextImage() {
        return this.testingImageReader.nextImage();
    }
}
