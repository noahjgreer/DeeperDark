package net.minecraft.test;

import java.util.stream.Stream;

@FunctionalInterface
public interface TestInstanceFinder {
   Stream findTests();
}
