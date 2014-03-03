package br.com.tap4mobile.airhockey.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.Resources;

public class TextResourceReader {
	public static String readTextFileResource(Context context, int resourceId){

		StringBuilder body = new StringBuilder();

		try {

			InputStream inputStream = context.getResources().openRawResource(resourceId);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String nextLine;

			while ((nextLine = bufferedReader.readLine()) != null) {
				body.append(nextLine).append("\n");
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not Open resource: "+ resourceId, e);
		} catch (Resources.NotFoundException e) {
			throw new RuntimeException("Could not found: "+ resourceId, e);
		}


		return body.toString();
	}
}
