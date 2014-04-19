package com.actionbarsherlock.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.ViewDebug.ExportedProperty;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.RemoteViews.RemoteView;

@RemoteViews.RemoteView
public class IcsProgressBar
  extends View
{
  private static final int ANIMATION_RESOLUTION = 200;
  private static final boolean IS_HONEYCOMB = false;
  private static final int MAX_LEVEL = 10000;
  private static final int[] ProgressBar;
  private static final int ProgressBar_animationResolution = 14;
  private static final int ProgressBar_indeterminate = 5;
  private static final int ProgressBar_indeterminateBehavior = 10;
  private static final int ProgressBar_indeterminateDrawable = 7;
  private static final int ProgressBar_indeterminateDuration = 9;
  private static final int ProgressBar_indeterminateOnly = 6;
  private static final int ProgressBar_interpolator = 13;
  private static final int ProgressBar_max = 2;
  private static final int ProgressBar_maxHeight = 1;
  private static final int ProgressBar_maxWidth = 0;
  private static final int ProgressBar_minHeight = 12;
  private static final int ProgressBar_minWidth = 11;
  private static final int ProgressBar_progress = 3;
  private static final int ProgressBar_progressDrawable = 8;
  private static final int ProgressBar_secondaryProgress = 4;
  private static final int TIMEOUT_SEND_ACCESSIBILITY_EVENT = 200;
  private AccessibilityEventSender mAccessibilityEventSender;
  private AccessibilityManager mAccessibilityManager;
  private AlphaAnimation mAnimation;
  private int mAnimationResolution;
  private int mBehavior;
  private Drawable mCurrentDrawable;
  private int mDuration;
  private boolean mInDrawing;
  private boolean mIndeterminate;
  private Drawable mIndeterminateDrawable;
  private int mIndeterminateRealLeft;
  private int mIndeterminateRealTop;
  private Interpolator mInterpolator;
  private long mLastDrawTime;
  private int mMax;
  int mMaxHeight;
  int mMaxWidth;
  int mMinHeight;
  int mMinWidth;
  private boolean mNoInvalidate;
  private boolean mOnlyIndeterminate;
  private int mProgress;
  private Drawable mProgressDrawable;
  private RefreshProgressRunnable mRefreshProgressRunnable;
  Bitmap mSampleTile;
  private int mSecondaryProgress;
  private boolean mShouldStartAnimationDrawable;
  private Transformation mTransformation;
  private long mUiThreadId = Thread.currentThread().getId();
  
  static
  {
    if (Build.VERSION.SDK_INT >= 11) {}
    for (boolean bool = true;; bool = false)
    {
      IS_HONEYCOMB = bool;
      ProgressBar = new int[] { 16843039, 16843040, 16843062, 16843063, 16843064, 16843065, 16843066, 16843067, 16843068, 16843069, 16843070, 16843071, 16843072, 16843073, 16843546 };
      return;
    }
  }
  
  public IcsProgressBar(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public IcsProgressBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842871);
  }
  
  public IcsProgressBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public IcsProgressBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1);
    initProgressBar();
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, ProgressBar, paramInt1, paramInt2);
    this.mNoInvalidate = true;
    Drawable localDrawable1 = localTypedArray.getDrawable(8);
    if (localDrawable1 != null) {
      setProgressDrawable(tileify(localDrawable1, false));
    }
    this.mDuration = localTypedArray.getInt(9, this.mDuration);
    this.mMinWidth = localTypedArray.getDimensionPixelSize(11, this.mMinWidth);
    this.mMaxWidth = localTypedArray.getDimensionPixelSize(0, this.mMaxWidth);
    this.mMinHeight = localTypedArray.getDimensionPixelSize(12, this.mMinHeight);
    this.mMaxHeight = localTypedArray.getDimensionPixelSize(1, this.mMaxHeight);
    this.mBehavior = localTypedArray.getInt(10, this.mBehavior);
    int i = localTypedArray.getResourceId(13, 17432587);
    if (i > 0) {
      setInterpolator(paramContext, i);
    }
    setMax(localTypedArray.getInt(2, this.mMax));
    setProgress(localTypedArray.getInt(3, this.mProgress));
    setSecondaryProgress(localTypedArray.getInt(4, this.mSecondaryProgress));
    Drawable localDrawable2 = localTypedArray.getDrawable(7);
    if (localDrawable2 != null) {
      setIndeterminateDrawable(tileifyIndeterminate(localDrawable2));
    }
    this.mOnlyIndeterminate = localTypedArray.getBoolean(6, this.mOnlyIndeterminate);
    this.mNoInvalidate = false;
    boolean bool1;
    if (!this.mOnlyIndeterminate)
    {
      boolean bool2 = localTypedArray.getBoolean(5, this.mIndeterminate);
      bool1 = false;
      if (!bool2) {}
    }
    else
    {
      bool1 = true;
    }
    setIndeterminate(bool1);
    this.mAnimationResolution = localTypedArray.getInteger(14, 200);
    localTypedArray.recycle();
    this.mAccessibilityManager = ((AccessibilityManager)paramContext.getSystemService("accessibility"));
  }
  
  private void doRefreshProgress(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    for (;;)
    {
      Drawable localDrawable2;
      try
      {
        float f;
        Drawable localDrawable1;
        if (this.mMax > 0)
        {
          f = paramInt2 / this.mMax;
          localDrawable1 = this.mCurrentDrawable;
          if (localDrawable1 != null)
          {
            boolean bool = localDrawable1 instanceof LayerDrawable;
            localDrawable2 = null;
            if (!bool) {
              break label116;
            }
            localDrawable2 = ((LayerDrawable)localDrawable1).findDrawableByLayerId(paramInt1);
            break label116;
            localDrawable2.setLevel(i);
            if ((paramBoolean2) && (paramInt1 == 16908301)) {
              onProgressRefresh(f, paramBoolean1);
            }
          }
        }
        else
        {
          f = 0.0F;
          continue;
          localDrawable2 = localDrawable1;
          continue;
        }
        invalidate();
        continue;
        int i = (int)(10000.0F * f);
      }
      finally {}
      label116:
      if (localDrawable2 == null) {}
    }
  }
  
  private void initProgressBar()
  {
    this.mMax = 100;
    this.mProgress = 0;
    this.mSecondaryProgress = 0;
    this.mIndeterminate = false;
    this.mOnlyIndeterminate = false;
    this.mDuration = 4000;
    this.mBehavior = 1;
    this.mMinWidth = 24;
    this.mMaxWidth = 48;
    this.mMinHeight = 24;
    this.mMaxHeight = 48;
  }
  
  private void refreshProgress(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        if (this.mUiThreadId == Thread.currentThread().getId())
        {
          doRefreshProgress(paramInt1, paramInt2, paramBoolean, true);
          return;
        }
        RefreshProgressRunnable localRefreshProgressRunnable;
        if (this.mRefreshProgressRunnable != null)
        {
          localRefreshProgressRunnable = this.mRefreshProgressRunnable;
          this.mRefreshProgressRunnable = null;
          localRefreshProgressRunnable.setup(paramInt1, paramInt2, paramBoolean);
          post(localRefreshProgressRunnable);
        }
        else
        {
          localRefreshProgressRunnable = new RefreshProgressRunnable(paramInt1, paramInt2, paramBoolean);
        }
      }
      finally {}
    }
  }
  
  private void scheduleAccessibilityEventSender()
  {
    if (this.mAccessibilityEventSender == null) {
      this.mAccessibilityEventSender = new AccessibilityEventSender(null);
    }
    for (;;)
    {
      postDelayed(this.mAccessibilityEventSender, 200L);
      return;
      removeCallbacks(this.mAccessibilityEventSender);
    }
  }
  
  private Drawable tileify(Drawable paramDrawable, boolean paramBoolean)
  {
    Object localObject2;
    if ((paramDrawable instanceof LayerDrawable))
    {
      LayerDrawable localLayerDrawable = (LayerDrawable)paramDrawable;
      int i = localLayerDrawable.getNumberOfLayers();
      Drawable[] arrayOfDrawable = new Drawable[i];
      int j = 0;
      if (j < i)
      {
        int m = localLayerDrawable.getId(j);
        Drawable localDrawable = localLayerDrawable.getDrawable(j);
        if ((m == 16908301) || (m == 16908303)) {}
        for (boolean bool = true;; bool = false)
        {
          arrayOfDrawable[j] = tileify(localDrawable, bool);
          j++;
          break;
        }
      }
      localObject2 = new LayerDrawable(arrayOfDrawable);
      for (int k = 0; k < i; k++) {
        ((LayerDrawable)localObject2).setId(k, localLayerDrawable.getId(k));
      }
    }
    if ((paramDrawable instanceof BitmapDrawable))
    {
      Bitmap localBitmap = ((BitmapDrawable)paramDrawable).getBitmap();
      if (this.mSampleTile == null) {
        this.mSampleTile = localBitmap;
      }
      Object localObject1 = new ShapeDrawable(getDrawableShape());
      BitmapShader localBitmapShader = new BitmapShader(localBitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
      ((ShapeDrawable)localObject1).getPaint().setShader(localBitmapShader);
      if (paramBoolean) {
        localObject1 = new ClipDrawable((Drawable)localObject1, 3, 1);
      }
      localObject2 = localObject1;
      return localObject2;
    }
    return paramDrawable;
  }
  
  private Drawable tileifyIndeterminate(Drawable paramDrawable)
  {
    if ((paramDrawable instanceof AnimationDrawable))
    {
      AnimationDrawable localAnimationDrawable1 = (AnimationDrawable)paramDrawable;
      int i = localAnimationDrawable1.getNumberOfFrames();
      AnimationDrawable localAnimationDrawable2 = new AnimationDrawable();
      localAnimationDrawable2.setOneShot(localAnimationDrawable1.isOneShot());
      for (int j = 0; j < i; j++)
      {
        Drawable localDrawable = tileify(localAnimationDrawable1.getFrame(j), true);
        localDrawable.setLevel(10000);
        localAnimationDrawable2.addFrame(localDrawable, localAnimationDrawable1.getDuration(j));
      }
      localAnimationDrawable2.setLevel(10000);
      paramDrawable = localAnimationDrawable2;
    }
    return paramDrawable;
  }
  
  private void updateDrawableBounds(int paramInt1, int paramInt2)
  {
    int i = paramInt1 - getPaddingRight() - getPaddingLeft();
    int j = paramInt2 - getPaddingBottom() - getPaddingTop();
    int k;
    int m;
    float f1;
    if (this.mIndeterminateDrawable != null)
    {
      boolean bool1 = this.mOnlyIndeterminate;
      k = 0;
      m = 0;
      if (bool1)
      {
        boolean bool2 = this.mIndeterminateDrawable instanceof AnimationDrawable;
        k = 0;
        m = 0;
        if (!bool2)
        {
          int n = this.mIndeterminateDrawable.getIntrinsicWidth();
          int i1 = this.mIndeterminateDrawable.getIntrinsicHeight();
          f1 = n / i1;
          float f2 = paramInt1 / paramInt2;
          boolean bool3 = f1 < f2;
          k = 0;
          m = 0;
          if (bool3)
          {
            if (f2 <= f1) {
              break label201;
            }
            int i3 = (int)(f1 * paramInt2);
            k = (paramInt1 - i3) / 2;
            i = k + i3;
          }
        }
      }
    }
    for (;;)
    {
      this.mIndeterminateDrawable.setBounds(0, 0, i - k, j - m);
      this.mIndeterminateRealLeft = k;
      this.mIndeterminateRealTop = m;
      if (this.mProgressDrawable != null) {
        this.mProgressDrawable.setBounds(0, 0, i, j);
      }
      return;
      label201:
      int i2 = (int)(paramInt1 * (1.0F / f1));
      m = (paramInt2 - i2) / 2;
      j = m + i2;
      k = 0;
    }
  }
  
  private void updateDrawableState()
  {
    int[] arrayOfInt = getDrawableState();
    if ((this.mProgressDrawable != null) && (this.mProgressDrawable.isStateful())) {
      this.mProgressDrawable.setState(arrayOfInt);
    }
    if ((this.mIndeterminateDrawable != null) && (this.mIndeterminateDrawable.isStateful())) {
      this.mIndeterminateDrawable.setState(arrayOfInt);
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    updateDrawableState();
  }
  
  Drawable getCurrentDrawable()
  {
    return this.mCurrentDrawable;
  }
  
  Shape getDrawableShape()
  {
    return new RoundRectShape(new float[] { 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F }, null, null);
  }
  
  public Drawable getIndeterminateDrawable()
  {
    return this.mIndeterminateDrawable;
  }
  
  public Interpolator getInterpolator()
  {
    return this.mInterpolator;
  }
  
  @ViewDebug.ExportedProperty(category="progress")
  public int getMax()
  {
    try
    {
      int i = this.mMax;
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  /* Error */
  @ViewDebug.ExportedProperty(category="progress")
  public int getProgress()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 217	com/actionbarsherlock/internal/widget/IcsProgressBar:mIndeterminate	Z
    //   6: istore_2
    //   7: iload_2
    //   8: ifeq +9 -> 17
    //   11: iconst_0
    //   12: istore_3
    //   13: aload_0
    //   14: monitorexit
    //   15: iload_3
    //   16: ireturn
    //   17: aload_0
    //   18: getfield 194	com/actionbarsherlock/internal/widget/IcsProgressBar:mProgress	I
    //   21: istore_3
    //   22: goto -9 -> 13
    //   25: astore_1
    //   26: aload_0
    //   27: monitorexit
    //   28: aload_1
    //   29: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	30	0	this	IcsProgressBar
    //   25	4	1	localObject	Object
    //   6	2	2	bool	boolean
    //   12	10	3	i	int
    // Exception table:
    //   from	to	target	type
    //   2	7	25	finally
    //   17	22	25	finally
  }
  
  public Drawable getProgressDrawable()
  {
    return this.mProgressDrawable;
  }
  
  /* Error */
  @ViewDebug.ExportedProperty(category="progress")
  public int getSecondaryProgress()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 217	com/actionbarsherlock/internal/widget/IcsProgressBar:mIndeterminate	Z
    //   6: istore_2
    //   7: iload_2
    //   8: ifeq +9 -> 17
    //   11: iconst_0
    //   12: istore_3
    //   13: aload_0
    //   14: monitorexit
    //   15: iload_3
    //   16: ireturn
    //   17: aload_0
    //   18: getfield 199	com/actionbarsherlock/internal/widget/IcsProgressBar:mSecondaryProgress	I
    //   21: istore_3
    //   22: goto -9 -> 13
    //   25: astore_1
    //   26: aload_0
    //   27: monitorexit
    //   28: aload_1
    //   29: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	30	0	this	IcsProgressBar
    //   25	4	1	localObject	Object
    //   6	2	2	bool	boolean
    //   12	10	3	i	int
    // Exception table:
    //   from	to	target	type
    //   2	7	25	finally
    //   17	22	25	finally
  }
  
  public final void incrementProgressBy(int paramInt)
  {
    try
    {
      setProgress(paramInt + this.mProgress);
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public final void incrementSecondaryProgressBy(int paramInt)
  {
    try
    {
      setSecondaryProgress(paramInt + this.mSecondaryProgress);
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    if (!this.mInDrawing)
    {
      if (verifyDrawable(paramDrawable))
      {
        Rect localRect = paramDrawable.getBounds();
        int i = getScrollX() + getPaddingLeft();
        int j = getScrollY() + getPaddingTop();
        invalidate(i + localRect.left, j + localRect.top, i + localRect.right, j + localRect.bottom);
      }
    }
    else {
      return;
    }
    super.invalidateDrawable(paramDrawable);
  }
  
  @ViewDebug.ExportedProperty(category="progress")
  public boolean isIndeterminate()
  {
    try
    {
      boolean bool = this.mIndeterminate;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    if (this.mProgressDrawable != null) {
      this.mProgressDrawable.jumpToCurrentState();
    }
    if (this.mIndeterminateDrawable != null) {
      this.mIndeterminateDrawable.jumpToCurrentState();
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.mIndeterminate) {
      startAnimation();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    if (this.mIndeterminate) {
      stopAnimation();
    }
    if (this.mRefreshProgressRunnable != null) {
      removeCallbacks(this.mRefreshProgressRunnable);
    }
    if (this.mAccessibilityEventSender != null) {
      removeCallbacks(this.mAccessibilityEventSender);
    }
    super.onDetachedFromWindow();
  }
  
  /* Error */
  protected void onDraw(android.graphics.Canvas paramCanvas)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: invokespecial 517	android/view/View:onDraw	(Landroid/graphics/Canvas;)V
    //   7: aload_0
    //   8: getfield 251	com/actionbarsherlock/internal/widget/IcsProgressBar:mCurrentDrawable	Landroid/graphics/drawable/Drawable;
    //   11: astore_3
    //   12: aload_3
    //   13: ifnull +160 -> 173
    //   16: aload_1
    //   17: invokevirtual 522	android/graphics/Canvas:save	()I
    //   20: pop
    //   21: aload_1
    //   22: aload_0
    //   23: invokevirtual 396	com/actionbarsherlock/internal/widget/IcsProgressBar:getPaddingLeft	()I
    //   26: aload_0
    //   27: getfield 416	com/actionbarsherlock/internal/widget/IcsProgressBar:mIndeterminateRealLeft	I
    //   30: iadd
    //   31: i2f
    //   32: aload_0
    //   33: invokevirtual 402	com/actionbarsherlock/internal/widget/IcsProgressBar:getPaddingTop	()I
    //   36: aload_0
    //   37: getfield 418	com/actionbarsherlock/internal/widget/IcsProgressBar:mIndeterminateRealTop	I
    //   40: iadd
    //   41: i2f
    //   42: invokevirtual 526	android/graphics/Canvas:translate	(FF)V
    //   45: aload_0
    //   46: invokevirtual 529	com/actionbarsherlock/internal/widget/IcsProgressBar:getDrawingTime	()J
    //   49: lstore 5
    //   51: aload_0
    //   52: getfield 531	com/actionbarsherlock/internal/widget/IcsProgressBar:mAnimation	Landroid/view/animation/AlphaAnimation;
    //   55: ifnull +81 -> 136
    //   58: aload_0
    //   59: getfield 531	com/actionbarsherlock/internal/widget/IcsProgressBar:mAnimation	Landroid/view/animation/AlphaAnimation;
    //   62: lload 5
    //   64: aload_0
    //   65: getfield 533	com/actionbarsherlock/internal/widget/IcsProgressBar:mTransformation	Landroid/view/animation/Transformation;
    //   68: invokevirtual 539	android/view/animation/AlphaAnimation:getTransformation	(JLandroid/view/animation/Transformation;)Z
    //   71: pop
    //   72: aload_0
    //   73: getfield 533	com/actionbarsherlock/internal/widget/IcsProgressBar:mTransformation	Landroid/view/animation/Transformation;
    //   76: invokevirtual 545	android/view/animation/Transformation:getAlpha	()F
    //   79: fstore 8
    //   81: aload_0
    //   82: iconst_1
    //   83: putfield 462	com/actionbarsherlock/internal/widget/IcsProgressBar:mInDrawing	Z
    //   86: aload_3
    //   87: ldc_w 271
    //   90: fload 8
    //   92: fmul
    //   93: f2i
    //   94: invokevirtual 262	android/graphics/drawable/Drawable:setLevel	(I)Z
    //   97: pop
    //   98: aload_0
    //   99: iconst_0
    //   100: putfield 462	com/actionbarsherlock/internal/widget/IcsProgressBar:mInDrawing	Z
    //   103: invokestatic 550	android/os/SystemClock:uptimeMillis	()J
    //   106: aload_0
    //   107: getfield 552	com/actionbarsherlock/internal/widget/IcsProgressBar:mLastDrawTime	J
    //   110: lsub
    //   111: aload_0
    //   112: getfield 226	com/actionbarsherlock/internal/widget/IcsProgressBar:mAnimationResolution	I
    //   115: i2l
    //   116: lcmp
    //   117: iflt +19 -> 136
    //   120: aload_0
    //   121: invokestatic 550	android/os/SystemClock:uptimeMillis	()J
    //   124: putfield 552	com/actionbarsherlock/internal/widget/IcsProgressBar:mLastDrawTime	J
    //   127: aload_0
    //   128: aload_0
    //   129: getfield 226	com/actionbarsherlock/internal/widget/IcsProgressBar:mAnimationResolution	I
    //   132: i2l
    //   133: invokevirtual 556	com/actionbarsherlock/internal/widget/IcsProgressBar:postInvalidateDelayed	(J)V
    //   136: aload_3
    //   137: aload_1
    //   138: invokevirtual 559	android/graphics/drawable/Drawable:draw	(Landroid/graphics/Canvas;)V
    //   141: aload_1
    //   142: invokevirtual 562	android/graphics/Canvas:restore	()V
    //   145: aload_0
    //   146: getfield 564	com/actionbarsherlock/internal/widget/IcsProgressBar:mShouldStartAnimationDrawable	Z
    //   149: ifeq +24 -> 173
    //   152: aload_3
    //   153: instanceof 566
    //   156: ifeq +17 -> 173
    //   159: aload_3
    //   160: checkcast 566	android/graphics/drawable/Animatable
    //   163: invokeinterface 569 1 0
    //   168: aload_0
    //   169: iconst_0
    //   170: putfield 564	com/actionbarsherlock/internal/widget/IcsProgressBar:mShouldStartAnimationDrawable	Z
    //   173: aload_0
    //   174: monitorexit
    //   175: return
    //   176: astore 9
    //   178: aload_0
    //   179: iconst_0
    //   180: putfield 462	com/actionbarsherlock/internal/widget/IcsProgressBar:mInDrawing	Z
    //   183: aload 9
    //   185: athrow
    //   186: astore_2
    //   187: aload_0
    //   188: monitorexit
    //   189: aload_2
    //   190: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	191	0	this	IcsProgressBar
    //   0	191	1	paramCanvas	android.graphics.Canvas
    //   186	4	2	localObject1	Object
    //   11	149	3	localDrawable	Drawable
    //   49	14	5	l	long
    //   79	12	8	f	float
    //   176	8	9	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   81	98	176	finally
    //   2	12	186	finally
    //   16	81	186	finally
    //   98	136	186	finally
    //   136	173	186	finally
    //   178	186	186	finally
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    paramAccessibilityEvent.setItemCount(this.mMax);
    paramAccessibilityEvent.setCurrentItemIndex(this.mProgress);
  }
  
  /* Error */
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 251	com/actionbarsherlock/internal/widget/IcsProgressBar:mCurrentDrawable	Landroid/graphics/drawable/Drawable;
    //   6: astore 4
    //   8: iconst_0
    //   9: istore 5
    //   11: iconst_0
    //   12: istore 6
    //   14: aload 4
    //   16: ifnull +45 -> 61
    //   19: aload_0
    //   20: getfield 167	com/actionbarsherlock/internal/widget/IcsProgressBar:mMinWidth	I
    //   23: aload_0
    //   24: getfield 172	com/actionbarsherlock/internal/widget/IcsProgressBar:mMaxWidth	I
    //   27: aload 4
    //   29: invokevirtual 407	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   32: invokestatic 587	java/lang/Math:min	(II)I
    //   35: invokestatic 590	java/lang/Math:max	(II)I
    //   38: istore 6
    //   40: aload_0
    //   41: getfield 174	com/actionbarsherlock/internal/widget/IcsProgressBar:mMinHeight	I
    //   44: aload_0
    //   45: getfield 176	com/actionbarsherlock/internal/widget/IcsProgressBar:mMaxHeight	I
    //   48: aload 4
    //   50: invokevirtual 410	android/graphics/drawable/Drawable:getIntrinsicHeight	()I
    //   53: invokestatic 587	java/lang/Math:min	(II)I
    //   56: invokestatic 590	java/lang/Math:max	(II)I
    //   59: istore 5
    //   61: aload_0
    //   62: invokespecial 437	com/actionbarsherlock/internal/widget/IcsProgressBar:updateDrawableState	()V
    //   65: iload 6
    //   67: aload_0
    //   68: invokevirtual 396	com/actionbarsherlock/internal/widget/IcsProgressBar:getPaddingLeft	()I
    //   71: aload_0
    //   72: invokevirtual 393	com/actionbarsherlock/internal/widget/IcsProgressBar:getPaddingRight	()I
    //   75: iadd
    //   76: iadd
    //   77: istore 7
    //   79: iload 5
    //   81: aload_0
    //   82: invokevirtual 402	com/actionbarsherlock/internal/widget/IcsProgressBar:getPaddingTop	()I
    //   85: aload_0
    //   86: invokevirtual 399	com/actionbarsherlock/internal/widget/IcsProgressBar:getPaddingBottom	()I
    //   89: iadd
    //   90: iadd
    //   91: istore 8
    //   93: getstatic 92	com/actionbarsherlock/internal/widget/IcsProgressBar:IS_HONEYCOMB	Z
    //   96: ifeq +24 -> 120
    //   99: aload_0
    //   100: iload 7
    //   102: iload_1
    //   103: iconst_0
    //   104: invokestatic 594	android/view/View:resolveSizeAndState	(III)I
    //   107: iload 8
    //   109: iload_2
    //   110: iconst_0
    //   111: invokestatic 594	android/view/View:resolveSizeAndState	(III)I
    //   114: invokevirtual 597	com/actionbarsherlock/internal/widget/IcsProgressBar:setMeasuredDimension	(II)V
    //   117: aload_0
    //   118: monitorexit
    //   119: return
    //   120: aload_0
    //   121: iload 7
    //   123: iload_1
    //   124: invokestatic 600	android/view/View:resolveSize	(II)I
    //   127: iload 8
    //   129: iload_2
    //   130: invokestatic 600	android/view/View:resolveSize	(II)I
    //   133: invokevirtual 597	com/actionbarsherlock/internal/widget/IcsProgressBar:setMeasuredDimension	(II)V
    //   136: goto -19 -> 117
    //   139: astore_3
    //   140: aload_0
    //   141: monitorexit
    //   142: aload_3
    //   143: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	144	0	this	IcsProgressBar
    //   0	144	1	paramInt1	int
    //   0	144	2	paramInt2	int
    //   139	4	3	localObject	Object
    //   6	43	4	localDrawable	Drawable
    //   9	82	5	i	int
    //   12	65	6	j	int
    //   77	45	7	k	int
    //   91	37	8	m	int
    // Exception table:
    //   from	to	target	type
    //   2	8	139	finally
    //   19	61	139	finally
    //   61	117	139	finally
    //   120	136	139	finally
  }
  
  void onProgressRefresh(float paramFloat, boolean paramBoolean)
  {
    if (this.mAccessibilityManager.isEnabled()) {
      scheduleAccessibilityEventSender();
    }
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    SavedState localSavedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(localSavedState.getSuperState());
    setProgress(localSavedState.progress);
    setSecondaryProgress(localSavedState.secondaryProgress);
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.progress = this.mProgress;
    localSavedState.secondaryProgress = this.mSecondaryProgress;
    return localSavedState;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    updateDrawableBounds(paramInt1, paramInt2);
  }
  
  protected void onVisibilityChanged(View paramView, int paramInt)
  {
    super.onVisibilityChanged(paramView, paramInt);
    if (this.mIndeterminate)
    {
      if ((paramInt == 8) || (paramInt == 4)) {
        stopAnimation();
      }
    }
    else {
      return;
    }
    startAnimation();
  }
  
  public void postInvalidate()
  {
    if (!this.mNoInvalidate) {
      super.postInvalidate();
    }
  }
  
  /* Error */
  public void setIndeterminate(boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 211	com/actionbarsherlock/internal/widget/IcsProgressBar:mOnlyIndeterminate	Z
    //   6: ifeq +10 -> 16
    //   9: aload_0
    //   10: getfield 217	com/actionbarsherlock/internal/widget/IcsProgressBar:mIndeterminate	Z
    //   13: ifne +32 -> 45
    //   16: iload_1
    //   17: aload_0
    //   18: getfield 217	com/actionbarsherlock/internal/widget/IcsProgressBar:mIndeterminate	Z
    //   21: if_icmpeq +24 -> 45
    //   24: aload_0
    //   25: iload_1
    //   26: putfield 217	com/actionbarsherlock/internal/widget/IcsProgressBar:mIndeterminate	Z
    //   29: iload_1
    //   30: ifeq +18 -> 48
    //   33: aload_0
    //   34: aload_0
    //   35: getfield 404	com/actionbarsherlock/internal/widget/IcsProgressBar:mIndeterminateDrawable	Landroid/graphics/drawable/Drawable;
    //   38: putfield 251	com/actionbarsherlock/internal/widget/IcsProgressBar:mCurrentDrawable	Landroid/graphics/drawable/Drawable;
    //   41: aload_0
    //   42: invokevirtual 507	com/actionbarsherlock/internal/widget/IcsProgressBar:startAnimation	()V
    //   45: aload_0
    //   46: monitorexit
    //   47: return
    //   48: aload_0
    //   49: aload_0
    //   50: getfield 420	com/actionbarsherlock/internal/widget/IcsProgressBar:mProgressDrawable	Landroid/graphics/drawable/Drawable;
    //   53: putfield 251	com/actionbarsherlock/internal/widget/IcsProgressBar:mCurrentDrawable	Landroid/graphics/drawable/Drawable;
    //   56: aload_0
    //   57: invokevirtual 511	com/actionbarsherlock/internal/widget/IcsProgressBar:stopAnimation	()V
    //   60: goto -15 -> 45
    //   63: astore_2
    //   64: aload_0
    //   65: monitorexit
    //   66: aload_2
    //   67: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	68	0	this	IcsProgressBar
    //   0	68	1	paramBoolean	boolean
    //   63	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	16	63	finally
    //   16	29	63	finally
    //   33	45	63	finally
    //   48	60	63	finally
  }
  
  public void setIndeterminateDrawable(Drawable paramDrawable)
  {
    if (paramDrawable != null) {
      paramDrawable.setCallback(this);
    }
    this.mIndeterminateDrawable = paramDrawable;
    if (this.mIndeterminate)
    {
      this.mCurrentDrawable = paramDrawable;
      postInvalidate();
    }
  }
  
  public void setInterpolator(Context paramContext, int paramInt)
  {
    setInterpolator(AnimationUtils.loadInterpolator(paramContext, paramInt));
  }
  
  public void setInterpolator(Interpolator paramInterpolator)
  {
    this.mInterpolator = paramInterpolator;
  }
  
  public void setMax(int paramInt)
  {
    if (paramInt < 0) {
      paramInt = 0;
    }
    try
    {
      if (paramInt != this.mMax)
      {
        this.mMax = paramInt;
        postInvalidate();
        if (this.mProgress > paramInt) {
          this.mProgress = paramInt;
        }
        refreshProgress(16908301, this.mProgress, false);
      }
      return;
    }
    finally {}
  }
  
  public void setProgress(int paramInt)
  {
    try
    {
      setProgress(paramInt, false);
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  /* Error */
  void setProgress(int paramInt, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 217	com/actionbarsherlock/internal/widget/IcsProgressBar:mIndeterminate	Z
    //   6: istore 4
    //   8: iload 4
    //   10: ifeq +6 -> 16
    //   13: aload_0
    //   14: monitorexit
    //   15: return
    //   16: iload_1
    //   17: ifge +5 -> 22
    //   20: iconst_0
    //   21: istore_1
    //   22: iload_1
    //   23: aload_0
    //   24: getfield 188	com/actionbarsherlock/internal/widget/IcsProgressBar:mMax	I
    //   27: if_icmple +8 -> 35
    //   30: aload_0
    //   31: getfield 188	com/actionbarsherlock/internal/widget/IcsProgressBar:mMax	I
    //   34: istore_1
    //   35: iload_1
    //   36: aload_0
    //   37: getfield 194	com/actionbarsherlock/internal/widget/IcsProgressBar:mProgress	I
    //   40: if_icmpeq -27 -> 13
    //   43: aload_0
    //   44: iload_1
    //   45: putfield 194	com/actionbarsherlock/internal/widget/IcsProgressBar:mProgress	I
    //   48: aload_0
    //   49: ldc_w 263
    //   52: aload_0
    //   53: getfield 194	com/actionbarsherlock/internal/widget/IcsProgressBar:mProgress	I
    //   56: iload_2
    //   57: invokespecial 651	com/actionbarsherlock/internal/widget/IcsProgressBar:refreshProgress	(IIZ)V
    //   60: goto -47 -> 13
    //   63: astore_3
    //   64: aload_0
    //   65: monitorexit
    //   66: aload_3
    //   67: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	68	0	this	IcsProgressBar
    //   0	68	1	paramInt	int
    //   0	68	2	paramBoolean	boolean
    //   63	4	3	localObject	Object
    //   6	3	4	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	8	63	finally
    //   22	35	63	finally
    //   35	60	63	finally
  }
  
  public void setProgressDrawable(Drawable paramDrawable)
  {
    if ((this.mProgressDrawable != null) && (paramDrawable != this.mProgressDrawable)) {
      this.mProgressDrawable.setCallback(null);
    }
    for (int i = 1;; i = 0)
    {
      if (paramDrawable != null)
      {
        paramDrawable.setCallback(this);
        int j = paramDrawable.getMinimumHeight();
        if (this.mMaxHeight < j)
        {
          this.mMaxHeight = j;
          requestLayout();
        }
      }
      this.mProgressDrawable = paramDrawable;
      if (!this.mIndeterminate)
      {
        this.mCurrentDrawable = paramDrawable;
        postInvalidate();
      }
      if (i != 0)
      {
        updateDrawableBounds(getWidth(), getHeight());
        updateDrawableState();
        doRefreshProgress(16908301, this.mProgress, false, false);
        doRefreshProgress(16908303, this.mSecondaryProgress, false, false);
      }
      return;
    }
  }
  
  /* Error */
  public void setSecondaryProgress(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 217	com/actionbarsherlock/internal/widget/IcsProgressBar:mIndeterminate	Z
    //   6: istore_3
    //   7: iload_3
    //   8: ifeq +6 -> 14
    //   11: aload_0
    //   12: monitorexit
    //   13: return
    //   14: iload_1
    //   15: ifge +5 -> 20
    //   18: iconst_0
    //   19: istore_1
    //   20: iload_1
    //   21: aload_0
    //   22: getfield 188	com/actionbarsherlock/internal/widget/IcsProgressBar:mMax	I
    //   25: if_icmple +8 -> 33
    //   28: aload_0
    //   29: getfield 188	com/actionbarsherlock/internal/widget/IcsProgressBar:mMax	I
    //   32: istore_1
    //   33: iload_1
    //   34: aload_0
    //   35: getfield 199	com/actionbarsherlock/internal/widget/IcsProgressBar:mSecondaryProgress	I
    //   38: if_icmpeq -27 -> 11
    //   41: aload_0
    //   42: iload_1
    //   43: putfield 199	com/actionbarsherlock/internal/widget/IcsProgressBar:mSecondaryProgress	I
    //   46: aload_0
    //   47: ldc_w 311
    //   50: aload_0
    //   51: getfield 199	com/actionbarsherlock/internal/widget/IcsProgressBar:mSecondaryProgress	I
    //   54: iconst_0
    //   55: invokespecial 651	com/actionbarsherlock/internal/widget/IcsProgressBar:refreshProgress	(IIZ)V
    //   58: goto -47 -> 11
    //   61: astore_2
    //   62: aload_0
    //   63: monitorexit
    //   64: aload_2
    //   65: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	66	0	this	IcsProgressBar
    //   0	66	1	paramInt	int
    //   61	4	2	localObject	Object
    //   6	2	3	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	7	61	finally
    //   20	33	61	finally
    //   33	58	61	finally
  }
  
  public void setVisibility(int paramInt)
  {
    if (getVisibility() != paramInt)
    {
      super.setVisibility(paramInt);
      if (this.mIndeterminate)
      {
        if ((paramInt != 8) && (paramInt != 4)) {
          break label36;
        }
        stopAnimation();
      }
    }
    return;
    label36:
    startAnimation();
  }
  
  void startAnimation()
  {
    if (getVisibility() != 0) {
      return;
    }
    if ((this.mIndeterminateDrawable instanceof Animatable))
    {
      this.mShouldStartAnimationDrawable = true;
      this.mAnimation = null;
    }
    for (;;)
    {
      postInvalidate();
      return;
      if (this.mInterpolator == null) {
        this.mInterpolator = new LinearInterpolator();
      }
      this.mTransformation = new Transformation();
      this.mAnimation = new AlphaAnimation(0.0F, 1.0F);
      this.mAnimation.setRepeatMode(this.mBehavior);
      this.mAnimation.setRepeatCount(-1);
      this.mAnimation.setDuration(this.mDuration);
      this.mAnimation.setInterpolator(this.mInterpolator);
      this.mAnimation.setStartTime(-1L);
    }
  }
  
  void stopAnimation()
  {
    this.mAnimation = null;
    this.mTransformation = null;
    if ((this.mIndeterminateDrawable instanceof Animatable))
    {
      ((Animatable)this.mIndeterminateDrawable).stop();
      this.mShouldStartAnimationDrawable = false;
    }
    postInvalidate();
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    return (paramDrawable == this.mProgressDrawable) || (paramDrawable == this.mIndeterminateDrawable) || (super.verifyDrawable(paramDrawable));
  }
  
  private class AccessibilityEventSender
    implements Runnable
  {
    private AccessibilityEventSender() {}
    
    public void run()
    {
      IcsProgressBar.this.sendAccessibilityEvent(4);
    }
  }
  
  private class RefreshProgressRunnable
    implements Runnable
  {
    private boolean mFromUser;
    private int mId;
    private int mProgress;
    
    RefreshProgressRunnable(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this.mId = paramInt1;
      this.mProgress = paramInt2;
      this.mFromUser = paramBoolean;
    }
    
    public void run()
    {
      IcsProgressBar.this.doRefreshProgress(this.mId, this.mProgress, this.mFromUser, true);
      IcsProgressBar.access$102(IcsProgressBar.this, this);
    }
    
    public void setup(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this.mId = paramInt1;
      this.mProgress = paramInt2;
      this.mFromUser = paramBoolean;
    }
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public IcsProgressBar.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new IcsProgressBar.SavedState(paramAnonymousParcel, null);
      }
      
      public IcsProgressBar.SavedState[] newArray(int paramAnonymousInt)
      {
        return new IcsProgressBar.SavedState[paramAnonymousInt];
      }
    };
    int progress;
    int secondaryProgress;
    
    private SavedState(Parcel paramParcel)
    {
      super();
      this.progress = paramParcel.readInt();
      this.secondaryProgress = paramParcel.readInt();
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.progress);
      paramParcel.writeInt(this.secondaryProgress);
    }
  }
}


/* Location:
 * Qualified Name:     com.actionbarsherlock.internal.widget.IcsProgressBar
 * JD-Core Version:    0.7.0.1
 */