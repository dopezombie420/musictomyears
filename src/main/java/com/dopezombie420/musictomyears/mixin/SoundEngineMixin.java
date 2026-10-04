package com.dopezombie420.musictomyears.mixin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;

import com.dopezombie420.musictomyears.MusicToMyEars;

import net.minecraft.client.options.GameOptions;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.Sounds;
import paulscode.sound.SoundSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.mob.player.PlayerEntity;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
	
	@Shadow
    private static boolean started;
	@Shadow
	private GameOptions options;
	@Shadow
	private static SoundSystem system;
	@Shadow
	private Sounds music;
	@Shadow
	private Random random;
	@Shadow
	private int musicCooldown = 0;
	
	private static final Logger logger = LogManager.getLogger("Music to my ears!");
	private File resourceDir = new File(Minecraft.getWorkingDirectory(), "resources/");
	
	private Sounds creativeMusic = new Sounds();
	private Sounds netherMusic = new Sounds();
	private Sounds endMusic = new Sounds();
	private Sounds bossMusic = new Sounds();
	private Sounds creditsMusic = new Sounds();
	private Sounds menuMusic = new Sounds();
	
	private void extractAndLoadMusic() {
		String filePath;
		InputStream in;
		
		// Creative music
		for (int i = 1; i <= 6; i++) {
			filePath = "/newnewmusic/game/creative/creative" + String.valueOf(i) + ".ogg";
			logger.info(filePath);
			
			in = getClass().getResourceAsStream(filePath);
			try {
				File desFile = new File(resourceDir.getPath(), filePath);
				desFile.getParentFile().mkdirs();
				logger.info("added file: " + desFile.getPath());
				if (!desFile.exists()) {
				    Files.copy(in, desFile.toPath());
				}
			    
			    creativeMusic.load(filePath, desFile);
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}
		
		// Nether music
		for (int i = 1; i <= 4; i++) {
			filePath = "/newnewmusic/game/nether/nether" + String.valueOf(i) + ".ogg";
			logger.info(filePath);
			
			in = getClass().getResourceAsStream(filePath);
			try {
				File desFile = new File(resourceDir.getPath(), filePath);
				desFile.getParentFile().mkdirs();
				logger.info("added file: " + desFile.getPath());
				if (!desFile.exists()) {
				    Files.copy(in, desFile.toPath());
				}
			    
			    netherMusic.load(filePath, desFile);
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}
		
		// End music
		filePath = "/newnewmusic/game/end/end.ogg";
		logger.info(filePath);
		
		in = getClass().getResourceAsStream(filePath);
		try {
			File desFile = new File(resourceDir.getPath(), filePath);
			desFile.getParentFile().mkdirs();
			logger.info("added file: " + desFile.getPath());
			if (!desFile.exists()) {
			    Files.copy(in, desFile.toPath());
			}
		    
		    endMusic.load(filePath, desFile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		// Boss music
		filePath = "/newnewmusic/game/end/boss.ogg";
		logger.info(filePath);
		
		in = getClass().getResourceAsStream(filePath);
		try {
			File desFile = new File(resourceDir.getPath(), filePath);
			desFile.getParentFile().mkdirs();
			logger.info("added file: " + desFile.getPath());
			if (!desFile.exists()) {
			    Files.copy(in, desFile.toPath());
			}
		    
		    bossMusic.load(filePath, desFile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		// Credits music
		filePath = "/newnewmusic/game/end/credits.ogg";
		logger.info(filePath);
		
		in = getClass().getResourceAsStream(filePath);
		try {
			File desFile = new File(resourceDir.getPath(), filePath);
			desFile.getParentFile().mkdirs();
			logger.info("added file: " + desFile.getPath());
			if (!desFile.exists()) {
			    Files.copy(in, desFile.toPath());
			}
		    
		    creditsMusic.load(filePath, desFile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		// Menu music
		for (int i = 1; i <= 4; i++) {
			filePath = "/newnewmusic/menu/menu" + String.valueOf(i) + ".ogg";
			logger.info(filePath);
			
			in = getClass().getResourceAsStream(filePath);
			try {
				File desFile = new File(resourceDir.getPath(), filePath);
				desFile.getParentFile().mkdirs();
				logger.info("added file: " + desFile.getPath());
				if (!desFile.exists()) {
				    Files.copy(in, desFile.toPath());
				}
			    
			    menuMusic.load(filePath, desFile);
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}
	}

	@Inject(method = "load", at = @At("HEAD"))
	public void load(GameOptions options, CallbackInfo ci) {
		extractAndLoadMusic();
	}
	
	@Overwrite
	public void tickMusic() {
		logger.info("music ticker");
		if (started && this.options.musicVolume != 0.0F) {
			if (!system.playing("BgMusic") && !system.playing("streaming")) {
				if (this.musicCooldown > 0) {
					this.musicCooldown--;
					return;
				}
				
				Sound sound;
				
				if (false) {
					//sound = this.menuMusic.getRandom();
				} else if (Minecraft.getInstance().player.dimension == -1) {
					sound = this.netherMusic.getRandom();
				} else if (Minecraft.getInstance().player.dimension == 1) {
					sound = this.endMusic.getRandom();
				} else if (Minecraft.getInstance().player.abilities.canFly) {
					sound = this.creativeMusic.getRandom();
				} else {
					sound = this.music.getRandom();
				}

				
				if (sound != null) {
					logger.info("Music Playing: " + sound.path + " | " + sound.url);
					this.musicCooldown = this.random.nextInt(12000) + 12000;
					system.backgroundMusic("BgMusic", sound.url, sound.path, false);
					system.setVolume("BgMusic", this.options.musicVolume);
					system.play("BgMusic");
				}
			}
		}
	}
}
