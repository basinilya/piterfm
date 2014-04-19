package com.actionbarsherlock.internal.nineoldandroids.animation;

public class IntEvaluator
  implements TypeEvaluator<Integer>
{
  public Integer evaluate(float paramFloat, Integer paramInteger1, Integer paramInteger2)
  {
    int i = paramInteger1.intValue();
    return Integer.valueOf((int)(i + paramFloat * (paramInteger2.intValue() - i)));
  }
}


/* Location:
 * Qualified Name:     com.actionbarsherlock.internal.nineoldandroids.animation.IntEvaluator
 * JD-Core Version:    0.7.0.1
 */