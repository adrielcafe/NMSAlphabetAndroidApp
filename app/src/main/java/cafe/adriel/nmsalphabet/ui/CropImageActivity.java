package cafe.adriel.nmsalphabet.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.isseiaoki.simplecropview.CropImageView;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.event.ImageCroppedEvent;
import cafe.adriel.nmsalphabet.util.Util;

public class CropImageActivity extends AppCompatActivity {

    private String imagePath;

    @BindView(R.id.crop)
    CropImageView cropView;
    @BindView(R.id.fab)
    FloatingActionButton fabView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        ButterKnife.bind(this);

        imagePath = getIntent().getStringExtra(Constant.EXTRA_IMAGE_PATH);
        if(imagePath != null){
            init();
            Toast.makeText(this, getString(R.string.select_phrase_on_image), Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    @OnClick(R.id.fab)
    public void cropImage(){
        if(Util.isConnected(this)) {
            Bitmap croppedImage = cropView.getCroppedBitmap();
            EventBus.getDefault().postSticky(new ImageCroppedEvent(croppedImage));
            finish();
        }
    }

    protected void init() {
        Glide.with(this).load(imagePath).asBitmap().into(new SimpleTarget<Bitmap>(1024, 1024) {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                cropView.setImageBitmap(bitmap);
            }
        });
        Drawable fabIcon = new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_crop)
                .color(Color.WHITE)
                .sizeDp(50);
        fabView.setImageDrawable(fabIcon);
    }
}