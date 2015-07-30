// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;

public class InstrumentUtils
{
    public static String getURI(final URI theUri) {
        if (theUri == null) {
            return "";
        }
        return getURI(theUri.getScheme(), theUri.getHost(), theUri.getPort(), theUri.getPath());
    }
    
    public static String getURI(final URL theUrl) {
        if (theUrl == null) {
            return "";
        }
        try {
            return getURI(theUrl.toURI());
        }
        catch (URISyntaxException e) {
            return getURI(theUrl.getProtocol(), theUrl.getHost(), theUrl.getPort(), theUrl.getPath());
        }
    }
    
    public static String getURI(final String scheme, final String host, final int port, final String path) {
        final StringBuilder sb = new StringBuilder();
        if (scheme != null) {
            sb.append(scheme);
            sb.append("://");
        }
        if (host != null) {
            sb.append(host);
            if (port >= 0) {
                sb.append(":");
                sb.append(port);
            }
        }
        if (path != null) {
            sb.append(path);
        }
        return sb.toString();
    }
    
    public static void setFinal(final Object context, final Field field, final Object newValue) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        final boolean wasAccessible = modifiersField.isAccessible();
        modifiersField.setAccessible(true);
        field.set(context, newValue);
        modifiersField.setAccessible(wasAccessible);
    }
}
