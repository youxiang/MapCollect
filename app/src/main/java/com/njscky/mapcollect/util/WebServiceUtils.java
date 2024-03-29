package com.njscky.mapcollect.util;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WebServiceUtils {
    public static final String WEB_SERVER_URL = "http://58.213.48.109/GXXJWebService/Service1.asmx";
    private static final String NAMESPACE = "http://tempuri.org/";

    public static void callWebService(String url, final String methodName,
                                      HashMap<String, String> properties,
                                      final WebServiceCallBack webServiceCallBack) {

        final HttpTransportSE httpTransportSE = new HttpTransportSE(url);
        SoapObject soapObject = new SoapObject(NAMESPACE, methodName);

        if (properties != null) {
            for (Iterator<Map.Entry<String, String>> it = properties.entrySet()
                    .iterator(); it.hasNext(); ) {
                Map.Entry<String, String> entry = it.next();
                soapObject.addProperty(entry.getKey(), entry.getValue());
            }
        }
        final SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER12);

        soapEnvelope.setOutputSoapObject(soapObject);
        soapEnvelope.dotNet = true;
        httpTransportSE.debug = true;
        AppExecutors.NETWORK.execute(() -> {
            SoapObject resultSoapObject = null;
            try {
                httpTransportSE.call(NAMESPACE + methodName, soapEnvelope);
                if (soapEnvelope.getResponse() != null) {
                    resultSoapObject = (SoapObject) soapEnvelope.bodyIn;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                SoapObject finalResultSoapObject = resultSoapObject;
                AppExecutors.MAIN.execute(() -> {
                    if (webServiceCallBack != null) {
                        webServiceCallBack.callBack(finalResultSoapObject);
                    }
                });
            }
        });
    }

    public interface WebServiceCallBack {
        public void callBack(SoapObject result);
    }

}
