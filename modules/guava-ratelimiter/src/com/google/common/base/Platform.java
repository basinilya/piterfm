package com.google.common.base;

public class Platform {
  /** Calls {@link System#nanoTime()}. */
  static long systemNanoTime() {
    return System.nanoTime();
  }
}
