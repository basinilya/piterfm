package com.actionbarsherlock.internal.view.menu;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout.LayoutParams;
import com.actionbarsherlock.internal.widget.IcsLinearLayout;

public class ActionMenuView
  extends IcsLinearLayout
  implements MenuBuilder.ItemInvoker, MenuView
{
  static final int GENERATED_ITEM_PADDING = 4;
  private static final boolean IS_FROYO = false;
  static final int MIN_CELL_SIZE = 56;
  private boolean mFirst = true;
  private boolean mFormatItems;
  private int mFormatItemsWidth;
  private int mGeneratedItemPadding;
  private MenuBuilder mMenu;
  private int mMinCellSize;
  private ActionMenuPresenter mPresenter;
  private boolean mReserveOverflow;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 8) {}
    for (boolean bool = true;; bool = false)
    {
      IS_FROYO = bool;
      return;
    }
  }
  
  public ActionMenuView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ActionMenuView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setBaselineAligned(false);
    float f = paramContext.getResources().getDisplayMetrics().density;
    this.mMinCellSize = ((int)(56.0F * f));
    this.mGeneratedItemPadding = ((int)(4.0F * f));
  }
  
  static int measureChildForCells(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt3) - paramInt4, View.MeasureSpec.getMode(paramInt3));
    int j = 0;
    if (paramInt2 > 0)
    {
      paramView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1 * paramInt2, -2147483648), i);
      int k = paramView.getMeasuredWidth();
      j = k / paramInt1;
      if (k % paramInt1 != 0) {
        j++;
      }
    }
    ActionMenuItemView localActionMenuItemView;
    if ((paramView instanceof ActionMenuItemView))
    {
      localActionMenuItemView = (ActionMenuItemView)paramView;
      if ((localLayoutParams.isOverflowButton) || (localActionMenuItemView == null) || (!localActionMenuItemView.hasText())) {
        break label143;
      }
    }
    label143:
    for (boolean bool = true;; bool = false)
    {
      localLayoutParams.expandable = bool;
      localLayoutParams.cellsUsed = j;
      paramView.measure(View.MeasureSpec.makeMeasureSpec(j * paramInt1, 1073741824), i);
      return j;
      localActionMenuItemView = null;
      break;
    }
  }
  
  private void onMeasureExactFormat(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getMode(paramInt2);
    int j = View.MeasureSpec.getSize(paramInt1);
    int k = View.MeasureSpec.getSize(paramInt2);
    int m = getPaddingLeft() + getPaddingRight();
    int n = getPaddingTop() + getPaddingBottom();
    int i1 = j - m;
    int i2 = i1 / this.mMinCellSize;
    int i3 = i1 % this.mMinCellSize;
    if (i2 == 0)
    {
      setMeasuredDimension(i1, 0);
      return;
    }
    int i4 = this.mMinCellSize + i3 / i2;
    int i5 = i2;
    int i6 = 0;
    int i7 = 0;
    int i8 = 0;
    int i9 = 0;
    int i10 = 0;
    long l1 = 0L;
    int i11 = getChildCount();
    int i12 = 0;
    while (i12 < i11)
    {
      View localView4 = getChildAt(i12);
      if (localView4.getVisibility() == 8)
      {
        i12++;
      }
      else
      {
        boolean bool1 = localView4 instanceof ActionMenuItemView;
        i9++;
        if (bool1) {
          localView4.setPadding(this.mGeneratedItemPadding, 0, this.mGeneratedItemPadding, 0);
        }
        LayoutParams localLayoutParams5 = (LayoutParams)localView4.getLayoutParams();
        localLayoutParams5.expanded = false;
        localLayoutParams5.extraPixels = 0;
        localLayoutParams5.cellsUsed = 0;
        localLayoutParams5.expandable = false;
        localLayoutParams5.leftMargin = 0;
        localLayoutParams5.rightMargin = 0;
        boolean bool2;
        if ((bool1) && (((ActionMenuItemView)localView4).hasText()))
        {
          bool2 = true;
          label246:
          localLayoutParams5.preventEdgeOffset = bool2;
          if (!localLayoutParams5.isOverflowButton) {
            break label357;
          }
        }
        label357:
        for (int i26 = 1;; i26 = i5)
        {
          int i27 = measureChildForCells(localView4, i4, i26, paramInt2, n);
          i7 = Math.max(i7, i27);
          if (localLayoutParams5.expandable) {
            i8++;
          }
          if (localLayoutParams5.isOverflowButton) {
            i10 = 1;
          }
          i5 -= i27;
          int i28 = localView4.getMeasuredHeight();
          i6 = Math.max(i6, i28);
          if (i27 != 1) {
            break;
          }
          l1 |= 1 << i12;
          break;
          bool2 = false;
          break label246;
        }
      }
    }
    int i13;
    int i14;
    int i21;
    long l2;
    int i22;
    int i23;
    label404:
    LayoutParams localLayoutParams4;
    if ((i10 != 0) && (i9 == 2))
    {
      i13 = 1;
      i14 = 0;
      if ((i8 <= 0) || (i5 <= 0)) {
        break label515;
      }
      i21 = 2147483647;
      l2 = 0L;
      i22 = 0;
      i23 = 0;
      if (i23 >= i11) {
        break label501;
      }
      localLayoutParams4 = (LayoutParams)getChildAt(i23).getLayoutParams();
      if (localLayoutParams4.expandable) {
        break label445;
      }
    }
    for (;;)
    {
      i23++;
      break label404;
      i13 = 0;
      break;
      label445:
      if (localLayoutParams4.cellsUsed < i21)
      {
        i21 = localLayoutParams4.cellsUsed;
        l2 = 1 << i23;
        i22 = 1;
      }
      else if (localLayoutParams4.cellsUsed == i21)
      {
        l2 |= 1 << i23;
        i22++;
      }
    }
    label501:
    l1 |= l2;
    label515:
    int i15;
    label529:
    int i18;
    label668:
    int i19;
    if (i22 > i5)
    {
      if ((i10 != 0) || (i9 != 1)) {
        break label837;
      }
      i15 = 1;
      if ((i5 <= 0) || (l1 == 0L) || ((i5 >= i9 - 1) && (i15 == 0) && (i7 <= 1))) {
        break label993;
      }
      float f = Long.bitCount(l1);
      if (i15 == 0)
      {
        if (((1L & l1) != 0L) && (!((LayoutParams)getChildAt(0).getLayoutParams()).preventEdgeOffset)) {
          f -= 0.5F;
        }
        if (((l1 & 1 << i11 - 1) != 0L) && (!((LayoutParams)getChildAt(i11 - 1).getLayoutParams()).preventEdgeOffset)) {
          f -= 0.5F;
        }
      }
      if (f <= 0.0F) {
        break label843;
      }
      i18 = (int)(i5 * i4 / f);
      i19 = 0;
      label671:
      if (i19 >= i11) {
        break label993;
      }
      if ((l1 & 1 << i19) != 0L) {
        break label849;
      }
    }
    for (;;)
    {
      i19++;
      break label671;
      int i24 = i21 + 1;
      int i25 = 0;
      if (i25 < i11)
      {
        View localView3 = getChildAt(i25);
        LayoutParams localLayoutParams3 = (LayoutParams)localView3.getLayoutParams();
        if ((l2 & 1 << i25) == 0L) {
          if (localLayoutParams3.cellsUsed == i24) {
            l1 |= 1 << i25;
          }
        }
        for (;;)
        {
          i25++;
          break;
          if ((i13 != 0) && (localLayoutParams3.preventEdgeOffset) && (i5 == 1)) {
            localView3.setPadding(i4 + this.mGeneratedItemPadding, 0, this.mGeneratedItemPadding, 0);
          }
          localLayoutParams3.cellsUsed = (1 + localLayoutParams3.cellsUsed);
          localLayoutParams3.expanded = true;
          i5--;
        }
      }
      i14 = 1;
      break;
      label837:
      i15 = 0;
      break label529;
      label843:
      i18 = 0;
      break label668;
      label849:
      View localView2 = getChildAt(i19);
      LayoutParams localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
      if ((localView2 instanceof ActionMenuItemView))
      {
        localLayoutParams2.extraPixels = i18;
        localLayoutParams2.expanded = true;
        if ((i19 == 0) && (!localLayoutParams2.preventEdgeOffset)) {
          localLayoutParams2.leftMargin = (-i18 / 2);
        }
        i14 = 1;
      }
      else if (localLayoutParams2.isOverflowButton)
      {
        localLayoutParams2.extraPixels = i18;
        localLayoutParams2.expanded = true;
        localLayoutParams2.rightMargin = (-i18 / 2);
        i14 = 1;
      }
      else
      {
        if (i19 != 0) {
          localLayoutParams2.leftMargin = (i18 / 2);
        }
        int i20 = i11 - 1;
        if (i19 != i20) {
          localLayoutParams2.rightMargin = (i18 / 2);
        }
      }
    }
    label993:
    if (i14 != 0)
    {
      int i16 = View.MeasureSpec.makeMeasureSpec(k - n, i);
      int i17 = 0;
      if (i17 < i11)
      {
        View localView1 = getChildAt(i17);
        LayoutParams localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
        if (!localLayoutParams1.expanded) {}
        for (;;)
        {
          i17++;
          break;
          localView1.measure(View.MeasureSpec.makeMeasureSpec(i4 * localLayoutParams1.cellsUsed + localLayoutParams1.extraPixels, 1073741824), i16);
        }
      }
    }
    if (i != 1073741824) {
      k = i6;
    }
    setMeasuredDimension(i1, k);
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return (paramLayoutParams != null) && ((paramLayoutParams instanceof LayoutParams));
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    return false;
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    LayoutParams localLayoutParams = new LayoutParams(-2, -2);
    localLayoutParams.gravity = 16;
    return localLayoutParams;
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof LayoutParams))
    {
      LayoutParams localLayoutParams = new LayoutParams((LayoutParams)paramLayoutParams);
      if (localLayoutParams.gravity <= 0) {
        localLayoutParams.gravity = 16;
      }
      return localLayoutParams;
    }
    return generateDefaultLayoutParams();
  }
  
  public LayoutParams generateOverflowButtonLayoutParams()
  {
    LayoutParams localLayoutParams = generateDefaultLayoutParams();
    localLayoutParams.isOverflowButton = true;
    return localLayoutParams;
  }
  
  public int getWindowAnimations()
  {
    return 0;
  }
  
  protected boolean hasDividerBeforeChildAt(int paramInt)
  {
    View localView1 = getChildAt(paramInt - 1);
    View localView2 = getChildAt(paramInt);
    int i = getChildCount();
    boolean bool1 = false;
    if (paramInt < i)
    {
      boolean bool2 = localView1 instanceof ActionMenuChildView;
      bool1 = false;
      if (bool2) {
        bool1 = false | ((ActionMenuChildView)localView1).needsDividerAfter();
      }
    }
    if ((paramInt > 0) && ((localView2 instanceof ActionMenuChildView))) {
      bool1 |= ((ActionMenuChildView)localView2).needsDividerBefore();
    }
    return bool1;
  }
  
  public void initialize(MenuBuilder paramMenuBuilder)
  {
    this.mMenu = paramMenuBuilder;
  }
  
  public boolean invokeItem(MenuItemImpl paramMenuItemImpl)
  {
    return this.mMenu.performItemAction(paramMenuItemImpl, 0);
  }
  
  public boolean isExpandedFormat()
  {
    return this.mFormatItems;
  }
  
  public boolean isOverflowReserved()
  {
    return this.mReserveOverflow;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    if (IS_FROYO) {
      super.onConfigurationChanged(paramConfiguration);
    }
    this.mPresenter.updateMenuView(false);
    if ((this.mPresenter != null) && (this.mPresenter.isOverflowMenuShowing()))
    {
      this.mPresenter.hideOverflowMenu();
      this.mPresenter.showOverflowMenu();
    }
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.mPresenter.dismissPopupMenus();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if ((!IS_FROYO) && (this.mFirst))
    {
      this.mFirst = false;
      requestLayout();
      return;
    }
    super.onDraw(paramCanvas);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!this.mFormatItems)
    {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    int i = getChildCount();
    int j = (paramInt2 + paramInt4) / 2;
    int k = 0;
    int m = paramInt3 - paramInt1 - getPaddingRight() - getPaddingLeft();
    int n = 0;
    int i1 = 0;
    if (i1 < i)
    {
      View localView3 = getChildAt(i1);
      if (localView3.getVisibility() == 8) {}
      for (;;)
      {
        i1++;
        break;
        LayoutParams localLayoutParams2 = (LayoutParams)localView3.getLayoutParams();
        if (localLayoutParams2.isOverflowButton)
        {
          int i16 = localView3.getMeasuredWidth();
          if (hasDividerBeforeChildAt(i1)) {
            i16 += 0;
          }
          int i17 = localView3.getMeasuredHeight();
          int i18 = getWidth() - getPaddingRight() - localLayoutParams2.rightMargin;
          int i19 = i18 - i16;
          int i20 = j - i17 / 2;
          localView3.layout(i19, i20, i18, i20 + i17);
          m -= i16;
          n = 1;
        }
        else
        {
          m -= localView3.getMeasuredWidth() + localLayoutParams2.leftMargin + localLayoutParams2.rightMargin;
          k++;
        }
      }
    }
    if ((i == 1) && (n == 0))
    {
      View localView2 = getChildAt(0);
      int i12 = localView2.getMeasuredWidth();
      int i13 = localView2.getMeasuredHeight();
      int i14 = (paramInt3 - paramInt1) / 2 - i12 / 2;
      int i15 = j - i13 / 2;
      localView2.layout(i14, i15, i14 + i12, i15 + i13);
      return;
    }
    int i2;
    label305:
    int i4;
    label324:
    int i5;
    int i6;
    int i7;
    label341:
    View localView1;
    LayoutParams localLayoutParams1;
    if (n != 0)
    {
      i2 = 0;
      int i3 = k - i2;
      if (i3 <= 0) {
        break label396;
      }
      i4 = m / i3;
      i5 = Math.max(0, i4);
      i6 = getPaddingLeft();
      i7 = 0;
      if (i7 < i)
      {
        localView1 = getChildAt(i7);
        localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
        if ((localView1.getVisibility() != 8) && (!localLayoutParams1.isOverflowButton)) {
          break label402;
        }
      }
    }
    for (;;)
    {
      i7++;
      break label341;
      break;
      i2 = 1;
      break label305;
      label396:
      i4 = 0;
      break label324;
      label402:
      int i8 = i6 + localLayoutParams1.leftMargin;
      int i9 = localView1.getMeasuredWidth();
      int i10 = localView1.getMeasuredHeight();
      int i11 = j - i10 / 2;
      localView1.layout(i8, i11, i8 + i9, i11 + i10);
      i6 = i8 + (i5 + (i9 + localLayoutParams1.rightMargin));
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    boolean bool1 = this.mFormatItems;
    if (View.MeasureSpec.getMode(paramInt1) == 1073741824) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      this.mFormatItems = bool2;
      if (bool1 != this.mFormatItems) {
        this.mFormatItemsWidth = 0;
      }
      int i = View.MeasureSpec.getMode(paramInt1);
      if ((this.mFormatItems) && (this.mMenu != null) && (i != this.mFormatItemsWidth))
      {
        this.mFormatItemsWidth = i;
        this.mMenu.onItemsChanged(true);
      }
      if (!this.mFormatItems) {
        break;
      }
      onMeasureExactFormat(paramInt1, paramInt2);
      return;
    }
    super.onMeasure(paramInt1, paramInt2);
  }
  
  public void setOverflowReserved(boolean paramBoolean)
  {
    this.mReserveOverflow = paramBoolean;
  }
  
  public void setPresenter(ActionMenuPresenter paramActionMenuPresenter)
  {
    this.mPresenter = paramActionMenuPresenter;
  }
  
  public static abstract interface ActionMenuChildView
  {
    public abstract boolean needsDividerAfter();
    
    public abstract boolean needsDividerBefore();
  }
  
  public static class LayoutParams
    extends LinearLayout.LayoutParams
  {
    public int cellsUsed;
    public boolean expandable;
    public boolean expanded;
    public int extraPixels;
    public boolean isOverflowButton;
    public boolean preventEdgeOffset;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      this.isOverflowButton = false;
    }
    
    public LayoutParams(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      super(paramInt2);
      this.isOverflowButton = paramBoolean;
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.isOverflowButton = paramLayoutParams.isOverflowButton;
    }
  }
}


/* Location:
 * Qualified Name:     com.actionbarsherlock.internal.view.menu.ActionMenuView
 * JD-Core Version:    0.7.0.1
 */