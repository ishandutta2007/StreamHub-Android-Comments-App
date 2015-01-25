package com.livefyre.comments;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LFUtils {



	public static CharSequence trimTrailingWhitespace(CharSequence source) {

		if (source == null)
			return "";

		int i = source.length();

		// loop back to the first non-whitespace character
		while (--i >= 0 && Character.isWhitespace(source.charAt(i))) {
		}

		return source.subSequence(0, i + 1);
	}

	@SuppressLint("SimpleDateFormat")
	public static String getFormatedDate(String rawDate,Boolean flag) {
		Calendar currenttime = Calendar.getInstance();
		Calendar commentTime = Calendar.getInstance();

		long nextDateInMillis = currenttime.getTimeInMillis();
		long commentTimeInMillis = Long.parseLong(rawDate) * 1000;
		commentTime.setTimeInMillis(commentTimeInMillis);

		long timeDifferenceMilliseconds = nextDateInMillis
				- commentTimeInMillis;
		long diffSeconds = timeDifferenceMilliseconds / 1000;
		long diffMinutes = timeDifferenceMilliseconds / (60 * 1000);
		long diffHours = timeDifferenceMilliseconds / (60 * 60 * 1000);
		long diffDays = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24);
		// long diffWeeks = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 *
		// 7);
		// long diffMonths = (long) (timeDifferenceMilliseconds / (60 * 60 *
		// 1000 * 24 * 30.41666666));
		// long diffYears = (long) (timeDifferenceMilliseconds / (60 * 60 * 1000
		// * 24 * 365));

		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
		if (flag) {
			if (diffSeconds < 1) {
				return "less than a second";
			} else if (diffMinutes < 1) {
				return diffSeconds + "s";
			} else if (diffHours < 1) {
				return diffMinutes + "m";
			} else if (diffDays < 1) {
				return diffHours + "h";
			} else if (diffDays < 7) {
				return diffDays + "d";
			} else {
				return sdf.format(commentTime.getTime());
			}
		} else {
			if (diffSeconds < 1) {
				return "Posted Just Now";
			} else if (diffMinutes < 1) {
				if (diffSeconds == 1)
					return "Posted " +diffSeconds+" Second ago";
				else
					return "Posted " +diffSeconds+" Seconds ago";
				
				
			} else if (diffHours < 1) {
				if (diffMinutes == 1)
					return "Posted " +diffMinutes+" Month ago";
				else
					return "Posted " +diffMinutes+" Months ago";
			} else if (diffDays < 1) {
				if (diffHours == 1)
					return "Posted " +diffHours+" hour ago";
				else
					return "Posted " +diffHours+" hours ago";
			} else if (diffDays < 7) {
				if (diffDays == 1)
					return "Posted " +diffDays+" day ago";
				else
					return "Posted " +diffDays+" days ago";
			} else {
				
				
				
				return "Posted on "+sdf.format(commentTime.getTime());
			}
		}
	}

}
