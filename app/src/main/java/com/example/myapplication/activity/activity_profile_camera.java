package com.example.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;

import com.example.myapplication.R;
import com.example.myapplication.fragment.fragment_profile_camera2Basic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class activity_profile_camera extends activity_main_basic {
    private fragment_profile_camera2Basic camera2BasicFragment;
    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            //mBackgroundHandler.post(new Camera2BasicFragment.ImageUpLoader(reader.acquireNextImage()));
            Image mImage = reader.acquireNextImage();
            File mFile = new File(getExternalFilesDir(null), "profileImage.jpg");

            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Intent resultIntent = new Intent();
            resultIntent.putExtra("profilePath", mFile.toString());
            setResult(Activity.RESULT_OK, resultIntent);

            camera2BasicFragment.closeCamera();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_camera);
        if (null == savedInstanceState) {
            camera2BasicFragment = new fragment_profile_camera2Basic();
            camera2BasicFragment.setOnImageAvailableListener(mOnImageAvailableListener);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, camera2BasicFragment)
                    .commit();
        }
    }

}
