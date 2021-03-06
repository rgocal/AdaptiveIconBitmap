package example.sarsamurmu.adaptiveiconbitmap;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

import sarsamurmu.adaptiveicon.AdaptiveIcon;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadIcon();
    }

    public void loadIcon() {
        Drawable iconDrawable = getApplicationContext().getDrawable(R.drawable.my_adaptive_icon);
        AdaptiveIconDrawable adaptiveIconDrawable = ((AdaptiveIconDrawable) iconDrawable);

        ImageView squareIcon = findViewById(R.id.square);
        loadIconToView(adaptiveIconDrawable, AdaptiveIcon.PATH_SQUARE, squareIcon);

        ImageView roundedSquareIcon = findViewById(R.id.rounded_square);
        loadIconToView(adaptiveIconDrawable, AdaptiveIcon.PATH_ROUNDED_SQUARE, roundedSquareIcon);

        ImageView squircleIcon = findViewById(R.id.squircle);
        loadIconToView(adaptiveIconDrawable, AdaptiveIcon.PATH_SQUIRCLE, squircleIcon);

        ImageView circleIcon = findViewById(R.id.circle);
        loadIconToView(adaptiveIconDrawable, AdaptiveIcon.PATH_CIRCLE, circleIcon);

        ImageView teardropIcon = findViewById(R.id.teardrop);
        loadIconToView(adaptiveIconDrawable, AdaptiveIcon.PATH_TEARDROP, teardropIcon);
    }

    public void loadIconToView(AdaptiveIconDrawable drawable, int path, ImageView view) {
        view.setImageBitmap(new AdaptiveIcon()
                .setDrawable(drawable)
                .setPath(path)
                .render());
    }

    public void saveImages(View v) {
        Drawable iconDrawable = getApplicationContext().getDrawable(R.drawable.my_adaptive_icon);
        AdaptiveIconDrawable adaptiveIconDrawable = ((AdaptiveIconDrawable) iconDrawable);

        saveImage(genBitmap(adaptiveIconDrawable, AdaptiveIcon.PATH_SQUARE), "square");
        saveImage(genBitmap(adaptiveIconDrawable, AdaptiveIcon.PATH_ROUNDED_SQUARE), "rounded_square");
        saveImage(genBitmap(adaptiveIconDrawable, AdaptiveIcon.PATH_SQUIRCLE), "squircle");
        saveImage(genBitmap(adaptiveIconDrawable, AdaptiveIcon.PATH_CIRCLE), "circle");
        saveImage(genBitmap(adaptiveIconDrawable, AdaptiveIcon.PATH_TEARDROP), "teardrop");
    }

    public Bitmap genBitmap(AdaptiveIconDrawable drawable, int path) {

        return new AdaptiveIcon()
                .setDrawable(drawable)
                .setPath(path)
                .render();
    }

    private void saveImage(Bitmap finalBitmap, String image_name) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/AdaptiveIconBitmap");
        String fname = image_name + ".png";
        File file = new File(myDir, fname);
        Log.i("LOAD", root + "/AdaptiveIconBitmap/" + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
