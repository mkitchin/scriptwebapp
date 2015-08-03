package com.opsysinc.scripting.server.util;

import com.opsysinc.scripting.shared.JobDataFormat;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Format utils.
 * <p/>
 * Created by Michael J. Kitchin on 7/31/2015.
 */
public final class FormatUtils {


    private static Map<JobDataFormat, XStream> formatXStreams;

    static {

        final EnumMap<JobDataFormat, XStream> tempDataFormats = new EnumMap<>(JobDataFormat.class);

        tempDataFormats.put(JobDataFormat.xml, new XStream(new Xpp3Driver()));
        tempDataFormats.put(JobDataFormat.json, new XStream(new JsonHierarchicalStreamDriver()));

        FormatUtils.formatXStreams = Collections.unmodifiableMap(tempDataFormats);
    }

    /**
     * Basic ctor (private for util class).
     */
    private FormatUtils() {
    }

    /**
     * Get XStream for data format.
     *
     * @param dataFormat Data format.
     * @return XStream if found, null otherwise.
     */
    public static XStream getFormatXStream(final JobDataFormat dataFormat) {

        return FormatUtils.formatXStreams.get(dataFormat);
    }

    /**
     * Formats an object as a string, per supplied format. Will be String.valueOf(input)
     * if (a) input or xstream is null or (b) input is not serializable.
     *
     * @param input   Input Object (optional, may be  null; null/!serializable means result=String.valueOf(input)))
     * @param xstream Input xstream (optional, may be null; null means result=String.valueOf(input))
     * @return String representation of input object.
     */
    public static String formatObject(final Object input,
                                      final XStream xstream) {

        String result = null;

        if ((input != null) &&
                (xstream != null)) {

            Class inputClazz = input.getClass();

            // catch n-dimensional arrays
            while (inputClazz.isArray() &&
                    (inputClazz.getComponentType() != null)) {

                inputClazz = inputClazz.getComponentType();
            }

            if (Serializable.class.isAssignableFrom(inputClazz)) {

                result = xstream.toXML(input);
            }
        }

        if (result == null) {

            result = String.valueOf(input);
        }

        return result;
    }
}
