package com.opsysinc.scripting.client.app;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Script web resources.
 * <p/>
 * Created by Michael J. Kitchin on 8/30/2015.
 */
public interface ScriptWebResources extends ClientBundle {

    @ClientBundle.Source("resources/images/folder-icon.png")
    ImageResource getFolderIconResource();

    @ClientBundle.Source("resources/images/file-icon.png")
    ImageResource getFileIconResource();

    @ClientBundle.Source("resources/images/default-icon.png")
    ImageResource getDefaultIconResource();
}
