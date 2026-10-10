package com.dopezombie420.musictomyears.mixin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.options.GameOptions;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.Sounds;
import paulscode.sound.SoundSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.entity.mob.monster.boss.EnderDragonEntity;

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
	private int musicCooldown;
	
	private static final Logger logger = LogManager.getLogger("Music to my ears!");
	private File resourceDir = new File(Minecraft.getWorkingDirectory(), "resources/");
	
	private String soundCat = null;
	
	public final class SoundRegistry {
		public String path;
		public int count;
		public Sounds soundPool = new Sounds();
		
        public SoundRegistry(String path, int count) {
            this.path = path;
            this.count = count;
        }
	}
	
	private SoundRegistry[] newMusic = {
		new SoundRegistry("game/creative/creative", 6),
		new SoundRegistry("game/nether/nether", 4),
		new SoundRegistry("game/end/end", 1),
		new SoundRegistry("game/end/boss", 1),
		new SoundRegistry("game/end/credits", 1),
		new SoundRegistry("menu/menu", 4)
	};
	
	private void extractAndLoadMusic() {
		String filePath;
		InputStream in;
		
		for (int i = 0; i < newMusic.length; i++) {
			for (int j = 1; j <= newMusic[i].count; j++) {
				if (newMusic[i].count == 1) {
					filePath = "/newnewmusic/" + newMusic[i].path + ".ogg";
				} else {
					filePath = "/newnewmusic/" + newMusic[i].path + String.valueOf(j) + ".ogg";
				}
				
				in = getClass().getResourceAsStream(filePath);
				try {
					File desFile = new File(resourceDir.getPath(), filePath);
					desFile.getParentFile().mkdirs();
					logger.info("added file: " + desFile.getPath());
					if (!desFile.exists()) {
					    Files.copy(in, desFile.toPath());
					}
				    
					newMusic[i].soundPool.load(filePath, desFile);
				} catch (IOException e) {
				    e.printStackTrace();
				}
			}
		}
	}

	private Sound getMusic(Sounds soundPool, String cat) {		
		if (!Objects.equals(cat, soundCat)) {
			system.stop("BgMusic");
			this.musicCooldown = 0;
			//this.musicCooldown = this.random.nextInt(12000) + 12000;
			soundCat = cat;	
		} else {
			if (!system.playing("BgMusic") && !system.playing("streaming")) {
				return soundPool.getRandom();
			}
		}
		
		return null;
	}
	
	@Inject(method = "load", at = @At("HEAD"))
	public void load(GameOptions options, CallbackInfo ci) {
		extractAndLoadMusic();
	}
	
	@Overwrite
	public void tickMusic() {		
		if (started && this.options.musicVolume != 0.0F) {
			Sound sound = null;
			
			if (false) {
				//sound = this.menuMusic.getRandom();
			} else if (Minecraft.getInstance().screen instanceof CreditsScreen) {
				this.musicCooldown = 0;
				sound = getMusic(newMusic[4].soundPool, "credits");
			} else if (Minecraft.getInstance().player.world.getEntityCount(EnderDragonEntity.class) > 0) {
				this.musicCooldown = 0;
				sound = getMusic(newMusic[3].soundPool, "boss");
			} else if (Minecraft.getInstance().player.dimension == -1) {
				sound = getMusic(newMusic[1].soundPool, "nether");
			} else if (Minecraft.getInstance().player.dimension == 1) {
				this.musicCooldown = 0;
				sound = getMusic(newMusic[2].soundPool, "end");
			} else if (Minecraft.getInstance().player.abilities.canFly) {
				sound = getMusic(newMusic[0].soundPool, "creative");
			} else {
				sound = getMusic(music, "survival");
			}
			
			if (!system.playing("BgMusic") && !system.playing("streaming")) {
				if (this.musicCooldown > 0) {
					this.musicCooldown--;
					return;
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
