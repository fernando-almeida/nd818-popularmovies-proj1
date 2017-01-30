package com.example.falmeida.popularmovies_proj1.picasso;

/**
 * Created by falmeida on 30/01/17.
 */

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.telecom.Call;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public final class MultiFallBackTarget implements Target {
    private static final List<MultiFallBackTarget> TARGETS = new ArrayList<MultiFallBackTarget>();

    private WeakReference<ImageView> weakImage;
    private List<String> fallbacks;
    private Callback callback;

    public MultiFallBackTarget(ImageView image){
        weakImage = new WeakReference<>(image);
        fallbacks = new ArrayList<>();
        TARGETS.add(this);
        callback = null;
    }

    public MultiFallBackTarget(ImageView image, String fallbackUrl){
        weakImage = new WeakReference<>(image);
        fallbacks = new ArrayList<>();
        fallbacks.add(fallbackUrl);
        TARGETS.add(this);
        this.callback = null;
    }

    public MultiFallBackTarget(ImageView image, String fallbackUrl, Callback callback){
        weakImage = new WeakReference<>(image);
        fallbacks = new ArrayList<>();
        fallbacks.add(fallbackUrl);
        TARGETS.add(this);
        this.callback = callback;
    }

    public void addFallback(String fallbackUrl){
        fallbacks.add(fallbackUrl);
    }

    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from){
        removeSelf();

        ImageView image = weakImage.get();
        if(image == null) return;

        image.setImageBitmap(bitmap);

        if ( callback != null ) {
            callback.onSuccess();
        }
    }

    public void onBitmapFailed(Drawable errorDrawable){
        ImageView image = weakImage.get();
        if(image == null) {
            if ( callback != null ) {
                callback.onError();
            }
            removeSelf();
            return;
        }

        if(fallbacks.size() > 0){
            String nextUrl = fallbacks.remove(0);
            // here you call picasso again
            Picasso.with(image.getContext()).load(nextUrl).into(this);
        } else {
            removeSelf();
        }
    }
    public void onPrepareLoad(Drawable placeHolderDrawable){}

    private void removeSelf(){
        TARGETS.remove(this);
    }
}