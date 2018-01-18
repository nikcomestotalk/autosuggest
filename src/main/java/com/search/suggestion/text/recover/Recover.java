package com.search.suggestion.text.recover;

import java.io.IOException;

/**
 * Created by nikhil on 19/1/18.
 */
public interface Recover {
    public void updateIndexer() throws IOException, InterruptedException;
    public void swap();
}
