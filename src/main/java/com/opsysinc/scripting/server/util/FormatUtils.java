package com.opsysinc.scripting.server.util;

import com.opsysinc.scripting.shared.JobDataFormat;
import com.opsysinc.scripting.shared.JobDataUtils;
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


    private static Map<JobDataFormat, XStream> dataFormats;

    static {

        final EnumMap<JobDataFormat, XStream> tempDataFormats = new EnumMap<>(JobDataFormat.class);

        tempDataFormats.put(JobDataFormat.xml, new XStream(new Xpp3Driver()));
        tempDataFormats.put(JobDataFormat.json, new XStream(new JsonHierarchicalStreamDriver()));

        FormatUtils.dataFormats = Collections.unmodifiableMap(tempDataFormats);
    }

    /**
     * Basic ctor (private for util class).
     */
    private FormatUtils() {
    }

    /**
     * Formats an object as a string, per supplied format.
     *
     * @param input      Input Object.
     * @param dataFormat Input data format.
     * @return String representation of input object.
     */
    public static String formatObject(final Object input,
                                      final JobDataFormat dataFormat) {

        JobDataUtils.checkNullObject(dataFormat, true);
        String result = null;

        if (input != null) {

            Class inputClazz = input.getClass();

            if (inputClazz.isArray()) {

                inputClazz = inputClazz.getComponentType();
            }

            if (Serializable.class.isAssignableFrom(inputClazz)) {

                final XStream xstream = FormatUtils.dataFormats.get(dataFormat);

                if (xstream != null) {

                    result = xstream.toXML(input);
                }
            }
        }

        if (result == null) {

            result = String.valueOf(input);
        }

        return result;
    }
}
