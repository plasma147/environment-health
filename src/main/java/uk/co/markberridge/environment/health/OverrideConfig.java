package uk.co.markberridge.environment.health;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OverrideConfig {

    private static final Logger log = LoggerFactory.getLogger(OverrideConfig.class);

    private final String name;

    public OverrideConfig(String fileName) {
        this("", fileName);
    }

    public OverrideConfig(String dirLocation, String fileName) {
        checkNotNull(dirLocation, "dirLocation is null");
        checkNotNull(fileName, "fileName is null");
        checkArgument(fileName.endsWith(".yml"), "fileName must end with .yml");

        String dir = dirLocation.isEmpty() || dirLocation.endsWith("/") ? dirLocation : dirLocation + "/";

        // Look for username override file
        String u = System.getProperty("user.name");
        File overrideFile = new File(dir + u + '-' + fileName);
        log.warn("looking for override file :" + overrideFile.getAbsolutePath());

        if (overrideFile.exists()) {
            this.name = dir + overrideFile.getName();
            log.warn("Found configuration override file {}", name);
        } else {
            this.name = dir + fileName;
        }
    }

    public String getName() {
        return name;
    }
}
