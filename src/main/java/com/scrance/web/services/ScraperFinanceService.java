package com.scrance.web.services;
import com.scrance.web.models.ConsoleColors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

@Service
public class ScraperFinanceService {

    public void dummyMethod() {
        // Your task logic goes here
        System.out.println("Scheduled task executed at: " + new Date());
        getDataFromFinance(new String[] {"AAPL", "REP.MC", "GOOG"});
    }

    public  void getDataFromFinance (String[] stockListSymbol) {
        try {
            JSONArray result = new JSONArray();
            for (String stockSymbol: stockListSymbol ){
                String url = "https://finance.yahoo.com/quote/"+stockSymbol+"/options?p="+stockSymbol;
                Document document = Jsoup.connect(url).userAgent("Chrome/41.0.2228.0").get();
                Element finStreamer = document.select("div#quote-header-info").first();
                Element finStreamer2 = finStreamer != null ? finStreamer.select("fin-streamer").first() : null;
                Elements temp = document.select("tr");

                JSONObject json = new JSONObject();
                json.put("shortName", stockSymbol);
                json.put("symbol", stockSymbol);
                json.put("longName", finStreamer2 != null ? finStreamer.select("h1").first().childNodes().get(0): null);
                json.put("marketState", "As of  02:19PM EST. Market open");
                json.put("regularMarketTime", "1");
                json.put("regularMarketPrice", finStreamer2 != null ? finStreamer2.attr("value"): null);
                json.put("regularMarketDayHigh", "186.19");
                json.put("regularMarketDayLow", "186.19");
                json.put("regularMarketChange", "186.19");
                json.put("regularMarketChangePercent", "186.19");
                result.put(json);
            }
            print(result);
        } catch (IOException ex) {
            int cOverp = 0 ;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //return null;
    }

    private static void print(JSONArray jsonArrObject) throws JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append("\033[H\033[2J");
        sb.append(String.format(ConsoleColors.CYAN_UNDERLINED+"%-15s%10s%10s%11s%10s%15s%12s %-20s\033[0m\n", "Name", "Symbol", "Price", "Diff", "Percent", "Delay", "MarketState", "Long Name"));

        long currentTimeMillis = System.currentTimeMillis();

        for (int i = 0; i < jsonArrObject.length(); i++) {
            JSONObject data = jsonArrObject.getJSONObject(i);

            String shortName = data.optString("shortName");
            shortName = shortName.length()>14?shortName.substring(0, 14):shortName;

            String longName = data.optString("longName", shortName);
            String symbol = data.optString("symbol");
            String marketState = data.optString("marketState");
            long regularMarketTime = data.optLong("regularMarketTime");

            double regularMarketPrice = data.optDouble("regularMarketPrice");
            double regularMarketDayHigh = data.optDouble("regularMarketDayHigh");
            double regularMarketDayLow = data.optDouble("regularMarketDayLow");
            double regularMarketChange = data.optDouble("regularMarketChange");
            double regularMarketChangePercent = data.optDouble("regularMarketChangePercent");

            String color = regularMarketChange==0?"":regularMarketChange>0?ConsoleColors.GREEN_BOLD_BRIGHT:ConsoleColors.RED_BOLD_BRIGHT;

            sb.append(String.format("%-15s", shortName));
            sb.append(String.format("%10s", symbol));

            if(regularMarketDayHigh == regularMarketPrice || regularMarketDayLow == regularMarketPrice) {
                sb.append(String.format(ConsoleColors.WHITE_BOLD+color+"%10.2f"+ConsoleColors.RESET, regularMarketPrice));
            } else {
                sb.append(String.format(ConsoleColors.WHITE_BOLD+"%10.2f"+ConsoleColors.RESET, regularMarketPrice));
            }

            sb.append(String.format(color+"%11s"+ ConsoleColors.RESET, String.format("%.2f", regularMarketChange)+" "+(regularMarketChange>0?"▲":regularMarketChange<0?"▼":"-")));
            sb.append(String.format(color+"%10s"+ConsoleColors.RESET, String.format("(%.2f%%)", regularMarketChangePercent)));
            sb.append(String.format("%15s", prettyTime(currentTimeMillis-(regularMarketTime*1000))));
            sb.append(String.format("%12s", marketState));
            sb.append(String.format(" %-20s\n", longName));
        }

        System.out.print(sb);
        //Mail m = new Mail();
        //m.sendMail(sb.toString(), "Nuevas notificaciones en tus Bolsa");
    }

    private static String prettyTime(long millis) {
        return String.format("%dm, %ds",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }
}
