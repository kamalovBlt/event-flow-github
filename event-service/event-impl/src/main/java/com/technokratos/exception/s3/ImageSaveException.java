package com.technokratos.exception.s3;

import com.technokratos.exception.EventServiceException;

public class ImageSaveException extends EventServiceException {
    public ImageSaveException(String s) {
        super(s);
    }
}
