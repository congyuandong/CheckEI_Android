package edu.congyuandong.checkei.offerWall;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import cn.domob.data.OErrorInfo;
import cn.domob.data.OManager;
import cn.domob.data.OManager.ConsumeStatus;
import edu.congyuandong.checkei.util.SystemSettings;

public class OfferWall {

	private static final String PUBLISHID = "96ZJ2b8QzehB3wTAwQ";
	//private static final String userId = "1";
	private OManager mManager;

	public OfferWall(Context mContext) {
		//mManager = new OManager(mContext, PUBLISHID, userId);
		mManager = new OManager(mContext,PUBLISHID);
	}

	// 获取积分
	public void wallGet() {
		mManager.setAddWallListener(new OManager.AddWallListener() {

			@Override
			public void onAddWallFailed(OErrorInfo mDomobOfferWallErrorInfo) {
				// showToast(mDomobOfferWallErrorInfo.toString());
				Log.e("wall_error", mDomobOfferWallErrorInfo.toString());
			}

			@Override
			public void onAddWallClose() {
				// 此处可以设置为横屏...
			}

			@Override
			public void onAddWallSucess() {

			}
		});
		mManager.loadOfferWall();
	}

	// 使用积分
	public void wallUse(int score) {
		mManager.setConsumeListener(new OManager.ConsumeListener() {

			@Override
			public void onConsumeFailed(
					final OErrorInfo mDomobOfferWallErrorInfo) {
				// showToast(mDomobOfferWallErrorInfo.toString());
				Log.e("wall_error", mDomobOfferWallErrorInfo.toString());
			}

			@Override
			public void onConsumeSucess(final int point, final int consumed,
					final ConsumeStatus cs) {

				switch (cs) {
				case SUCCEED:
					Log.v("wall_log", "消费成功:" + "总积分：" + point + "总消费积分："
							+ consumed);
					break;
				case OUT_OF_POINT:
					Log.v("wall_log", "总积分不足，消费失败：" + "总积分：" + point + "总消费积分："
							+ consumed);
					break;
				case ORDER_REPEAT:
					Log.v("wall_log", "订单号重复，消费失败：" + "总积分：" + point + "总消费积分："
							+ consumed);
					break;

				default:
					Log.e("wall_error", "未知错误");
					break;
				}
			}
		});
		if (score == 0) {
			// showToast("消费积分不能为空");
			Log.e("wall_error", "消费积分不能为空");
		} else {
			mManager.consumePoints(score);
		}
	}

	// 积分查询
	public void checkWall(final Context ctx) {
		mManager.setCheckPointsListener(new OManager.CheckPointsListener() {

			@Override
			public void onCheckPointsSucess(final int point, final int consumed) {
				Log.v("wall_log", "总积分：" + point + "总消费积分：" + consumed);
			    showToast("剩余积分：" + point + " 消费积分：" + consumed,ctx);
			    //存储积分信息
			    //SystemSettings.setSettingMessage(ctx, "EISEARCH_SCORE", point);
			}

			@Override
			public void onCheckPointsFailed(
					final OErrorInfo mDomobOfferWallErrorInfo) {
				Log.e("wall_error", mDomobOfferWallErrorInfo.toString());
			}
		});
		mManager.checkPoints();

	}
	
	public void showToast(final String content, final Context ctx) {
		((Activity) ctx).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(ctx, content, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
