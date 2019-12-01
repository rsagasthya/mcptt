/*
Copyright 2018 Bittium Wireless Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.mcopenplatform.iapi;

import android.os.Messenger;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Handler;

public class McopMessenger implements Parcelable {
    private Messenger m;

    public void writeToParcel(Parcel out, int flags) {
        m.writeToParcel(out, flags);
    }

    public int describeContents() { return 0; }

    public static final Parcelable.Creator<McopMessenger> CREATOR
            = new Parcelable.Creator<McopMessenger>() {
        public McopMessenger createFromParcel(Parcel in) {
            return new McopMessenger(in);
        }

        public McopMessenger[] newArray(int size) {
            return new McopMessenger[size];
        }
    };

    public McopMessenger(Handler h) {
        this.m = new Messenger(h);
    }

    private McopMessenger(Parcel p) {
        this.m = Messenger.CREATOR.createFromParcel(p);
    }

    public Messenger getMessenger() { return m; }
}

