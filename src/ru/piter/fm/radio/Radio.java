package ru.piter.fm.radio;

import java.io.Serializable;
import java.util.List;

public class Radio
  implements Serializable
{
  private List<Channel> channels;
  private String hostUrl;
  private String name;
  private String stationsUrl;
  
  public Radio(String paramString1, String paramString2)
  {
    this.name = paramString1;
    this.hostUrl = paramString2;
    this.stationsUrl = (paramString2 + "/stations");
  }
  
  public List<Channel> getChannels()
  {
    return this.channels;
  }
  
  public String getHostUrl()
  {
    return this.hostUrl;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getStationsUrl()
  {
    return this.stationsUrl;
  }
  
  public void setChannels(List<Channel> paramList)
  {
    this.channels = paramList;
  }
  
  public void setHostUrl(String paramString)
  {
    this.hostUrl = paramString;
  }
  
  public void setName(String paramString)
  {
    this.name = paramString;
  }
  
  public void setStationsUrl(String paramString)
  {
    this.stationsUrl = paramString;
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.radio.Radio
 * JD-Core Version:    0.7.0.1
 */