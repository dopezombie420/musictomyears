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
import net.minecraft.entity.living.mob.monster.boss.EnderDragonEntity;
import net.ornithemc.osl.lifecycle.impl.client.MinecraftAccess;

import com.dopezombie420.musictomyears.MusicToMyEars;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
	
	@Shadow
    private boolean started;
	@Shadow
	private GameOptions options;
	@Shadow
	private SoundSystem system;
	@Shadow
	private Sounds music;
	@Shadow
	private Random random;
	@Shadow
	private int musicCooldown;
	@Shadow
	private File assetsDir;
	
	private static Minecraft mc = Minecraft.getInstance();
	private File musicDir = new File(assetsDir, "virtual/legacy/sounds/music/");
	
	private String soundCat = null;
	
	// Music pools
	private Sounds creativeMusic = new Sounds(mc.getResourceManager(), "music", true);
	private Sounds netherMusic = new Sounds(mc.getResourceManager(), "music", true);
	private Sounds endMusic = new Sounds(mc.getResourceManager(), "music", true);
	private Sounds bossMusic = new Sounds(mc.getResourceManager(), "music", true);
	private Sounds creditsMusic = new Sounds(mc.getResourceManager(), "music", true);
	private Sounds menuMusic = new Sounds(mc.getResourceManager(), "music", true);
	
	@Inject(method = "load(Ljava/io/File;)V", at = @At("TAIL"))
	private void onLoad(File file, CallbackInfo ci) {
		LogManager.getLogger().info("path: " + file.getPath());
		
		String string3 = this.assetsDir.toURI().relativize(file.toURI()).getPath();
		int i = string3.indexOf("/");
		if (i != -1) {
			string3 = string3.substring(i + 1).replace('\\', '/');
			LogManager.getLogger().info("string3: " + string3);
			if (string3.contains("music/game/creative".replace('\\', '/'))) {
				creativeMusic.load(string3);
			} else if (string3.contains("music/game/nether".replace('\\', '/'))) {
				netherMusic.load(string3);
			} else if (string3.contains("music/game/end/end".replace('\\', '/'))) {
				endMusic.load(string3);
			} else if (string3.contains("music/game/end/boss".replace('\\', '/'))) {
				bossMusic.load(string3);
			} else if (string3.contains("music/game/end/credits".replace('\\', '/'))) {
				creditsMusic.load(string3);
			} else if (string3.contains("music/menu".replace('\\', '/'))) {
				menuMusic.load(string3);
			}
		}
	}
	
	@Overwrite
	public void tickMusic() {
		if (started && this.options.musicVolume != 0.0F) {
			Sound sound = null;
			
			if (mc.world == null) {
				sound = menuMusic.getRandom();
			} else if (mc.screen instanceof CreditsScreen) {
				sound = creditsMusic.getRandom();
			} else if (mc.player.dimension == -1) {
				sound = netherMusic.getRandom();
			} else if (mc.player.dimension == 1) {
				if (mc.player.world.getEntityCount(EnderDragonEntity.class) > 0) {
					bossMusic.getRandom();
				} else {
					endMusic.getRandom();
				}
			} else if (mc.player.abilities.canFly) {
				sound = creativeMusic.getRandom();
			} else {
				sound = music.getRandom();
			}
			
			if (!system.playing("BgMusic") && !system.playing("streaming")) {
				/*if (this.musicCooldown > 0 && !mc.paused) {
					this.musicCooldown--;
					return;
				}*/
				
				if (sound != null) {
					system.backgroundMusic("BgMusic", sound.getUrl(), sound.getPath(), false);
					system.setVolume("BgMusic", this.options.musicVolume);
					system.play("BgMusic");
				}
			}
		}
	}
}
