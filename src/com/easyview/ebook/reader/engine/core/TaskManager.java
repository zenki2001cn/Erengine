/**
 * @file       TaskManager.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-1-26 下午04:50:45 
 */

package com.easyview.ebook.reader.engine.core;

import java.util.ArrayList;

import com.easyview.ebook.reader.engine.core.IActionCall;
import com.easyview.ebook.reader.engine.core.EngineCode.EngineMsgCode;
import com.easyview.ebook.reader.engine.util.Logger;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class TaskManager {
	static private final String TAG = "TaskManager";
	private ArrayList<ActionTask> mTaskQueue = new ArrayList<ActionTask>();
	Thread mThread = null;
	static private TaskManager mTaskManager = null;
	static private TaskHandler mHandler = null;
	static private EngineCode mEngineError = null;

	private boolean mServerQuit = false;
	private volatile boolean mServerRunning = false;
	private ActionTask mCurTask = null;

	private TaskManager() {
	}

	static protected TaskManager getInstance() {
		if (null == mTaskManager) {
			mTaskManager = new TaskManager();
		}

		if (null == mHandler) {
			mHandler = new TaskHandler(mTaskManager);
		}

		if (null == mEngineError) {
			mEngineError = EngineCode.getInstance();
		}

		if (mTaskManager != null) {
			mTaskManager.serverStart();
		}

		return mTaskManager;
	}

	synchronized private boolean hasElement() {
		if (mTaskQueue != null) {
			return !mTaskQueue.isEmpty();
		}

		return false;
	}

	synchronized void put(ActionTask task, boolean head) {
		if (mTaskQueue != null) {
			if (head) {
				mTaskQueue.add(0, task);
			} else {
				mTaskQueue.add(task);
			}
		} else {
			Logger.dLog(TAG, "Task queue = " + mTaskQueue);
			return;
		}

		waitforServer();

		try {
			synchronized (mThread) {
				Logger.vLog(TAG, "Add a task and notify server, has task = "
						+ mTaskManager.hasElement());
				if (mThread != null) {
					mThread.notify();
				}

			}
		} catch (Exception e) {
			Logger.eLog(TAG, "Add task error" + e);
		}

	}

	synchronized protected void put(ActionTask task) {
		if (mTaskQueue.size() > 5) {
			return;
		}

		put(task, false);
	}

	synchronized private ActionTask take() {
		if ((mTaskQueue != null) && (mTaskQueue.size() > 0)) {
			return mTaskQueue.remove(0);
		}

		return null;
	}

	private void executeTask() {
		if (mCurTask != null) {
			Logger.vLog(TAG, "ExecuteTask status = " + mCurTask.getStatus());
			DecAdapter adapter = mCurTask.getDecAdapter();
			if (adapter != null) {
				adapter.setStop(false);
			}
			mCurTask.execute();
		}
	}

	private void cancelTask() {
		if (mCurTask != null) {
			Logger.vLog(TAG, "CancelTask status = " + mCurTask.getStatus());
			DecAdapter adapter = mCurTask.getDecAdapter();
			if (adapter != null) {
				adapter.cancelProcessing();
			}
			mCurTask.cancel(true);
		}
	}

	private synchronized void serverStop() {
		if (mThread != null) {
			if (mCurTask != null) {
				if (mCurTask != null) {
					mCurTask.cancel(true);
					mCurTask = null;
				}
			}

			mServerQuit = true;
			if (mTaskQueue != null) {
				mTaskQueue.clear();
				mTaskQueue = null;
			}

			try {
				synchronized (mThread) {
					mThread.notify();
				}
			} catch (Exception e) {
			}

			try {
				mThread.join(100);
			} catch (InterruptedException e) {
				Logger.eLog(TAG, "TaskManager join error");
			}

			mThread = null;
		}
		Logger.vLog(TAG, "TaskManager server stop");
	}

	private void waitforTask(ActionTask task) {
		Logger.vLog(TAG,
				"Waitfor other Task begin .... " + mCurTask.getStatus());
		while (!task.getStatus().equals(AsyncTask.Status.FINISHED)) {
			// Logger.vLog(TAG, "waitfor Task .... " + mCurTask.getStatus());
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Logger.eLog(TAG, "WaitforTask error");
				break;
			}
		}
		Logger.vLog(TAG, "Waitfor other Task end .... " + mCurTask.getStatus());
	}

	private void waitforServer() {
		int loop = 0;

		while (!mServerRunning) {
			Logger.vLog(TAG, "Waitfor server boot .....");
			try {
				if (loop > 10) {
					break;
				}

				Thread.sleep(100);
				loop++;
			} catch (Exception e) {
				Logger.eLog(TAG, "WaitforServer error");
			}

			mEngineError
					.setLastCode(EngineMsgCode.ENGINE_ERROR_TASK_SERVER_NOT_RUNNING);
		}
	}

	private synchronized void serverStart() {
		if (mThread == null) {
			mThread = new Thread() {
				public void run() {
					while (!mServerQuit) {
						if (!mTaskManager.hasElement()) {
							synchronized (this) {
								try {
									mCurTask = null;
									mServerRunning = true;

									Logger.vLog(TAG,
											"TaskManager server is waitting!");
									wait();
								} catch (InterruptedException e) {
									mEngineError
											.setLastCode(EngineMsgCode.ENGINE_ERROR_TASK_SERVER_CRASH);
								}
							}
						}

						cancelTask();

						Logger.eLog(TAG, "Enter task handler loop !!!!");
						try {
							while (mTaskManager.hasElement()) {
								mCurTask = mTaskManager.take();
								mHandler.sendEmptyMessage(TaskHandler.MESSAGE_EXECUTE);
								waitforTask(mCurTask);
							}

							Logger.eLog(TAG, "Exit task handler loop ....");

						} catch (Exception e) {
							Logger.eLog(TAG, e.toString());

							if (mEngineError != null) {
								mEngineError
										.setLastCode(EngineMsgCode.ENGINE_ERROR_TASK_SERVER_CRASH);
							}
						}
					}

					mServerRunning = false;
				}
			};

			mServerQuit = false;
			mThread.setDaemon(true);
			mThread.start();
			Logger.vLog(TAG, "TaskManager server start ...");
		} else {
			Logger.vLog(TAG, "TaskManager server is running");
		}
	}

	synchronized protected void clearTask() {
		if ((mTaskQueue != null) && (mTaskQueue.size() > 0)) {
			mTaskQueue.clear();
		}
	}

	protected void free() {
		if (mTaskManager != null) {
			mTaskManager.serverStop();
			mTaskManager = null;
		}

		if (mHandler != null) {
			mHandler = null;
		}

		if (mEngineError != null) {
			mEngineError.free();
			mEngineError = null;
		}

		Logger.dLog(TAG, "call free(): mThread = " + mThread
				+ " mTaskManager = " + mTaskManager + " mEngineError = "
				+ mEngineError);
	}

	static private class TaskHandler extends Handler {

		private TaskManager mTaskManager = null;

		static private final int MESSAGE_EXECUTE = 0;

		public TaskHandler(TaskManager taskManager) {
			mTaskManager = taskManager;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_EXECUTE:
				mTaskManager.executeTask();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	static protected class ActionTask extends
			AsyncTask<Object, Object, Boolean> {
		IActionCall mAction;
		IActionCall mPredo;
		IActionCall mTodo;

		DecAdapter mAdapter;

		protected void setDecAdapter(DecAdapter adapter) {
			mAdapter = adapter;
		}

		protected DecAdapter getDecAdapter() {
			return mAdapter;
		}

		protected ActionTask(IActionCall action, IActionCall predo,
				IActionCall todo) {
			mAction = action;
			mPredo = predo;
			mTodo = todo;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			if (mPredo != null) {
				Logger.dLog(TAG, "Predo action called");
				mPredo.action();
			}
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			boolean res = false;

			if (mAction != null) {
				Logger.dLog(TAG, "Doinbackground action called");
				res = mAction.action();
			}

			return res;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (mTodo != null) {
				Logger.dLog(TAG, "Todo action called");
				mTodo.action();
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			Logger.dLog(TAG, "Task onCancelled");
		}
	}
}
