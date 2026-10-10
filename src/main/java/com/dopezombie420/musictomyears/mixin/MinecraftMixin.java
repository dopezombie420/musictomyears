package com.dopezombie420.musictomyears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.sound.SoundEngine;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	public SoundEngine soundEngine;
	
	@Inject(method = "tick", at = @At("TAIL"))
	public void tick(CallbackInfo ci) {
		// Allows for the title and credits music to play
		this.soundEngine.tickMusic();
	}
}