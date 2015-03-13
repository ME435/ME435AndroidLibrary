package edu.rosehulman.me435;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

/**
 * This class is a simple helper for doing TextToSpeech.  Create an instance
 * of this class then use the speak command.  In addition to speech there are
 * a few additional helper methods for ringtones and notification beeps.
 * 
 * @author fisherds@gmail.com (Dave Fisher)
 */
public class TextToSpeechHelper implements OnInitListener {

	/** TAG used for logging initialization error messages (if necessary). */
	public static final String TAG = TextToSpeechHelper.class.getSimpleName();
	
	/** Reference to use Androids TextToSpeech engine. */
	private TextToSpeech mTts;
	
	/** Reference to the main activity of this app. */
	private Activity mMainActivity;
	
	/** Refereces to old ringtones. */
	private Set<Ringtone> mPlayingRingtones = new HashSet<Ringtone>();
	
	/** Constructor */
	public TextToSpeechHelper(Activity mainActivity) {
		mTts = new TextToSpeech(mainActivity, this);
		mMainActivity = mainActivity;
	}
	
	/**
	 * Stop the TextToSpeech engine.
	 * Within the Activity's onDestroy method call this method.
	 */
	public void shutdown() {
		if (mTts != null) {
			mTts.stop();
			mTts.shutdown();
		}
	}
	

	/**
	 * Call this method with a String to speak that text.
	 * 
	 * @param messageToSpeak String to speak.
	 */
	public void speak(String messageToSpeak) {
		mTts.speak(messageToSpeak, TextToSpeech.QUEUE_FLUSH, null);
	}
	
	
	/**
	 * Makes a beep using the default notification.
	 * 
	 * If you can't here the beep you may need to visit Settings -> Sound -> Volumes
	 * to turn up the Notification sound.  So there are two volume settings, the main
	 * volume with the device buttons and the notification volume (both must be up).
	 * Then set your notification sound to your favorite tone (Mira is nice and short).
	 */
	public void playNotificationBeep() {
   	 try {
   	        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
   	        Ringtone r = RingtoneManager.getRingtone(mMainActivity, notification);
   	        r.play();
   	        mPlayingRingtones.add(r);
   	    } catch (Exception e) {}
	}
	
	/**
	 * Plays the default ring tone.
	 * 
	 * Much like the notificaiton beep within settings you can adjust the volume and tone.
	 */
	public void playDefaultRingtone() {
   	 try {
   	        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
   	        Ringtone r = RingtoneManager.getRingtone(mMainActivity, notification);
   	        r.play();
   	        mPlayingRingtones.add(r);
   	    } catch (Exception e) {}
	}
	
	/**
	 * Plays "some" valid ring tone (picked not sure how).  Uses some arbitrary method to
	 * select a valid tone then play it.  The URI of the tone selected is displayed in the
	 * log message.  For fun I provided another method below, playRingtone, that lets you
	 * pass in the final parameter of that URI to play a different tone.  You can look at
	 * the number displayed in the log and guess other ids that might work (many are silent).
	 */
	public void playValidRingtone() {
		try {
			Uri someRingtoneUri = RingtoneManager.getValidRingtoneUri(mMainActivity);
			Log.d(TAG, "URI used for rington " + someRingtoneUri);
			// Note you could manually alter that last value in the URI (see playRingtone).
   	        Ringtone r = RingtoneManager.getRingtone(mMainActivity, someRingtoneUri);
			r.play();
   	        mPlayingRingtones.add(r);
		} catch (Exception e) {
		}
	}
	
	/**
	 * Warning this is a very hacky function.  If you would like to guess at valid
	 * sound numbers you can play it with this method.  It's a guess and check game, many
	 * sound ids don't work.  One good way to find interesting numbers is to make a button
	 * cycle through all the possible values, then write down ones you like.  Here is code
	 * you could use as a button handler.  Note that mMediaIdCounter needs to be created
	 * and set to 0 as a member variable.  Here mTts is an instance of TextToSpeechHelper.
	 *
	 *	public void handlePlayNext(View view) {
	 *		mTts.stop();
	 *		mMediaIdCounter++;
	 *		mTts.playRingtone(mMediaIdCounter);
	 *		mSomeTextView.setText("Playing id = " + mMediaIdCounter);
	 *   }
	 *
	 * @param mediaFileId Guess for a valid file number.  You can use the playValidRingtone
	 * 		function to learn one valid number, then guess a bit higher or lower to try more.
	 */
	public void playRingtone(int mediaFileId) {
		try {
			Uri ringtoneUri = Uri.parse("content://media/internal/audio/media/" + mediaFileId);
   	        Ringtone r = RingtoneManager.getRingtone(mMainActivity, ringtoneUri);
			r.play();
   	        mPlayingRingtones.add(r);
		} catch (Exception e) {
		}
	}
	

	/**
	 * Stops all ringtones.
	 */
	public void stopRingtone() {
		for (Ringtone r : mPlayingRingtones) {
			if(r.isPlaying()) {
				r.stop();
			}
		}
		removeStoppedRingtones();
	}
	
	
	/**
	 * Helper to remove ringtones that have already stopped.
	 */
	private void removeStoppedRingtones() {
		Set<Ringtone> playingRingtones = new HashSet<Ringtone>();
		for (Ringtone r : mPlayingRingtones) {
			if (r.isPlaying()) {
				playingRingtones.add(r);
			}
		}
		mPlayingRingtones = playingRingtones;
	}

	// ------------- OnInitListener method ----------------------------
	/**
	 * Method called when the TextToSpeech engine starts to determine if it is
	 * successful.
	 */
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = mTts.setLanguage(Locale.US);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e(TAG, "This Language is not supported");
			} else {
				Log.d(TAG, "TTS Ready");
			}
		} else {
			Log.e(TAG, "TTS Initilization Failed!");
		}
	}
}
