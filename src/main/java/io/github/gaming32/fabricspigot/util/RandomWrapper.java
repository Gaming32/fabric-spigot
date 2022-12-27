package io.github.gaming32.fabricspigot.util;

import net.minecraft.util.math.random.RandomSplitter;

import java.util.Random;

public final class RandomWrapper implements net.minecraft.util.math.random.Random {
    private final Random random;

    public RandomWrapper(Random random) {
        this.random = random;
    }

    @Override
    public net.minecraft.util.math.random.Random split() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RandomSplitter nextSplitter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSeed(long seed) {
        random.setSeed(seed);
    }

    @Override
    public int nextInt() {
        return random.nextInt();
    }

    @Override
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    @Override
    public long nextLong() {
        return random.nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    @Override
    public float nextFloat() {
        return random.nextFloat();
    }

    @Override
    public double nextDouble() {
        return random.nextDouble();
    }

    @Override
    public double nextGaussian() {
        return random.nextGaussian();
    }

    public static final class Inverse extends Random {
        private final net.minecraft.util.math.random.Random random;

        public Inverse(net.minecraft.util.math.random.Random random) {
            this.random = random;
        }

        @Override
        public synchronized void setSeed(long seed) {
            if (random != null) {
                random.setSeed(seed);
            }
        }

        @Override
        public int nextInt() {
            return random.nextInt();
        }

        @Override
        public int nextInt(int bound) {
            return random.nextInt(bound);
        }

        @Override
        public long nextLong() {
            return random.nextLong();
        }

        @Override
        public boolean nextBoolean() {
            return random.nextBoolean();
        }

        @Override
        public float nextFloat() {
            return random.nextFloat();
        }

        @Override
        public double nextDouble() {
            return random.nextDouble();
        }

        @Override
        public synchronized double nextGaussian() {
            return random.nextGaussian();
        }

        @Override
        public int nextInt(int origin, int bound) {
            return random.nextBetweenExclusive(origin, bound);
        }
    }
}
