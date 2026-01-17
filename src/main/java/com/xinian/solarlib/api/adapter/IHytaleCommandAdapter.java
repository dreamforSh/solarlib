package com.xinian.solarlib.api.adapter;

import com.xinian.solarlib.api.command.ICommand;
import com.xinian.solarlib.api.command.ICommandSender;

import javax.annotation.Nonnull;

/**
 * Adapter interface for bridging Hytale commands to SolarLib API
 */
public interface IHytaleCommandAdapter {
    
    /**
     * Adapt a SolarLib command to Hytale's command system
     *
     * @param command The SolarLib command to adapt
     * @return The Hytale command object
     */
    @Nonnull
    Object adaptToHytale(@Nonnull ICommand command);
    
    /**
     * Adapt a Hytale command to SolarLib's command system
     *
     * @param hytaleCommand The Hytale command to adapt
     * @return The SolarLib command
     */
    @Nonnull
    ICommand adaptFromHytale(@Nonnull Object hytaleCommand);
    
    /**
     * Adapt a Hytale command sender to SolarLib's command sender interface
     *
     * @param hytaleCommandSender The Hytale command sender
     * @return The SolarLib command sender
     */
    @Nonnull
    ICommandSender adaptCommandSender(@Nonnull Object hytaleCommandSender);
}
