package com.xinian.solarlib.api;

/**
 * SolarLib API Overview
 * 
 * This package contains the core API extracted from Hytale's command and registry systems.
 * SolarLib provides simplified interfaces for rapid development on Hytale servers.
 * 
 * ## Purpose
 * 
 * SolarLib is a library for quick implementation of Hytale commands and registrations.
 * It extracts and simplifies Hytale's complex command and registry APIs into easy-to-use interfaces.
 * 
 * ## Package Structure
 * 
 * ### com.xinian.solarlib.api.registry
 * - IRegistry: Registry interface for managing registrations
 * - IRegistration: Registration interface for registered objects
 * - IRegistrationConsumer: Functional interface for registration callbacks
 * - BaseRegistry: Base implementation of IRegistry
 * - BaseRegistration: Base implementation of IRegistration
 * 
 * ### com.xinian.solarlib.api.command
 * - ICommand: Command interface
 * - ICommandSender: Command sender interface
 * - ICommandContext: Command execution context interface
 * - ICommandOwner: Command owner interface
 * - ICommandRegistry: Command registry interface
 * - ICommandRegistration: Command registration interface
 * - BaseCommand: Base implementation of ICommand
 * - BaseCommandRegistration: Base implementation of ICommandRegistration
 * 
 * ### com.xinian.solarlib.api.adapter
 * - IHytaleCommandAdapter: Adapter for Hytale command system
 * - IHytaleRegistryAdapter: Adapter for Hytale registry system
 * - IAdapterFactory: Factory for creating adapters
 * 
 * ## Usage
 * 
 * ### Creating a Command
 * ```java
 * public class MyCommand extends BaseCommand {
 *     public MyCommand() {
 *         super("mycommand", "My command description");
 *     }
 *     
 *     &#064;Override
 *     public CompletableFuture<Void> execute(ICommandContext context) {
 *         context.sendMessage("Hello!");
 *         return CompletableFuture.completedFuture(null);
 *     }
 * }
 * ```
 * 
 * ### Using Registry
 * ```java
 * IRegistry<IRegistration> registry = new BaseRegistry<>(...);
 * registry.register(registration);
 * ```
 * 
 * ### Adapting to Hytale
 * ```java
 * IAdapterFactory factory = ...;
 * IHytaleCommandAdapter adapter = factory.getCommandAdapter();
 * Object hytaleCommand = adapter.adaptToHytale(myCommand);
 * ```
 * 
 * @author SolarLib Team
 * @version 1.0
 */
public final class ApiOverview {
    private ApiOverview() {
        throw new UnsupportedOperationException("This is a documentation class");
    }
}
