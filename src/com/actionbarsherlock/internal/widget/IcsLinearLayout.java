package com.actionbarsherlock.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import com.actionbarsherlock.internal.nineoldandroids.widget.NineLinearLayout;

public class IcsLinearLayout
  extends NineLinearLayout
{
  private static final int[] LinearLayout = { 16843049, 16843561, 16843562 };
  private static final int LinearLayout_divider = 0;
  private static final int LinearLayout_dividerPadding = 2;
  private static final int LinearLayout_showDividers = 1;
  public static final int SHOW_DIVIDER_BEGINNING = 1;
  public static final int SHOW_DIVIDER_END = 4;
  public static final int SHOW_DIVIDER_MIDDLE = 2;
  public static final int SHOW_DIVIDER_NONE;
  private Drawable mDivider;
  private int mDividerHeight;
  private int mDividerPadding;
  private int mDividerWidth;
  private int mShowDividers;
  
  public IcsLinearLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, LinearLayout);
    setDividerDrawable(localTypedArray.getDrawable(0));
    this.mShowDividers = localTypedArray.getInt(1, 0);
    this.mDividerPadding = localTypedArray.getDimensionPixelSize(2, 0);
    localTypedArray.recycle();
  }
  
  void drawDividersHorizontal(Canvas paramCanvas)
  {
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView2 = getChildAt(j);
      if ((localView2 != null) && (localView2.getVisibility() != 8) && (hasDividerBeforeChildAt(j)))
      {
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)localView2.getLayoutParams();
        drawVerticalDivider(paramCanvas, localView2.getLeft() - localLayoutParams.leftMargin);
      }
    }
    View localView1;
    if (hasDividerBeforeChildAt(i))
    {
      localView1 = getChildAt(i - 1);
      if (localView1 != null) {
        break label120;
      }
    }
    for (int k = getWidth() - getPaddingRight() - this.mDividerWidth;; k = localView1.getRight())
    {
      drawVerticalDivider(paramCanvas, k);
      return;
      label120:
      ((LinearLayout.LayoutParams)localView1.getLayoutParams());
    }
  }
  
  void drawDividersVertical(Canvas paramCanvas)
  {
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView2 = getChildAt(j);
      if ((localView2 != null) && (localView2.getVisibility() != 8) && (hasDividerBeforeChildAt(j)))
      {
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)localView2.getLayoutParams();
        drawHorizontalDivider(paramCanvas, localView2.getTop() - localLayoutParams.topMargin);
      }
    }
    View localView1;
    if (hasDividerBeforeChildAt(i))
    {
      localView1 = getChildAt(i - 1);
      if (localView1 != null) {
        break label120;
      }
    }
    for (int k = getHeight() - getPaddingBottom() - this.mDividerHeight;; k = localView1.getBottom())
    {
      drawHorizontalDivider(paramCanvas, k);
      return;
      label120:
      ((LinearLayout.LayoutParams)localView1.getLayoutParams());
    }
  }
  
  void drawHorizontalDivider(Canvas paramCanvas, int paramInt)
  {
    this.mDivider.setBounds(getPaddingLeft() + this.mDividerPadding, paramInt, getWidth() - getPaddingRight() - this.mDividerPadding, paramInt + this.mDividerHeight);
    this.mDivider.draw(paramCanvas);
  }
  
  void drawVerticalDivider(Canvas paramCanvas, int paramInt)
  {
    this.mDivider.setBounds(paramInt, getPaddingTop() + this.mDividerPadding, paramInt + this.mDividerWidth, getHeight() - getPaddingBottom() - this.mDividerPadding);
    this.mDivider.draw(paramCanvas);
  }
  
  public int getDividerPadding()
  {
    return this.mDividerPadding;
  }
  
  public int getDividerWidth()
  {
    return this.mDividerWidth;
  }
  
  public int getShowDividers()
  {
    return this.mShowDividers;
  }
  
  protected boolean hasDividerBeforeChildAt(int paramInt)
  {
    if (paramInt == 0) {
      if ((0x1 & this.mShowDividers) == 0) {}
    }
    do
    {
      return true;
      return false;
      if (paramInt != getChildCount()) {
        break;
      }
    } while ((0x4 & this.mShowDividers) != 0);
    return false;
    if ((0x2 & this.mShowDividers) != 0) {
      for (int i = paramInt - 1;; i--)
      {
        boolean bool = false;
        if (i >= 0)
        {
          if (getChildAt(i).getVisibility() != 8) {
            bool = true;
          }
        }
        else {
          return bool;
        }
      }
    }
    return false;
  }
  
  protected void measureChildWithMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = indexOfChild(paramView);
    int j = getOrientation();
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)paramView.getLayoutParams();
    if (hasDividerBeforeChildAt(i))
    {
      if (j == 1) {
        localLayoutParams.topMargin = this.mDividerHeight;
      }
    }
    else
    {
      int k = getChildCount();
      if ((i == k - 1) && (hasDividerBeforeChildAt(k)))
      {
        if (j != 1) {
          break label109;
        }
        localLayoutParams.bottomMargin = this.mDividerHeight;
      }
    }
    for (;;)
    {
      super.measureChildWithMargins(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
      localLayoutParams.leftMargin = this.mDividerWidth;
      break;
      label109:
      localLayoutParams.rightMargin = this.mDividerWidth;
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.mDivider != null)
    {
      if (getOrientation() != 1) {
        break label26;
      }
      drawDividersVertical(paramCanvas);
    }
    for (;;)
    {
      super.onDraw(paramCanvas);
      return;
      label26:
      drawDividersHorizontal(paramCanvas);
    }
  }
  
  public void setDividerDrawable(Drawable paramDrawable)
  {
    if (paramDrawable == this.mDivider) {
      return;
    }
    this.mDivider = paramDrawable;
    if (paramDrawable != null) {
      this.mDividerWidth = paramDrawable.getIntrinsicWidth();
    }
    for (this.mDividerHeight = paramDrawable.getIntrinsicHeight();; this.mDividerHeight = 0)
    {
      boolean bool = false;
      if (paramDrawable == null) {
        bool = true;
      }
      setWillNotDraw(bool);
      requestLayout();
      return;
      this.mDividerWidth = 0;
    }
  }
  
  public void setDividerPadding(int paramInt)
  {
    this.mDividerPadding = paramInt;
  }
  
  public void setShowDividers(int paramInt)
  {
    if (paramInt != this.mShowDividers)
    {
      requestLayout();
      invalidate();
    }
    this.mShowDividers = paramInt;
  }
}


/* Location:
 * Qualified Name:     com.actionbarsherlock.internal.widget.IcsLinearLayout
 * JD-Core Version:    0.7.0.1
 */