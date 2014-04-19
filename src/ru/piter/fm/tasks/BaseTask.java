package ru.piter.fm.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import ru.piter.fm.util.Utils;

public abstract class BaseTask<T>
  extends AsyncTask<Object, Void, T>
{
  public Context context;
  public ProgressDialog dialog;
  public Exception exception;
  public boolean isOnline = true;
  
  protected BaseTask() {}
  
  public BaseTask(Context paramContext)
  {
    this.context = paramContext;
    this.dialog = new ProgressDialog(paramContext);
  }
  
  protected T doInBackground(Object... paramVarArgs)
  {
    try
    {
      Object localObject = doWork(paramVarArgs);
      return localObject;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      this.exception = localException;
    }
    return null;
  }
  
  public abstract T doWork(Object... paramVarArgs)
    throws Exception;
  
  protected void onCancelled(T paramT)
  {
    super.onCancelled(paramT);
    if (this.dialog.isShowing()) {
      this.dialog.dismiss();
    }
  }
  
  public abstract void onError(Exception paramException);
  
  protected void onPostExecute(T paramT)
  {
    if ((this.dialog != null) && (this.dialog.isShowing())) {
      this.dialog.dismiss();
    }
    if (this.exception == null)
    {
      onResult(paramT);
      return;
    }
    onError(this.exception);
  }
  
  protected void onPreExecute()
  {
    if (!Utils.isInternetAvailable(this.context)) {
      this.isOnline = false;
    }
    this.dialog.setMessage(this.context.getResources().getString(2131361802));
    this.dialog.show();
  }
  
  public abstract void onResult(T paramT);
}


/* Location:
 * Qualified Name:     ru.piter.fm.tasks.BaseTask
 * JD-Core Version:    0.7.0.1
 */