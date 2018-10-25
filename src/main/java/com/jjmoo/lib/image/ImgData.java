package com.jjmoo.lib.image;

import com.jjmoo.lib.util.FileUtils;

/**
 * Created by user on 17-10-16.
 *
 */
@SuppressWarnings("unused")
public class ImgData {
    private byte[] mData;

    public ImgData(byte[] data) {
        mData = data;
    }

    public void saveToFile(String path) {
        FileUtils.writeBinaryFileIgnoreError(path, mData);
    }
}
