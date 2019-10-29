package com.njscky.mapcollect.business.layer;

import java.io.File;

public class LayerParameter {
    public String name;
    public File localDir;
    public String serverUrl;

    public LayerParameter() {
    }

    public LayerParameter(String name, File localDir, String serverUrl) {
        this.name = name;
        this.localDir = localDir;
        this.serverUrl = serverUrl;
    }
}
