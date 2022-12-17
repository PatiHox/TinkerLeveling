package org.embeddedt.tinkerleveling.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SimplePersistentCapabilityProvider<C, S extends INBT> implements ICapabilityProvider, INBTSerializable<S> {
    private final Capability<C> capability;
    private final LazyOptional<C> implementation;
    private final Direction direction;

    protected SimplePersistentCapabilityProvider(@Nonnull final Capability<C> capability, @Nonnull final LazyOptional<C> implementation, @Nullable final Direction direction) {
        this.capability = capability;
        this.implementation = implementation;
        this.direction = direction;
    }

    @Nonnull
    public static <C> SimplePersistentCapabilityProvider<C, INBT> from(@Nonnull final Capability<C> cap, @Nonnull final NonNullSupplier<C> impl) {
        return from(cap, null, impl);
    }

    @Nonnull
    public static <C> SimplePersistentCapabilityProvider<C, INBT> from(@Nonnull final Capability<C> cap, @Nullable final Direction direction, @Nonnull final NonNullSupplier<C> impl) {
        return new SimplePersistentCapabilityProvider<>(cap, LazyOptional.of(impl), direction);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, @Nullable final Direction side) {
        if (cap == this.capability) return this.implementation.cast();
        return LazyOptional.empty();
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public S serializeNBT() {
        return (S) this.capability.writeNBT(this.getInstance(), this.direction);
    }

    @Override
    public void deserializeNBT(@Nonnull final S nbt) {
        this.capability.readNBT(this.getInstance(), this.direction, nbt);
    }

    @Nonnull
    private C getInstance() {
        return this.implementation.orElseThrow(() -> new IllegalStateException("Unable to obtain capability instance"));
    }
}
