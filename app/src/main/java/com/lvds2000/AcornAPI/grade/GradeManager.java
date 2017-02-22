package com.lvds2000.AcornAPI.grade;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * @author Lester Lyu
 *
 */
public class GradeManager {

	private OkHttpClient client;

	public GradeManager(OkHttpClient client){
		this.client = client;
	}

    /**
     * Please use bootstrap css
     * @return the html representation of the grades, normally two tables
     */
	public String getGradeHtml(){
		// https://acorn.utoronto.ca/sws/transcript/academic/main.do?main.dispatch&mode=complete
		final String res[] = new String[1];
		res[0] = "";
        final boolean isFinished[] = new boolean[1];
        isFinished[0] = false;
		Request request = new Request.Builder()
				.url("https://acorn.utoronto.ca/sws/transcript/academic/main.do?main.dispatch")
				.get()
				.build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
                Log.e("GradeManager", "Unable to get grade.");
            }

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String html = response.body().string();
				Document doc = Jsoup.parse(html);
                doc.getElementsByClass("recentAcademicHistoryRight").remove();
				Elements elements = doc.getElementsByClass("section academic-history xs-block-table");
                System.out.println("table numbers: " + elements.size());
				for(int i = 0; i < elements.size(); i++){
					res[0] += elements.get(i).outerHtml().replaceAll("section academic-history xs-block-table",
                            "section academic-history xs-block-table table table-hover table-condensed");
                    //System.out.println(elements.get(i).outerHtml());
                }
                isFinished[0] = true;
			}
		});
		long prev = System.currentTimeMillis();
		while(System.currentTimeMillis() - prev < 15000 && !isFinished[0]){

		}
        return (!isFinished[0]) ? "N/A" : res[0];
	}



}
