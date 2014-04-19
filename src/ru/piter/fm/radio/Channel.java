package ru.piter.fm.radio;

import java.io.Serializable;
import ru.piter.fm.util.SearchFilter.Filterable;

public class Channel
  implements Serializable, Comparable, SearchFilter.Filterable
{
  private String channelId;
  private String logoUrl;
  private String name;
  private Radio radio;
  private String range;
  private String translationUrl;
  
  public int compareTo(Object paramObject)
  {
    Channel localChannel = (Channel)paramObject;
    try
    {
      float f1 = Float.parseFloat(this.range.split("\\s")[0]);
      float f2 = Float.parseFloat(localChannel.getRange().split("\\s")[0]);
      int i;
      if (f1 < f2) {
        i = -1;
      }
      boolean bool;
      do
      {
        return i;
        bool = f1 < f2;
        i = 0;
      } while (!bool);
      return 1;
    }
    catch (NumberFormatException localNumberFormatException) {}
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    Channel localChannel;
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      localChannel = (Channel)paramObject;
      if (!this.channelId.equals(localChannel.channelId)) {
        return false;
      }
      if (!this.logoUrl.equals(localChannel.logoUrl)) {
        return false;
      }
      if (!this.name.equals(localChannel.name)) {
        return false;
      }
    } while (this.range.equals(localChannel.range));
    return false;
  }
  
  public String getChannelId()
  {
    return this.channelId;
  }
  
  public String getLogoUrl()
  {
    return this.logoUrl;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public Radio getRadio()
  {
    return this.radio;
  }
  
  public String getRange()
  {
    return this.range;
  }
  
  public String getTranslationUrl()
  {
    return this.translationUrl;
  }
  
  public int hashCode()
  {
    return 31 * (31 * this.channelId.hashCode() + this.name.hashCode()) + this.range.hashCode();
  }
  
  public void setChannelId(String paramString)
  {
    this.channelId = paramString;
  }
  
  public void setLogoUrl(String paramString)
  {
    this.logoUrl = paramString;
  }
  
  public void setName(String paramString)
  {
    this.name = paramString;
  }
  
  public void setRadio(Radio paramRadio)
  {
    this.radio = paramRadio;
  }
  
  public void setRange(String paramString)
  {
    this.range = paramString;
  }
  
  public void setTranslationUrl(String paramString)
  {
    this.translationUrl = paramString;
  }
  
  public String toFilterString()
  {
    return this.name.toLowerCase() + this.range.toLowerCase();
  }
  
  public String toString()
  {
    return "Channel{channelId='" + this.channelId + '\'' + ", translationUrl='" + this.translationUrl + '\'' + ", name='" + this.name + '\'' + ", range='" + this.range + '\'' + '}';
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.radio.Channel
 * JD-Core Version:    0.7.0.1
 */