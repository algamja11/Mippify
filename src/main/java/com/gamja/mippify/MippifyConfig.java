package com.gamja.mippify;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;

public class MippifyConfig {
    public boolean fixMipGeneration = true;
    public int mipBlurSize = 1;

    private File configFile;

    public void loadAll() {
        configFile = new File(Minecraft.getInstance().gameDirectory, "config/mippify.properties");
        try {
            if (configFile.exists()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8.newDecoder()));
                String curLine;
                while ((curLine = in.readLine()) != null) {
                    String[] kv = curLine.split(":", 2);
                    switch (kv[0]) {
                        case "Fix Mipmap Generation" -> fixMipGeneration = Boolean.parseBoolean(kv[1]);
                        case "Mipmap Blur Size" -> mipBlurSize = Math.min(Math.max(Integer.parseInt(kv[1]), 1), 8);
                    }
                }
                in.close();
            }
            saveAll();
        } catch (Exception e) {
            Mippify.LOGGER.error("Failed to load settings!", e);
        }
    }

    public void saveAll() {
        configFile.getParentFile().mkdirs();
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8.newEncoder())));

            out.println("Fix Mipmap Generation:" + fixMipGeneration);
            out.println("Mipmap Blur Size:" + mipBlurSize);

            out.close();
        }catch (Exception e) {
            Mippify.LOGGER.error("Failed to save settings!", e);
        }
    }
}
