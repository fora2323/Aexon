package com.aexon.theme.hct;

import androidx.annotation.ColorInt;

public class AexonQuantizerCompat {
	@ColorInt
	public static int extractDominantColor(int[] pixels, @ColorInt int fallbackColor) {
		java.util.Map<Integer, Integer> colorCounts = new java.util.HashMap<>();
		java.util.Map<Integer, long[]> colorSums = new java.util.HashMap<>();
		
		for (int pixel : pixels) {
			int alpha = (pixel >> 24) & 0xff;
			if (alpha < 240) continue;
			
			int r = (pixel >> 16) & 0xff;
			int g = (pixel >> 8) & 0xff;
			int b = pixel & 0xff;
			
			int groupedColor = (0xff << 24) | ((r & 0xF0) << 16) | ((g & 0xF0) << 8) | (b & 0xF0);
			colorCounts.put(groupedColor, colorCounts.getOrDefault(groupedColor, 0) + 1);
			
			long[] sums = colorSums.get(groupedColor);
			if (sums == null) {
				sums = new long[]{0, 0, 0};
				colorSums.put(groupedColor, sums);
			}
			sums[0] += r;
			sums[1] += g;
			sums[2] += b;
		}
		
		if (colorCounts.isEmpty()) return fallbackColor;
		
		int bestColor = fallbackColor;
		double maxScore = -1.0;
		
		for (java.util.Map.Entry<Integer, Integer> entry : colorCounts.entrySet()) {
			int group = entry.getKey();
			int count = entry.getValue();
			long[] sums = colorSums.get(group);
			if (sums == null) continue;
			
			int avgR = (int) (sums[0] / count);
			int avgG = (int) (sums[1] / count);
			int avgB = (int) (sums[2] / count);
			int avgColor = (0xff << 24) | (avgR << 16) | (avgG << 8) | avgB;
			
			AexonHctCompat hct = AexonHctCompat.fromInt(avgColor);
			double chroma = hct.getChroma();
			if (chroma < 15.0 && colorCounts.size() > 1) continue;
			
			double populationFraction = (double) count / pixels.length;
			double score = (chroma * 2.0) + (populationFraction * 100.0);
			if (score > maxScore) {
				maxScore = score;
				bestColor = avgColor;
			}
		}
		
		if (maxScore == -1.0) {
			int maxCount = 0;
			for (java.util.Map.Entry<Integer, Integer> entry : colorCounts.entrySet()) {
				int group = entry.getKey();
				int count = entry.getValue();
				if (count > maxCount) {
					maxCount = count;
					long[] sums = colorSums.get(group);
					if (sums == null) continue;
					bestColor = (0xff << 24) | (((int) (sums[0] / maxCount)) << 16) | (((int) (sums[1] / maxCount)) << 8) | (int) (sums[2] / maxCount);
				}
			}
		}
		return bestColor;
	}
}