package com.opsysinc.scripting.server.servlet;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * File upload servlet.
 *
 * @author mkit
 */
public class FileUploadServlet extends HttpServlet {

    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default max file size in bytes.
     */
    private static final int DEFAULT_MAX_FILE_SIZE_IN_BYTES = 10 * 1024 * 1024;

    /**
     * Default buffer size in bytes.
     */
    private static final int DEFAULT_BUFFER_SIZE_IN_BYTES = 8192;

    /**
     * Post operation.
     */
    @Override
    public void doPost(final HttpServletRequest request,
                             final HttpServletResponse response) throws ServletException,
            IOException {

        final ServletFileUpload upload = new ServletFileUpload();

        try {

            final FileItemIterator iter = upload.getItemIterator(request);
            final int maxFileSize = FileUploadServlet.DEFAULT_MAX_FILE_SIZE_IN_BYTES;

            while (iter.hasNext()) {

                final FileItemStream fileItemStream = iter.next();
                final String fieldName = fileItemStream.getFieldName();
                final InputStream inputStream = fileItemStream.openStream();

                // Process the input stream
                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                int readInBytes = 0;
                final byte[] buffer = new byte[FileUploadServlet.DEFAULT_BUFFER_SIZE_IN_BYTES];

                while ((readInBytes = inputStream
                        .read(buffer, 0, buffer.length)) != -1) {

                    outputStream.write(buffer, 0, readInBytes);
                }

                if (outputStream.size() > maxFileSize) {

                    throw new RuntimeException("File is > than " + maxFileSize);
                }
            }

        } catch (final Throwable ex) {

            throw new RuntimeException(ex);
        }

    }
}