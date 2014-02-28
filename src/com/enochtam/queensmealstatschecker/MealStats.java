package com.enochtam.queensmealstatschecker;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MealStats {
    private String rawHtml;

    private String name;
    private double myFunds;
    private double myFundsBonus;
    private double amp;
    private double ampBonus;
    private double additionalAmp;

    public ArrayList<NameValuePair> mealData = new ArrayList<NameValuePair>();


    public MealStats(String html){
        this.rawHtml=html;

    }
    public void parseHtml(){
        Document doc = Jsoup.parse(rawHtml);

        Elements name = doc.select("h3[class$=greeting]");
        this.name = name.text().replace("Welcome ","");

        Elements tr = doc.select("tr");
        for(Element row : tr){
            //System.out.println(row);
            Element first = row.select("td").first();
            if (first != null) {
                //System.out.println(first.text());
                if (first.text().contains("My Funds Bonus")){
                    Element myFundsBonus = row.select("td").get(1);
                    this.myFundsBonus = Double.parseDouble(myFundsBonus.text().replace("$",""));
                }else if( first.text().contains("My Funds") ){
                    Element myFunds = row.select("td").get(1);
                    this.myFunds = Double.parseDouble(myFunds.text().replace("$",""));
                }else if ( first.text().contains("AMP Bonus") ){
                    Element ampBonus = row.select("td").get(1);
                    this.ampBonus = Double.parseDouble(ampBonus.text().replace("$",""));
                }else if ( first.text().contains("Additional AMP Purchase") ){
                    Element additionalAmp = row.select("td").get(1);
                    this.additionalAmp = this.additionalAmp + Double.parseDouble(additionalAmp.text().replace("$",""));
                }else if ( first.text().contains("AMP") ){
                    Element amp = row.select("td").get(1);
                    this.amp = Double.parseDouble(amp.text().replace("$",""));
                }

            }
        }// end for loop

        boolean atTableFlag=false;
        Elements tables = doc.select("table");
        for(Element thisTable : tables){
            Elements tableRows = thisTable.select("tr");

            if(atTableFlag){
                Element shouldHaveWeekly = tableRows.first().select("td").get(1);
                if(shouldHaveWeekly.text().contains("Weekly ")){
                    for(Element thisRow:tableRows){
                        Element cell1 = thisRow.select("td").first();
                        Element cell2 = thisRow.select("td").get(1);
                        mealData.add(new BasicNameValuePair(cell1.text(), cell2.text()));

                    }
                }
                atTableFlag=false;
            }

            Element firstCell = tableRows.first().select("td").first();
            if(firstCell!=null){
                if(firstCell.text().contains("Usage")){
                    atTableFlag=true;
                }
            }

        }// end for loop

    } // end parseHtml

    public double getTotalFlex(){
        return amp+additionalAmp+ampBonus; //TODO make sure cents don't go over 100
    }
    public double getTotalDining(){
        return myFunds+myFundsBonus; //TODO make sure cents don't go over 100
    }
    public String toString(){
        return "Name: "+ name+"\nmyFunds: "+myFunds+"\nmyFundsBonus: "+myFundsBonus+"\namp: "+amp+"\nampBonus: "+ampBonus+"\nadditionalAmp: "+additionalAmp;
    }
}
