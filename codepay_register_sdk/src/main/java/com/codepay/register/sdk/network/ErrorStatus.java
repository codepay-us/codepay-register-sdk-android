package com.codepay.register.sdk.network;

/**
 * Created by Android Studio.
 * User: pupan
 * Date: 2022/6/29
 * Time: 15:08
 *
 * @author pupan
 */
public class ErrorStatus {

    /**
     * unknown error
     */
    public final static int UNKNOWN_ERROR = 1002;

    /**
     * Server internal error
     */
    public final static int SERVER_ERROR = 1003;

    /**
     * Network connection timeout
     */
    public final static int NETWORK_ERROR = 1004;

    /**
     *API parsing exceptions (or third-party data structure changes) and other exceptions
     */
    public final static int API_ERROR = 1005;

    /**
     * sign error
     */
    public final static int SIGN_ERROR = 1006;

    /**
     * un init error
     */
    public final static  int INIT_ERROR = 1007;
}
