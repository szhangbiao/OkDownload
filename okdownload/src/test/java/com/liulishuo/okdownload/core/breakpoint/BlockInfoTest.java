package com.liulishuo.okdownload.core.breakpoint;

import org.junit.Before;
import org.junit.Test;

import static com.liulishuo.okdownload.core.Util.CHUNKED_CONTENT_LENGTH;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class BlockInfoTest {

    private BlockInfo info;

    @Before
    public void setup() {
        info = new BlockInfo(0, 1000);
    }

    @Test
    public void increase() {
        info.increaseCurrentOffset(123);
        assertThat(info.getCurrentOffset()).isEqualTo(123);
    }

    @Test
    public void copyNotClone() {
        info.increaseCurrentOffset(1);
        final BlockInfo copy = info.copy();
        copy.increaseCurrentOffset(1);

        assertThat(info.getCurrentOffset()).isEqualTo(1);
        assertThat(copy.getCurrentOffset()).isEqualTo(2);
    }

    @Test
    public void getRangeRight() {
        BlockInfo info = new BlockInfo(0, 3, 1);
        assertThat(info.getRangeRight()).isEqualTo(2);

        info = new BlockInfo(12, 6, 2);
        assertThat(info.getRangeRight()).isEqualTo(17);
    }

    @Test
    public void chunked() {
        BlockInfo info = new BlockInfo(0, CHUNKED_CONTENT_LENGTH);
        assertThat(info.getContentLength()).isEqualTo(CHUNKED_CONTENT_LENGTH);
    }
}