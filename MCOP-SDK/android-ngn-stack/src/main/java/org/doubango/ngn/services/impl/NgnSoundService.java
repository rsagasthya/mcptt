/*
* Copyright (C) 2017, University of the Basque Country (UPV/EHU)
*  Contact for licensing options: <licensing-mcpttclient(at)mcopenplatform(dot)com>
*
* The original file was part of Open Source IMSDROID
*  Copyright (C) 2010-2011, Mamadou Diop.
*  Copyright (C) 2011, Doubango Telecom.
*
*
* Contact: Mamadou Diop <diopmamadou(at)doubango(dot)org>
*
* This file is part of Open Source Doubango Framework.
*
* This is free software: you can redistribute it and/or modify it under the terms of
* the GNU General Public License as published by the Free Software Foundation, either version 3
* of the License, or (at your option) any later version.
*
* This is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package org.doubango.ngn.services.impl;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.os.Vibrator;
import android.util.Log;

import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.services.INgnSoundService;
import org.doubango.utils.Utils;

import static android.content.Context.VIBRATOR_SERVICE;

/**@page NgnSoundService_page Sound Service
 * 
 * The sound service is used to play the tones (ringtone, ringback, alert, ...).
 * You have to start the service through the NGN engine before any use.
 * 
 * @code
 * // Gets and instance of the NGN engine
 * NgnEngine mEngine = NgnEngine.getInstance();
 * // Plays the ringback tone
 * mEngine.getSoundService().startRingBackTone();
 * // Stops the ringback tone
 * mEngine.getSoundService().stopRingBackTone();
 * @endcode
 */

public class NgnSoundService extends NgnBaseService implements INgnSoundService{
	private final static String TAG = Utils.getTAG(NgnSoundService.class.getCanonicalName());
	
	/** DTMF tone volume relative to other sounds in the stream */
	private static final int TONE_RELATIVE_VOLUME = 50;
	
	private ToneGenerator mRingbackPlayer;
	private ToneGenerator mDTMFPlayer;
	protected Ringtone mRingtonePlayer;
	protected Vibrator mVibrator;

	@Override
	public boolean start() {
		Log.d(TAG, "starting...");
		return true;
	}

	@Override
	public boolean stop() {
		Log.d(TAG, "stopping...");
		
		if(mRingbackPlayer != null){
			synchronized(mRingbackPlayer){
				mRingbackPlayer.release();
				mRingbackPlayer = null;
			}
		}
		
		if(mRingtonePlayer != null){
			synchronized(mRingtonePlayer){
				if(mRingtonePlayer.isPlaying()){
					mRingtonePlayer.stop();
				}
				mRingtonePlayer = null;
			}
		}
		
		if(mDTMFPlayer != null){
			synchronized(mDTMFPlayer){
				mDTMFPlayer.release();
				mDTMFPlayer = null;
			}
		}
		
		return true;
	}

	@Override
	public void startDTMF(int number) {
		if (mDTMFPlayer == null) {
			try {
				mDTMFPlayer = new ToneGenerator(AudioManager.STREAM_VOICE_CALL, TONE_RELATIVE_VOLUME);
			} catch (RuntimeException e) {
				Log.w(TAG, "Exception caught while creating local tone generator: " + e);
				mDTMFPlayer = null;
			}
		}

		if(mDTMFPlayer != null){
			synchronized(mDTMFPlayer){
				switch(number){
					case 0: mDTMFPlayer.startTone(ToneGenerator.TONE_DTMF_0); break;
					case 1: mDTMFPlayer.startTone(ToneGenerator.TONE_DTMF_1); break;
					case 2: mDTMFPlayer.startTone(ToneGenerator.TONE_DTMF_2); break;
					case 3: mDTMFPlayer.startTone(ToneGenerator.TONE_DTMF_3); break;
					case 4: mDTMFPlayer.startTone(ToneGenerator.TONE_DTMF_4); break;
					case 5: mDTMFPlayer.startTone(ToneGenerator.TONE_DTMF_5); break;
					case 6: mDTMFPlayer.startTone(ToneGenerator.TONE_DTMF_6); break;
					case 7: mDTMFPlayer.startTone(ToneGenerator.TONE_DTMF_7); break;
					case 8: mDTMFPlayer.startTone(ToneGenerator.TONE_DTMF_8); break;
					case 9: mDTMFPlayer.startTone(ToneGenerator.TONE_DTMF_9); break;
					case 10: mDTMFPlayer.startTone(ToneGenerator.TONE_DTMF_S); break;
					case 11: mDTMFPlayer.startTone(ToneGenerator.TONE_DTMF_P); break;
				}
			}
		}
	}

	@Override
	public void stopDTMF() {
		if(mDTMFPlayer != null){
			synchronized(mDTMFPlayer){
				mDTMFPlayer.stopTone();
			}
		}
	}

	private void shakeItBaby(Context context) {
		long[] twice = {500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500,
				100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500
				,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500,
				100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500
				,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500,
				100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500, 100,500};
		if(mVibrator==null)
		mVibrator=((Vibrator) context.getSystemService(VIBRATOR_SERVICE));

		if(mVibrator!=null) {
			synchronized (mVibrator) {
				mVibrator.vibrate(twice, -1);
			}
		}
	}

	@Override
	public void startRingTone() {

		if(mRingtonePlayer == null){
			try{
				mRingtonePlayer = RingtoneManager.getRingtone(NgnApplication.getContext(), android.provider.Settings.System.DEFAULT_RINGTONE_URI);
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
		}
		shakeItBaby(NgnApplication.getContext());
		if(mRingtonePlayer != null){
			synchronized(mRingtonePlayer){
				mRingtonePlayer.play();
			}
		}
	}

	@Override
	public void stopRingTone() {
		if(mRingtonePlayer != null){
			synchronized(mRingtonePlayer){
				mRingtonePlayer.stop();
			}
		}
		if(mVibrator!=null){
			synchronized(mVibrator){
				mVibrator.cancel();
			}
		}
	}

	@Override
	public void startRingBackTone() {
		if (mRingbackPlayer == null) {
			try {
				mRingbackPlayer = new ToneGenerator(AudioManager.STREAM_VOICE_CALL, TONE_RELATIVE_VOLUME);
			} catch (RuntimeException e) {
				Log.w(TAG, "Exception caught while creating local tone generator: " + e);
				mRingbackPlayer = null;
			}
		}

		if(mRingbackPlayer != null){
			synchronized(mRingbackPlayer){
				mRingbackPlayer.startTone(ToneGenerator.TONE_SUP_RINGTONE);
			}
		}
	}

	@Override
	public void stopRingBackTone() {
		if(mRingbackPlayer != null){
			synchronized(mRingbackPlayer){
				mRingbackPlayer.stopTone();
			}
		}
	}

	@Override
	public void startNewEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopNewEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startConnectionChanged(boolean connected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopConnectionChanged(boolean connected) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean clearService(){
		return true;
	}

}
