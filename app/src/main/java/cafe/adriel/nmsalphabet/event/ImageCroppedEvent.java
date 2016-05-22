package cafe.adriel.nmsalphabet.event;

import android.graphics.Bitmap;

public class ImageCroppedEvent {
    public final Bitmap image;

    public ImageCroppedEvent(Bitmap image){
        this.image = image;
    }
}