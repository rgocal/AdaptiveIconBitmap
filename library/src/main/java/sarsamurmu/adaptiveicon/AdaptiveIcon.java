package sarsamurmu.adaptiveicon;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;

import sarsamurmu.adaptiveicon.utils.ImageUtils;
import sarsamurmu.adaptiveicon.utils.PathUtils;

public class AdaptiveIcon {
    public static final int PATH_CIRCLE = 0;
    public static final int PATH_SQUIRCLE = 1;
    public static final int PATH_ROUNDED_SQUARE = 2;
    public static final int PATH_SQUARE = 3;
    public static final int PATH_TEARDROP = 4;

    Drawable fgDrawable, bgDrawable;
    Bitmap scaledBgBitmap, scaledFgBitmap, bgBitmap, fgBitmap;
    Path path, scaledPath;
    Rect pathSize;
    Paint paint;

    private double scale = 1.0;
    private int size;
    private float fgScale = 1;
    private float offsetX, offsetY;

    public AdaptiveIcon() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        setScale(0.6);
        setPath(PATH_CIRCLE);

        size = 256;
    }

    public AdaptiveIcon setForeground(Drawable drawable) {
        fgDrawable = drawable;
        return this;
    }

    public AdaptiveIcon setBackground(Drawable drawable) {
        bgDrawable = drawable;
        return this;
    }

    public AdaptiveIcon setDrawables(Drawable fgDrawableData, Drawable bgDrawableData) {
        fgDrawable = fgDrawableData;
        bgDrawable = bgDrawableData;
        return this;
    }

    @TargetApi(26)
    public AdaptiveIcon setDrawable(AdaptiveIconDrawable drawable) {
        fgDrawable = drawable.getForeground();
        bgDrawable = drawable.getBackground();
        return this;
    }

    public AdaptiveIcon setScale(double scaleData) {
        scale = scaleData;
        return this;
    }

    public AdaptiveIcon setSize(int newSize) {
        size = newSize;
        return this;
    }

    public Bitmap getFgBitmap() {
        if (fgBitmap == null)
            fgBitmap = ImageUtils.drawableToBitmap(fgDrawable);
        return fgBitmap;
    }

    public Bitmap getBgBitmap() {
        if (bgBitmap == null)
            bgBitmap = ImageUtils.drawableToBitmap(bgDrawable);
        return bgBitmap;
    }

    public AdaptiveIcon setPath(int pathType) {
        path = new Path();
        pathSize = new Rect(0, 0, 50, 50);
        switch (pathType) {
            case PATH_CIRCLE:
                path.arcTo(new RectF(pathSize), 0, 359);
                path.close();
                break;
            case PATH_SQUIRCLE:
                setPath("M 50,0 C 10,0 0,10 0,50 C 0,90 10,100 50,100 C 90,100 100,90 100,50 C 100,10 90,0 50,0 Z");
                break;
            case PATH_ROUNDED_SQUARE:
                setPath("M 50,0 L 70,0 A 30,30,0,0 1 100,30 L 100,70 A 30,30,0,0 1 70,100 L 30,100 A 30,30,0,0 1 0,70 L 0,30 A 30,30,0,0 1 30,0 z");
                break;
            case PATH_SQUARE:
                path.lineTo(0, 50);
                path.lineTo(50, 50);
                path.lineTo(50, 0);
                path.lineTo(0, 0);
                path.close();
                break;
            case PATH_TEARDROP:
                setPath("M 50,0 A 50,50,0,0 1 100,50 L 100,85 A 15,15,0,0 1 85,100 L 50,100 A 50,50,0,0 1 50,0 z");
                break;
        }
        return this;
    }

    public AdaptiveIcon setPath(String pathData) {
        path = PathUtils.createPathFromPathData(pathData);
        pathSize = new Rect(0, 0, 100, 100);
        return this;
    }

    private Boolean isPrepared() {
        if (path != null && pathSize != null) {
            return true;
        }
        return false;
    }

    private Boolean isScaled(int width, int height) {
        return scaledBgBitmap != null && (getFgBitmap() == null || scaledFgBitmap != null) && scaledPath != null;
    }

    private Path getScaledPath(Path origPath, Rect origRect, int width, int height) {
        Rect newRect = new Rect(0, 0, width, height);
        int origWidth = origRect.right - origRect.left;
        int origHeight = origRect.bottom - origRect.top;

        Matrix matrix = new Matrix();
        matrix.postScale((float) (newRect.right - newRect.left) / origWidth, (float) (newRect.bottom - newRect.top) / origHeight);

        Path newPath = new Path();
        origPath.transform(matrix, newPath);
        return newPath;
    }

    private Bitmap getScaledBitmap(Bitmap bitmap, int width, int height) {
        if (scale <= 1)
            return ThumbnailUtils.extractThumbnail(bitmap, (int) ((2 - scale) * width), (int) ((2 - scale) * height));
        else if (bitmap.getWidth() > 1 && bitmap.getHeight() > 1) {
            int widthMargin = (int) ((scale - 1) * width);
            int heightMargin = (int) ((scale - 1) * height);

            if (widthMargin > 0 && heightMargin > 0) {
                Bitmap source = ThumbnailUtils.extractThumbnail(bitmap, (int) ((2 - scale) * width), (int) ((2 - scale) * height));
                int dWidth = width + widthMargin;
                int dHeight = height + heightMargin;
                bitmap = Bitmap.createBitmap(dWidth, dHeight, bitmap.getConfig());
                Canvas canvas = new Canvas(bitmap);
                canvas.drawBitmap(source, (dWidth - source.getWidth()) / 2, (dHeight - source.getHeight()) / 2, new Paint());
                return bitmap;
            }
        } else if (bitmap.getWidth() > 0 && bitmap.getHeight() > 0)
            return ThumbnailUtils.extractThumbnail(bitmap, width, height);

        return null;
    }

    public Bitmap render() {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        if (isPrepared()) {
            if (!isScaled(canvas.getWidth(), canvas.getHeight())) {
                scaledPath = getScaledPath(path, pathSize, size, size);
                if (getBgBitmap() != null) {
                    scaledBgBitmap = getScaledBitmap(getBgBitmap(), size, size);
                    scaledFgBitmap = getScaledBitmap(getFgBitmap(), size, size);
                } else if (getFgBitmap() != null)
                    scaledFgBitmap = ThumbnailUtils.extractThumbnail(getFgBitmap(), size, size);
            }

            if (scaledBgBitmap != null) {
                float dx = size * offsetX * 0.066f;
                float dy = size * offsetY * 0.066f;
                if (scaledBgBitmap.getWidth() > size && scaledBgBitmap.getHeight() > size)
                    canvas.scale(2 - ((fgScale + 1) / 2), 2 - ((fgScale + 1) / 2), size / 2, size / 2);
                else {
                    dx = 0;
                    dy = 0;
                }

                float marginX = (scaledBgBitmap.getWidth() - size) / 2;
                float marginY = (scaledBgBitmap.getHeight() - size) / 2;
                canvas.drawBitmap(scaledBgBitmap, dx - marginX, dy - marginY, paint);
            }

            if (scaledFgBitmap != null) {
                canvas.scale(2 - fgScale, 2 - fgScale, size / 2, size / 2);
                float dx = ((size - scaledFgBitmap.getWidth()) / 2) + (size * offsetX * 0.188f);
                float dy = ((size - scaledFgBitmap.getHeight()) / 2) + (size * offsetY * 0.188f);
                canvas.drawBitmap(scaledFgBitmap, dx, dy, paint);
                canvas.scale(fgScale + 1, fgScale + 1, size / 2, size / 2);
            }

            Paint bitmapPaint = new Paint();
            bitmapPaint.setAntiAlias(true);
            bitmapPaint.setShader(new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
            Bitmap iconBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas iconCanvas = new Canvas(iconBitmap);
            iconCanvas.drawPath(scaledPath, bitmapPaint);

            return iconBitmap;
        }
        return null;
    }
}
