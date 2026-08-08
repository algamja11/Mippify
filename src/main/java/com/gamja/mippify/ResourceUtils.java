package com.gamja.mippify;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

public class ResourceUtils {
    private static final String IDENTIFIER_TO_RESOURCE_PATH = "/assets/%s/%s";

    private ResourceUtils() {
    }

    public static InputStream get(Class<?> clazz, Identifier path) {
        Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(path);
        try {
            if (res.isPresent()) {
                return res.get().open();
            }

            String resPath = String.format(IDENTIFIER_TO_RESOURCE_PATH, path.getNamespace(), path.getPath());
            return clazz.getResourceAsStream(resPath);

        } catch (IOException e) {
            Mippify.LOGGER.error("Failed to get resource {}!", path, e);
        }

        return null;
    }
}
