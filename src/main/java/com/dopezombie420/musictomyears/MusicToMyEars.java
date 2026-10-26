package com.dopezombie420.musictomyears;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import net.fabricmc.loader.api.FabricLoader;
import net.ornithemc.osl.entrypoints.api.ModInitializer;

public class MusicToMyEars implements ModInitializer {
	private static final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("musictomyears.properties");
	
	// Configuration settings
	public static boolean overrideMusic = true;
	public static boolean overworldExtended = false;
	
	public static int[] survivalDelay = {12000, 12000};
	public static int[] creativeDelay = {2400, 1200};
	public static int[] netherDelay = {2400, 1200};
	public static int[] endDelay = {18000, 6000};
	public static int[] menuDelay = {580, 20};
	
    private static void initConfig() {
    	Properties props = new Properties();
    	
        props.setProperty("overrideMusic", String.valueOf(overrideMusic));
        props.setProperty("overworldExtended", String.valueOf(overworldExtended));
        
        props.setProperty("survivalDelay", String.valueOf(survivalDelay[0] + "-" + survivalDelay[1]));
        props.setProperty("creativeDelay", String.valueOf(creativeDelay[0] + "-" + creativeDelay[1]));
        props.setProperty("netherDelay", String.valueOf(netherDelay[0] + "-" + netherDelay[1]));
        props.setProperty("endDelay", String.valueOf(endDelay[0] + "-" + endDelay[1]));
        props.setProperty("menuDelay", String.valueOf(menuDelay[0] + "-" + menuDelay[1]));
        
        try (OutputStream out = Files.newOutputStream(configFile)) {
            props.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static int[] parseArray(String prop) {
    	return new int[] {
        	Integer.parseInt(prop.split("-")[0]),
        	Integer.parseInt(prop.split("-")[1]),
        };
    }
	
    private static void loadConfig() {
		if (!Files.exists(configFile)) {
			initConfig();
			return;
		}
		
		Properties props = new Properties();
		
        try (InputStream in = Files.newInputStream(configFile)) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        overrideMusic = Boolean.parseBoolean(props.getProperty("overrideMusic"));
        overworldExtended = Boolean.parseBoolean(props.getProperty("overworldExtended"));
        
        survivalDelay = parseArray(props.getProperty("survivalDelay"));
        creativeDelay = parseArray(props.getProperty("creativeDelay"));
        netherDelay = parseArray(props.getProperty("netherDelay"));
        endDelay = parseArray(props.getProperty("endDelay"));
        menuDelay = parseArray(props.getProperty("menuDelay"));
	}
	
	@Override
	public void init() {
		loadConfig();
	}
}
