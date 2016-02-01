package com.daemon.oxfordschool.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daemonsoft on 28/1/16.
 */
public class UserLogin implements Parcelable
{
    private String ID;
    private String UserType;
    private String FirstName;
    private String LastName;
    private String Email;
    private String Street_Address1;
    private String Street_Address2;
    private String Street_Address3;
    private String District;
    private String State;
    private String Mobile_Number;
    private String User_Password;
    private String Success;
    private String Message;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(ID);
        dest.writeString(UserType);
        dest.writeString(FirstName);
        dest.writeString(LastName);
        dest.writeString(Email);
        dest.writeString(Street_Address1);
        dest.writeString(Street_Address2);
        dest.writeString(Street_Address3);
        dest.writeString(District);
        dest.writeString(State);
        dest.writeString(Mobile_Number);
        dest.writeString(User_Password);
        dest.writeString(Success);
        dest.writeString(Message);

    }

    public UserLogin(Parcel source) {
        this.ID = source.readString();
        this.UserType = source.readString();
        this.FirstName = source.readString();
        this. LastName = source.readString();
        this.Email = source.readString();
        this.Street_Address1 = source.readString();
        this. Street_Address2 = source.readString();
        this. Street_Address3 = source.readString();
        this.District = source.readString();
        this. State = source.readString();
        this. Mobile_Number = source.readString();
        this. User_Password = source.readString();
        this. Success = source.readString();
        this. Message = source.readString();
    }

    public static final Parcelable.Creator<UserLogin> CREATOR = new Parcelable.Creator<UserLogin>()
    {
        public UserLogin createFromParcel(Parcel in)
        {
            return new UserLogin(in);
        }
        public UserLogin[] newArray(int size)
        {
            return new UserLogin[size];
        }
    };

}
