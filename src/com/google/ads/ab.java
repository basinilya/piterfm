package com.google.ads;

import android.content.Context;
import com.google.ads.util.AdUtil;
import com.google.ads.util.b;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ab
  implements Runnable
{
  private Context a;
  private String b;
  
  public ab(String paramString, Context paramContext)
  {
    this.b = paramString;
    this.a = paramContext;
  }
  
  protected HttpURLConnection a(URL paramURL)
    throws IOException
  {
    return (HttpURLConnection)paramURL.openConnection();
  }
  
  public void run()
  {
    try
    {
      b.a("Pinging URL: " + this.b);
      HttpURLConnection localHttpURLConnection = a(new URL(this.b));
      try
      {
        AdUtil.a(localHttpURLConnection, this.a);
        localHttpURLConnection.setInstanceFollowRedirects(true);
        localHttpURLConnection.connect();
        int i = localHttpURLConnection.getResponseCode();
        if ((i < 200) || (i >= 300)) {
          b.e("Did not receive 2XX (got " + i + ") from pinging URL: " + this.b);
        }
        return;
      }
      finally
      {
        localHttpURLConnection.disconnect();
      }
      return;
    }
    catch (IOException localIOException)
    {
      b.d("Unable to ping the URL: " + this.b, localIOException);
    }
  }
}


/* Location:
 * Qualified Name:     com.google.ads.ab
 * JD-Core Version:    0.7.0.1
 */