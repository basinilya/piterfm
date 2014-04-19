package ru.piter.fm.radio;

public class RadioFactory
{
  public static final String FAVOURITE = "Favourite";
  public static final String MOSKVA_FM = "MoskvaFM";
  public static final String PITER_FM = "PiterFM";
  
  public static Radio getRadio(String paramString)
  {
    if (paramString.equals("PiterFM")) {
      return new Radio("PiterFM", "http://piter.fm");
    }
    if (paramString.equals("MoskvaFM")) {
      return new Radio("MoskvaFM", "http://moskva.fm");
    }
    if (paramString.equals("Favourite")) {
      return new Radio("Favourite", "");
    }
    return null;
  }
}


/* Location:
 * Qualified Name:     ru.piter.fm.radio.RadioFactory
 * JD-Core Version:    0.7.0.1
 */