package edu.rosehulman.me435;

import java.util.List;

import root.gast.speech.RecognizerIntentFactory;
import android.util.Log;
import android.widget.Toast;

public class SpeechAccessoryActivity extends AccessoryActivity {

	private static final String TAG = SpeechAccessoryActivity.class.getSimpleName();
	private static final String KEYWORD_ANGLE = "angle";
	private static final String KEYWORD_DISTANCE = "distance";
	private static final String KEYWORD_NEGATIVE = "negative";
	private static final int DEFAULT_DISTANCE = 10;
	protected String mRobotName = null;

	/**
	 * Set the name of your robot for voice commands.
	 * 
	 * @param robotName
	 *            Name given to your robot.
	 */
	protected void setRobotName(String robotName) {
		mRobotName = robotName.toLowerCase();
	}

	/**
	 * Start listening for a voice command.
	 * 
	 * @param prompt
	 *            Text show to the user in the listen box.
	 */
	protected void startListening(String prompt) {
		recognize(RecognizerIntentFactory.getSimpleRecognizerIntent(prompt));
	}

	@Override
	protected void speechNotAvailable() {
		Toast.makeText(this, "speechNotAvailable", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void directSpeechNotAvailable() {
		Toast.makeText(this, "speechNotAvailable", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void languageCheckResult(String languageToUse) {
		Log.d(TAG, "languageCheckResult");
	}


	@Override
	protected void receiveWhatWasHeard(List<String> heard,
			float[] confidenceScores) {
		for (String command : heard) {
			command = command.toLowerCase();
			if (mRobotName == null || command.contains(mRobotName)) {
				// We have a command for this robot.
				boolean negativeAngle = false, negativeDistance = false;
				if (command.contains(KEYWORD_ANGLE)) {
					// We have an angle command.
					int[] numericValues = new int[2];
					String wordAfterAngle = getNextWord(command, KEYWORD_ANGLE);
					if (wordAfterAngle.equalsIgnoreCase(KEYWORD_NEGATIVE)) {
						negativeAngle = true;
						wordAfterAngle = getNextWord(command, KEYWORD_NEGATIVE);
					}
					if (convertWordToInt(wordAfterAngle, numericValues, 0)) {
						// Got an angle next get a distance if available.
						numericValues[1] = DEFAULT_DISTANCE;
						if (command.contains(KEYWORD_DISTANCE)) {
							String wordAfterDistance = getNextWord(command,
									KEYWORD_DISTANCE);
							if (wordAfterDistance
									.equalsIgnoreCase(KEYWORD_NEGATIVE)) {
								negativeDistance = true;
								wordAfterDistance = getNextWord(
										command.substring(command
												.indexOf(KEYWORD_DISTANCE)),
										KEYWORD_NEGATIVE);
							}
							convertWordToInt(wordAfterDistance, numericValues, 1);							
						}
						int angle = negativeAngle ? -numericValues[0]
								: numericValues[0];
						int distance = negativeDistance ? -numericValues[1]
								: numericValues[1];
						onVoiceCommand(angle, distance);
						break;
					}
				}
			}
		}
	}

	/**
	 * Helper method to get the next word, which is hopefully a number.
	 * For example after the word "angle" you hope the next word is a number.
	 * 
	 * @param command Entire command heard.
	 * @param keyword Keyword ("angle" or "distance") to look after.
	 * @return The String after the keyword.  "" if none is found.
	 */
	private String getNextWord(String command, String keyword) {
		String nextWord = "";
		int startIndex = command.indexOf(keyword) + keyword.length() + 1;
		if (command.indexOf(keyword) < 0 || startIndex >= command.length()) {
			// There is no keyword or no word after the keyword.
			return "";
		}
		int endIndex = command.indexOf(" ", startIndex);
		if (endIndex < 0) {
			nextWord = command.substring(startIndex);
		} else {
			nextWord = command.substring(startIndex, endIndex);
		}
		return nextWord;
	}

	/**
	 * Converts a string to a number.
	 * 
	 * @param potentialNumber String the is hopefully a number.
	 * @param numericValues An array to populate with the number.
	 * @param index The array index to populate with the number.
	 * @return True if the String is a number.
	 */
	private boolean convertWordToInt(String potentialNumber,
			int[] numericValues, int index) {
		try {
			numericValues[index] = Integer.parseInt(potentialNumber);
		} catch (NumberFormatException e) {
			// Wasn't a number, see if it's a written out single digit word.
			if (!isSignalDigitWord(potentialNumber, numericValues, index)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Helper method to convert words like "one" into an int.
	 * Uses a brute force approach to convert common words to ints.
	 *
	 * @param potentialNumber String that might be a written out number.
	 * @param numericValues An array to populate with the number.
	 * @param index The array index to populate with the number.
	 * @return True if the String is a number.
	 */
	private boolean isSignalDigitWord(String potentialNumber,
			int[] numericValues, int index) {
		if (potentialNumber.equalsIgnoreCase("zero")) {
			numericValues[index] = 0;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("one")) {
			numericValues[index] = 1;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("two") || potentialNumber.equalsIgnoreCase("to") || potentialNumber.equalsIgnoreCase("too")) {
			numericValues[index] = 2;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("three")) {
			numericValues[index] = 3;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("four") || potentialNumber.equalsIgnoreCase("for")) {
			numericValues[index] = 4;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("five")) {
			numericValues[index] = 5;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("six")) {
			numericValues[index] = 6;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("seven")) {
			numericValues[index] = 7;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("eight") || potentialNumber.equalsIgnoreCase("ate")) {
			numericValues[index] = 8;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("nine")) {
			numericValues[index] = 9;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("ten")) {
			numericValues[index] = 10;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("twenty")) {
			numericValues[index] = 20;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("thirty")) {
			numericValues[index] = 30;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("forty")) {
			numericValues[index] = 40;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("fifty")) {
			numericValues[index] = 50;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("sixty")) {
			numericValues[index] = 60;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("seventy")) {
			numericValues[index] = 70;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("eighty")) {
			numericValues[index] = 80;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("ninety")) {
			numericValues[index] = 90;
			return true;
		} else if (potentialNumber.equalsIgnoreCase("hundred")) {
			numericValues[index] = 100;
			return true;
		}
		return false;
	}

	/**
	 * This method is called if a valid voice command is received.
	 * Subclasses of this activity should override this method so
	 * that they can actually do something with the command.
	 * 
	 * @param angle Angle heard in voice command.
	 * @param distance Distance heard in voice command.
	 */
	protected void onVoiceCommand(int angle, int distance) {
		Log.d(TAG, "Voice command for angle " + angle + " distance " + distance);
	}

	@Override
	protected void recognitionFailure(int errorCode) {
		Toast.makeText(this, "recognitionFailure", Toast.LENGTH_SHORT).show();
	}

}
