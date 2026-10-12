package com.dopezombie420.musictomyears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.minecraft.client.sound.SoundEngine;

@Mixin(targets = "net.minecraft.client.ClientPlayerInteractionManager")
public class ClientPlayerInteractionManagerMixin {
    @WrapWithCondition(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sound/SoundEngine;tickMusic()V"
        ),
        require = 0 // Skip if the class doesnt exist <= 1.2.5
    )
    private boolean skipTickMusic(SoundEngine instance) {
        return false; // Skips the original music tick call to save on processing power
    }
}