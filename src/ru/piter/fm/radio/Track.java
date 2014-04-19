package ru.piter.fm.radio;

import ru.piter.fm.util.SearchFilter.Filterable;

public class Track
  implements SearchFilter.Filterable
{
  public static final int TYPE_SHOW = 2;
  public static final int TYPE_TRACK = 1;
  private String artistName;
  private String cover;
  private String duration;
  private String playCount;
  private long startAt;
  private String time;
  private String timestamp;
  private String trackId;
  private String trackName;
  private int type;
  
  public Track() {}
  
  public Track(int paramInt)
  {
    this.type = paramInt;
  }
  
  public String getArtistName()
  {
    return this.artistName;
  }
  
  public String getCover()
  {
    return this.cover;
  }
  
  public String getDuration()
  {
    return this.duration;
  }
  
  public String getPlayCount()
  {
    return this.playCount;
  }
  
  public long getStartAt()
  {
    return this.startAt;
  }
  
  public String getTime()
  {
    return this.time;
  }
  
  public String getTimestamp()
  {
    return this.timestamp;
  }
  
  public String getTrackId()
  {
    return this.trackId;
  }
  
  public String getTrackName()
  {
    return this.trackName;
  }
  
  public int getType()
  {
    return this.type;
  }
  
  public void setArtistName(String paramString)
  {
    this.artistName = paramString;
  }
  
  public void setCover(String paramString)
  {
    this.cover = paramString;
  }
  
  public void setDuration(String paramString)
  {
    this.duration = paramString;
  }
  
  public void setPlayCount(String paramString)
  {
    this.playCount = paramString;
  }
  
  public void setStartAt(long paramLong)
  {
    this.startAt = paramLong;
  }
  
  public void setTime(String paramString)
  {
    this.time = paramString;
  }
  
  public void setTimestamp(String paramString)
  {
    this.timestamp = paramString;
  }
  
  public void setTrackId(String paramString)
  {
    this.trackId = paramString;
  }
  
  public void setTrackName(String paramString)
  {
    this.trackName = paramString;
  }
  
  public void setType(int paramInt)
  {
    this.type = paramInt;
  }
  
  public String toFilterString()
  {
    return this.artistName.toLowerCase() + this.trackName.toLowerCase();
  }
  
  public String toString()
  {
    return "Track{artistName='" + this.artistName + '\'' + ", trackName='" + this.trackName + '\'' + ", type=" + this.type + '}';
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.radio.Track
 * JD-Core Version:    0.7.0.1
 */