package com.metype.makecraft.config;

import com.metype.makecraft.MakeCraft;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ConfigUtils {

    private static String ensureConfigExists(String configFileName) throws IOException {
        FabricLoader loader = FabricLoader.getInstance();
        String configFilePath = loader.getConfigDir() + "/" + MakeCraft.MOD_ID + "/" + configFileName;
        if (!new File(configFilePath).exists()) {
            File configFile = new File(configFilePath);
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
        }
        return configFilePath;
    }

    public static Reader getFileReaderForConfigFile(String fileName) throws IOException {
        return new InputStreamReader(new FileInputStream(ensureConfigExists(fileName)));
    }

    public static Writer getFileWriterForConfigFile(String fileName) throws IOException {
        return new OutputStreamWriter(new FileOutputStream(ensureConfigExists(fileName)));
    }

    public static LocalDateTime getRestartTime(String dateFmt) {
        LocalDateTime restart = LocalDateTime.now();
        String[] dateFmtArgs = Arrays.stream(dateFmt.split(" ")).map(String::trim).toArray(String[]::new);
        for(String arg : dateFmtArgs) {
            if(arg.endsWith("d")) {
                arg = arg.substring(0, arg.length() - 1);
                int days = Integer.parseInt(arg);
                if(days <= 0) continue;
                restart = restart.plusDays(days - (days % restart.getDayOfYear())); // Move up to the next correct day interval
            }
            if(arg.endsWith("h")) {
                arg = arg.substring(0, arg.length() - 1);
                int hourNum = Integer.parseInt(arg);
                hourNum = hourNum - restart.getHour();
                restart = restart.plusHours(hourNum); // And then rewind to the time they specified
            }
            if(arg.endsWith("m")) {
                arg = arg.substring(0, arg.length() - 1);
                int minNum = Integer.parseInt(arg);
                minNum = minNum - restart.getMinute();
                restart = restart.plusMinutes(minNum); // And then rewind to the time they specified
            }
            if(arg.endsWith("s")) {
                arg = arg.substring(0, arg.length() - 1);
                int secNum = Integer.parseInt(arg);
                secNum = secNum - restart.getSecond();
                restart = restart.plusSeconds(secNum); // And then rewind to the time they specified
            }
        }
        return restart;
    }
}

