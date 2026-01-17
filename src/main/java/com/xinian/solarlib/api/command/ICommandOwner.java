package com.xinian.solarlib.api.command;

import javax.annotation.Nonnull;

/**
 * Command owner interface
 * Extracted from Hytale's CommandOwner
 */
public interface ICommandOwner {
    
    /**
     * Get the name of this command owner
     *
     * @return The owner name
     */
    @Nonnull
    String getName();
}
