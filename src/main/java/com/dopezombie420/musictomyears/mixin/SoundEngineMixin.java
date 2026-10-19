package com.dopezombie420.musictomyears.mixin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
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
import net.minecraft.entity.mob.monster.boss.EnderDragonEntity;
import net.ornithemc.osl.lifecycle.impl.client.MinecraftAccess;

import com.dopezombie420.musictomyears.MusicToMyEars;

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
	
	private static Minecraft mc = null;
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

	private Sound getMusic(Sounds soundPool, String cat, int cooldown) {
		if (!Objects.equals(cat, soundCat)) {
			if (MusicToMyEars.overrideMusic || cat == "credits" || cat == "boss") {
				system.stop("BgMusic");
			}
			this.musicCooldown = cooldown;
			soundCat = cat;	
		} else {
			if (!system.playing("BgMusic") && !system.playing("streaming")) {
				return soundPool.getRandom();
			} else {
				if (this.musicCooldown <= 0) {
					this.musicCooldown = cooldown;
				}
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
		if (mc == null) {
			mc = MinecraftAccess.getInstance();
		}
		
		if (started && this.options.musicVolume != 0.0F) {
			Sound sound = null;
			
			if (mc.world == null) {
				sound = getMusic(newMusic[5].soundPool, "menu", this.random.nextInt(MusicToMyEars.menuDelay[0]) + MusicToMyEars.menuDelay[1]);
			} else if (mc.screen instanceof CreditsScreen) {
				sound = getMusic(newMusic[4].soundPool, "credits", 0);
			} else if (mc.player.dimension == -1) {
				sound = getMusic(newMusic[1].soundPool, "nether", this.random.nextInt(MusicToMyEars.netherDelay[0]) + MusicToMyEars.netherDelay[1]);
			} else if (mc.player.dimension == 1) {
				if (mc.player.world.getEntityCount(EnderDragonEntity.class) > 0) {
					sound = getMusic(newMusic[3].soundPool, "boss", 0);
				} else {
					sound = getMusic(newMusic[2].soundPool, "end", this.random.nextInt(MusicToMyEars.endDelay[0]) + MusicToMyEars.endDelay[1]);
				}
			} else if (mc.player.abilities.canFly) {
				sound = getMusic(newMusic[0].soundPool, "creative", this.random.nextInt(MusicToMyEars.creativeDelay[0]) + MusicToMyEars.creativeDelay[1]);
			} else {
				sound = getMusic(music, "survival", this.random.nextInt(MusicToMyEars.survivalDelay[0]) + MusicToMyEars.survivalDelay[1]);
			}
			
			if (!system.playing("BgMusic") && !system.playing("streaming")) {
				if (this.musicCooldown > 0 && !mc.paused) {
					this.musicCooldown--;
					LogManager.getLogger().info("music cooldown: " + String.valueOf(this.musicCooldown));
					return;
				}
				
				if (sound != null) {
					system.backgroundMusic("BgMusic", sound.url, sound.path, false);
					system.setVolume("BgMusic", this.options.musicVolume);
					system.play("BgMusic");
				}
			}
		}
	}
}
