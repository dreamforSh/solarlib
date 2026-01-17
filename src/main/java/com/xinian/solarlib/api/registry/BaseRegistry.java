package com.xinian.solarlib.api.registry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Base implementation of IRegistry
 * Extracted from Hytale's Registry class
 *
 * @param <T> The type of registration
 */
public abstract class BaseRegistry<T extends IRegistration> implements IRegistry<T> {
    @Nonnull
    private final BooleanSupplier precondition;
    private final String preconditionMessage;
    @Nonnull
    private final List<IRegistrationConsumer> registrations;
    @Nonnull
    private final List<IRegistrationConsumer> unmodifiableRegistrations;
    private boolean enabled;
    
    protected BaseRegistry(@Nonnull List<IRegistrationConsumer> registrations,
                          @Nonnull BooleanSupplier precondition,
                          String preconditionMessage) {
        this.registrations = new ArrayList<>(registrations);
        this.unmodifiableRegistrations = Collections.unmodifiableList(this.registrations);
        this.precondition = precondition;
        this.preconditionMessage = preconditionMessage;
        this.enabled = false;
    }
    
    @Override
    public void checkPrecondition() {
        if (!precondition.getAsBoolean()) {
            String message = preconditionMessage != null ? preconditionMessage : "Precondition not met";
            throw new IllegalStateException(message);
        }
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public void enable() {
        if (!enabled) {
            checkPrecondition();
            enabled = true;
            onEnable();
        }
    }
    
    @Override
    public void shutdown() {
        if (enabled) {
            enabled = false;
            onShutdown();
        }
    }
    
    @Override
    public T register(@Nonnull T registration) {
        checkPrecondition();
        T wrappedRegistration = wrapRegistration(registration);
        registrations.add(value -> {
            if (value) {
                onRegistrationEnabled(wrappedRegistration);
            } else {
                onRegistrationDisabled(wrappedRegistration);
            }
        });
        return wrappedRegistration;
    }
    
    @Nonnull
    @Override
    public List<IRegistrationConsumer> getRegistrations() {
        return unmodifiableRegistrations;
    }
    
    /**
     * Wrap a registration with additional functionality
     *
     * @param registration The registration to wrap
     * @return The wrapped registration
     */
    @Nonnull
    protected abstract T wrapRegistration(@Nonnull T registration);
    
    /**
     * Called when the registry is enabled
     */
    protected void onEnable() {
        // Override in subclasses if needed
    }
    
    /**
     * Called when the registry is shut down
     */
    protected void onShutdown() {
        // Override in subclasses if needed
    }
    
    /**
     * Called when a registration is enabled
     *
     * @param registration The enabled registration
     */
    protected void onRegistrationEnabled(@Nonnull T registration) {
        // Override in subclasses if needed
    }
    
    /**
     * Called when a registration is disabled
     *
     * @param registration The disabled registration
     */
    protected void onRegistrationDisabled(@Nonnull T registration) {
        // Override in subclasses if needed
    }
    
    @Nonnull
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{enabled=" + enabled + ", registrations=" + registrations.size() + "}";
    }
}
