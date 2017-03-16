package co.uk.rushorm.android.testobjects;

import java.util.Date;

import co.uk.rushorm.core.RushObject;

/**
 * Created by Stuart on 18/02/15.
 */
public class Bug6 extends RushObject {

    private long mUserId;

    private long mActualId;

    private String mDistance;

    private Date mBirthday;

    private String mGender;

    private Date mCreatedAt;

    private String mStatusMessage;

    private boolean mIsHereNow;

    private Float mRank;

    public Bug6() {}

    public Bug6(String string) {
        mUserId = 5;
        mActualId = 5;
        mDistance = "Far away";
        mBirthday = new Date();
        mGender = "Boy";
        mCreatedAt = new Date();
        mStatusMessage = "Status";
        mIsHereNow = true;
        mRank = 50654.45f;

    }

}
