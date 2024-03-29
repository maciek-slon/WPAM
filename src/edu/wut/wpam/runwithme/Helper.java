package edu.wut.wpam.runwithme;

import java.util.ArrayList;
import android.graphics.PointF;

public class Helper {
	static String formatTime(float seconds) {
		int hh = (int) Math.floor(seconds / 3600);
		seconds = seconds - hh * 3600;
		int mm = (int) Math.floor(seconds / 60);
		seconds = seconds - mm * 60;
		int ss = (int) Math.floor(seconds);
		return String.format("%d:%02d:%02d", hh, mm, ss);
	}
	
	static String formatTimeNice(float seconds) {
		int hh = (int) Math.floor(seconds / 3600);
		seconds = seconds - hh * 3600;
		int mm = (int) Math.floor(seconds / 60);
		seconds = seconds - mm * 60;
		int ss = (int) Math.floor(seconds);
		
		if (hh > 0)
			return String.format("%dh %02dm %02ds", hh, mm, ss);
		else
			return String.format("%dm %02ds", mm, ss);
	}
	
	static ArrayList<PointF> calculateSpeed(ArrayList<TrackPoint> track, int skip) {
		if (track == null || track.size() == 0)
			return null;
		
		ArrayList<PointF> points = new ArrayList<PointF>();
		//long basetime = track.get(0).tim;
		long totaltime = 0;
		long curtime = 0;
		float dist = 0;
		TrackPoint last = track.get(0);
		
		for (int i = 1; i < track.size(); i += skip) {
			dist = 0;
			curtime = 0;
			for (int j = i; (j < i + skip) && (j < track.size()); ++j) {
				TrackPoint cur = track.get(j);
				if (cur.tim - last.tim < 0) break;
				dist += last.distanceTo(cur);
				curtime += cur.tim - last.tim;
				last = track.get(j);
			}
			if (curtime <= 0) break;
			totaltime += curtime;
			float spd = 3.6f * dist / (0.001f * curtime);
			points.add(new PointF(0.001f * totaltime, spd));
		}
		
		return points;
	}

	public static TrackInfo calculateElev(ArrayList<TrackPoint> track,
			int skip) {
		
		TrackInfo ret = new TrackInfo();
		
		ret.asc = 0.0f;
		ret.desc = 0.0f;
		ret.elev_profile = null;
		
		if (track == null || track.size() == 0)
			return ret;
		
		
		ArrayList<PointF> points = new ArrayList<PointF>();
		long totaltime = 0;
		long curtime = 0;
		float alt = 0;
		int cnt = 0;
		float last_elev = 0;
		TrackPoint last = track.get(0);
		
		for (int i = 1; i < track.size(); i += skip) {
			alt = 0;
			curtime = 0;
			cnt = 0;
			for (int j = i; (j < i + skip) && (j < track.size()); ++j) {
				TrackPoint cur = track.get(j);
				if (cur.tim - last.tim < 0) break;
				alt += cur.alt;
				cnt++;
				curtime += cur.tim - last.tim;
				last = track.get(j);
			}
			if (curtime <= 0) break;
			totaltime += curtime;
			float elev = alt / cnt;
			
			if (last_elev == 0)
				last_elev = elev;
			
			float de = elev - last_elev;
			if (de > 0)
				ret.asc += de;
			else
				ret.desc -= de;
			last_elev = elev;
		
			points.add(new PointF(0.001f * totaltime, elev));
		}
		
		ret.elev_profile = points;
		return ret;
	}
}
