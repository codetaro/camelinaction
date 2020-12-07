package camelinaction.chapter5.usecase;

import org.apache.camel.component.http.HttpOperationFailedException;

public final class MyHttpUtil {

    public static boolean isIllegalDataError(HttpOperationFailedException cause) {
        int code = cause.getStatusCode();
        if (code != 500) return false;
        return "ILLEGAL DATA".equals(cause.getResponseBody().toString());
    }
}
