package com.example.realmonini.network.dto;

import java.util.List;

public class JournalListData {
    private List<JournalItem> journals;
    private Long nextCursor;
    private boolean hasNext;

    public List<JournalItem> getJournals() { return journals; }
    public Long getNextCursor() { return nextCursor; }
    public boolean isHasNext() { return hasNext; }
}
