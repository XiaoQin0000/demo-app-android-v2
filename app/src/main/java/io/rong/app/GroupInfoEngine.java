package io.rong.app;

import android.content.Context;
import android.net.Uri;

import io.rong.app.server.SealAction;
import io.rong.app.server.network.async.AsyncTaskManager;
import io.rong.app.server.network.async.OnDataListener;
import io.rong.app.server.network.http.HttpException;
import io.rong.app.server.response.GetGroupInfoResponse;
import io.rong.imlib.model.Group;

/**
 * Created by AMing on 16/1/26.
 * Company RongCloud
 */
public class GroupInfoEngine implements OnDataListener {

    private static final int REQUESTGROUPINFO = 19;
    private static GroupInfoEngine instance;
    private GroupInfoListeners mListener;

    private String groupId;
    private Group group;


    private GroupInfoEngine(Context context) {
        this.context = context;
    }

    private static Context context;

    public static GroupInfoEngine getInstance(Context context) {
        if (instance == null) {
            instance = new GroupInfoEngine(context);
        }
        return instance;
    }


    public Group startEngine(String groupId) {
        setGroupId(groupId);
        AsyncTaskManager.getInstance(context).request(REQUESTGROUPINFO, this);
        return getGroup();
    }

    @Override
    public Object doInBackground(int requestCode) throws HttpException {
        return new SealAction(context).getGroupInfo(getGroupId());
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            GetGroupInfoResponse ggiRes = (GetGroupInfoResponse) result;
            if (ggiRes.getCode() == 200) {
                group = new Group(ggiRes.getResult().getId(), ggiRes.getResult().getName(), Uri.parse(ggiRes.getResult().getPortraitUri()));
                if (mListener != null) {
                    mListener.onResult(group);
                }
            }
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case REQUESTGROUPINFO:
                break;
        }
    }

    public String getGroupId() {
        return groupId;
    }

    public void setmListener(GroupInfoListeners mListener) {
        this.mListener = mListener;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public interface GroupInfoListeners {
        void onResult(Group info);
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}