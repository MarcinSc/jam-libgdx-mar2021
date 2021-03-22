package com.gempukku.jam.libgdx.march2021.desktop;


import com.gempukku.jam.libgdx.march2021.ProfilerInfoProvider;

public class DesktopProfilerInfoProvider implements ProfilerInfoProvider {
    @Override
    public long getNanoTime() {
        return System.nanoTime();
    }

    @Override
    public long getUsedMemory() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
    }
}
