package com.jmarkstar.uploading.api;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jmarkstar.uploading.utils.AppUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by jmarkstar on 30/09/2017.
 */

public class ProgressRequestBody extends RequestBody {

    private File mFile;
    private UploadCallbacks mListener;
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    public ProgressRequestBody(final File file, final  UploadCallbacks listener) {
        mFile = file;
        mListener = listener;
        Log.v("ProgressRequestBody", "content type : "+ AppUtils.getMimeType(file.getAbsolutePath()));
    }

    @Nullable @Override public MediaType contentType() {;
        return MediaType.parse("image/*");
    }

    @Override public long contentLength() throws IOException {
        return mFile.length();
    }

    @Override public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;

        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {
                uploaded += read;
                sink.write(buffer, 0, read);
                handler.post(new ProgressUpdater(uploaded, fileLength));
            }
        }finally {
            in.close();
        }
    }

    private class ProgressUpdater implements Runnable {

        private long mUploaded;
        private long mTotal;

        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override public void run() {
            mListener.onProgressUpdate((int)(100 * mUploaded / mTotal));
        }
    }

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage);
    }
}
