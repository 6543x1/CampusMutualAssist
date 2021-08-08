package com.jessie.campusmutualassist.entity;


import java.io.Serializable;

public class UserPortrait implements Serializable
{
//原定是用于记录用户的数据，但是后面功能被刀了，就。。。就其中有一部分没有使用吧
    private int uid;
    private int countGoodComment;
    private int countMediumComment;
    private int countBadComment;
    private int countPunishedScore;
    private int countAdditionalScore;
    private int countSendGoodsComment;
    private int countSendMediumComment;
    private int countSendBadComment;

    public UserPortrait(int uid, int countGoodComment, int countMediumComment, int countBadComment, int countPunishedScore, int countAdditionalScore, int countSendGoodsComment, int countSendMediumComment, int countSendBadComment)
    {
        this.uid = uid;
        this.countGoodComment = countGoodComment;
        this.countMediumComment = countMediumComment;
        this.countBadComment = countBadComment;
        this.countPunishedScore = countPunishedScore;
        this.countAdditionalScore = countAdditionalScore;
        this.countSendGoodsComment = countSendGoodsComment;
        this.countSendMediumComment = countSendMediumComment;
        this.countSendBadComment = countSendBadComment;
    }

    public UserPortrait()
    {
    }

    public UserPortrait(int uid)
    {
        this.uid = uid;
        this.countGoodComment = 0;
        this.countMediumComment = 0;
        this.countBadComment = 0;
        this.countPunishedScore = 0;
        this.countAdditionalScore = 0;
        this.countSendGoodsComment = 0;
        this.countSendMediumComment = 0;
        this.countSendBadComment = 0;
    }

    public int getUid()
    {
        return uid;
    }

    public void setUid(int uid)
    {
        this.uid = uid;
    }


    public int getCountGoodComment()
    {
        return countGoodComment;
    }

    public void setCountGoodComment(int countGoodComment)
    {
        this.countGoodComment = countGoodComment;
    }


    public int getCountMediumComment()
    {
        return countMediumComment;
    }

    public void setCountMediumComment(int countMediumComment)
    {
        this.countMediumComment = countMediumComment;
    }


    public int getCountBadComment()
    {
        return countBadComment;
    }

    public void setCountBadComment(int countBadComment)
    {
        this.countBadComment = countBadComment;
    }


    public int getCountPunishedScore()
    {
        return countPunishedScore;
    }

    public void setCountPunishedScore(int countPunishedScore)
    {
        this.countPunishedScore = countPunishedScore;
    }


    public int getCountAdditionalScore()
    {
        return countAdditionalScore;
    }

    public void setCountAdditionalScore(int countAdditionalScore)
    {
        this.countAdditionalScore = countAdditionalScore;
    }


    public int getCountSendGoodsComment()
    {
        return countSendGoodsComment;
    }

    public void setCountSendGoodsComment(int countSendGoodsComment)
    {
        this.countSendGoodsComment = countSendGoodsComment;
    }


    public int getCountSendMediumComment()
    {
        return countSendMediumComment;
    }

    public void setCountSendMediumComment(int countSendMediumComment)
    {
        this.countSendMediumComment = countSendMediumComment;
    }


    public int getCountSendBadComment()
    {
        return countSendBadComment;
    }

    public void setCountSendBadComment(int countSendBadComment)
    {
        this.countSendBadComment = countSendBadComment;
    }

}
