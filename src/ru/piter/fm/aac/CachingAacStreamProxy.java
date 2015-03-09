/**
 * 
 */
package ru.piter.fm.aac;

import java.io.IOException;

/**
 * @author Ilya Basin
 *
 * TODO.
 */
public class CachingAacStreamProxy {

    private FixHeaderProxy srv;
    
    public CachingAacStreamProxy() {
        try {
            srv = new FixHeaderProxy();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getStreamUrl(String stationId, long timestamp) throws Exception {
        return srv.getUrl() + "/" + stationId + "/" + timestamp;
    }

    
    public void aaa() throws Exception {
    }

}
