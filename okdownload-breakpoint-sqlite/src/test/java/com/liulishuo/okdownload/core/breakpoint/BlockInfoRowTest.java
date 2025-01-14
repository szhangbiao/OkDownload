package com.liulishuo.okdownload.core.breakpoint;

import android.database.Cursor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.liulishuo.okdownload.core.breakpoint.BreakpointSQLiteKey.CONTENT_LENGTH;
import static com.liulishuo.okdownload.core.breakpoint.BreakpointSQLiteKey.CURRENT_OFFSET;
import static com.liulishuo.okdownload.core.breakpoint.BreakpointSQLiteKey.HOST_ID;
import static com.liulishuo.okdownload.core.breakpoint.BreakpointSQLiteKey.START_OFFSET;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.robolectric.annotation.Config.NONE;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class BlockInfoRowTest {

    private BlockInfoRow blockInfoRow;

    @Before
    public void setup() {
        final Cursor cursor = mock(Cursor.class);
        doReturn(0).when(cursor).getColumnIndex(HOST_ID);
        doReturn(10).when(cursor).getInt(0);

        doReturn(1).when(cursor).getColumnIndex(START_OFFSET);
        doReturn(20).when(cursor).getInt(1);

        doReturn(2).when(cursor).getColumnIndex(CONTENT_LENGTH);
        doReturn(30).when(cursor).getInt(2);

        doReturn(3).when(cursor).getColumnIndex(CURRENT_OFFSET);
        doReturn(5).when(cursor).getInt(3);

        blockInfoRow = new BlockInfoRow(cursor);
    }

    @Test
    public void construction() {
        assertThat(blockInfoRow.getBreakpointId()).isEqualTo(10);
        assertThat(blockInfoRow.getStartOffset()).isEqualTo(20);
        assertThat(blockInfoRow.getContentLength()).isEqualTo(30);
        assertThat(blockInfoRow.getCurrentOffset()).isEqualTo(5);
    }

    @Test
    public void toInfo() {
        final BlockInfo info = blockInfoRow.toInfo();
        assertThat(info.getStartOffset()).isEqualTo(20);
        assertThat(info.getContentLength()).isEqualTo(30);
        assertThat(info.getCurrentOffset()).isEqualTo(5);
    }
}