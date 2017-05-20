package com.jinr.core.utils;

import android.content.Context;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by: Administrator on 2017/1/5.
 */
public class AuthImageDownloader extends BaseImageDownloader {

    private SSLSocketFactory mSSLSocketFactory;
    public AuthImageDownloader(Context context) {
        super(context);
        SSLContext sslContext = sslContextForTrustedCertificates();
        mSSLSocketFactory = sslContext.getSocketFactory();
    }
    public AuthImageDownloader(Context context, int connectTimeout, int readTimeout) {
        super(context, connectTimeout, readTimeout);
        SSLContext sslContext = sslContextForTrustedCertificates();
        mSSLSocketFactory = sslContext.getSocketFactory();
    }
    @Override
    protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
        URL url = null;
        try {
            url = new URL(imageUri);
        } catch (MalformedURLException e) {
        }
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);

        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection)conn).setSSLSocketFactory(mSSLSocketFactory);
            ((HttpsURLConnection)conn).setHostnameVerifier((DO_NOT_VERIFY));
        }
        return new BufferedInputStream(conn.getInputStream(), BUFFER_SIZE);
    }
    // always verify the host - dont check for certificate
    final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };




    public SSLContext sslContextForTrustedCertificates() {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, null);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return sc;
        }
    }
}
