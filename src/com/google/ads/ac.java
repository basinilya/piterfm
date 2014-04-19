package com.google.ads;

import com.google.ads.internal.d;
import com.google.ads.util.b;
import java.lang.ref.WeakReference;

public class ac
  implements Runnable
{
  private WeakReference<d> a;
  
  public ac(d paramd)
  {
    this.a = new WeakReference(paramd);
  }
  
  public void run()
  {
    d locald = (d)this.a.get();
    if (locald == null)
    {
      b.a("The ad must be gone, so cancelling the refresh timer.");
      return;
    }
    locald.y();
  }
}


/* Location:
 * Qualified Name:     com.google.ads.ac
 * JD-Core Version:    0.7.0.1
 */