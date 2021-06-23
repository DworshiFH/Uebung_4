package at.ac.fhcampuswien.newsanalyzer.ui;


import at.ac.fhcampuswien.newsanalyzer.ctrl.Controller;
import at.ac.fhcampuswien.newsapi.beans.Article;
import at.ac.fhcampuswien.newsapi.beans.NewsResponse;
import at.ac.fhcampuswien.newsapi.enums.Category;
import at.ac.fhcampuswien.newsapi.enums.Country;
import at.ac.fhcampuswien.newsapi.enums.SortBy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;

public class UserInterface {

	Scanner scanner = new Scanner(System.in);

	private Controller ctrl = new Controller();

	public void getDataFromCtrl1() {
		ctrl.setKeyword("virus");
		ctrl.setCountry(Country.at);
		ctrl.setCategory(Category.health);
		ctrl.setSortBy(SortBy.RELEVANCY);
		ctrl.process();

		NewsResponse newsResponse = ctrl.getData();

		if(newsResponse != null){
			List<Article> articles = newsResponse.getArticles();
			articles.stream().forEach(article -> System.out.println(article.toString()));
		}
	}

	public void getDataFromCtrl2() {
		ctrl.setKeyword("Sport");
		ctrl.setCountry(Country.at);
		ctrl.setCategory(Category.sports);
		ctrl.setSortBy(SortBy.RELEVANCY);
		ctrl.process();

		NewsResponse newsResponse = ctrl.getData();

		if(newsResponse != null){
			List<Article> articles = newsResponse.getArticles();
			articles.stream().forEach(article -> System.out.println(article.toString()));
		}

		//ctrl.analysis(newsResponse);
	}

	public void getDataFromCtrl3(){
		ctrl.setKeyword("Aktien");
		ctrl.setCountry(Country.at);
		ctrl.setCategory(Category.business);
		ctrl.setSortBy(SortBy.RELEVANCY);
		ctrl.process();

		NewsResponse newsResponse = ctrl.getData();

		if(newsResponse != null){
			List<Article> articles = newsResponse.getArticles();
			articles.stream().forEach(article -> System.out.println(article.toString()));
		}

		//ctrl.analysis(newsResponse);
	}

	public void getDataForCustomInput() {
		// TODO implement me
		boolean categorySet = false;
		boolean analysisSet = false;
		boolean countrySet = false;

		//Choose Country
		while(!countrySet){
			System.out.print("Bitte geben Sie das Land ein aus dem Sie News erhalten möchten (Format: z.B. at...Österreich, us...USA): ");
			String s = scanner.next();
			countrySet=countrySelector(s);
			System.out.println();
		}

		//Choose Category
		while(!categorySet){
			System.out.println("Wählen Sie eine der folgenden Kategorien: ");
			System.out.print(
					"b: Business\n"+
							"u: Unterhaltung\n"+
							"g: Gesundheit\n"+
							"w: Wissenschaft\n"+
							"t: Technologie\n"+
							"> ");

			String s = scanner.next();
			switch(s){
				case "b": ctrl.setCategory(Category.business); categorySet=true; break;
				case "u": ctrl.setCategory(Category.entertainment); categorySet=true; break;
				case "g": ctrl.setCategory(Category.health); categorySet=true; break;
				case "w": ctrl.setCategory(Category.science); categorySet=true; break;
				case "t": ctrl.setCategory(Category.technology); categorySet=true; break;
				default:
					System.out.println();
					System.out.println("Ungültige Eingabe"); break;
			}
			System.out.println();
		}

		//Enter Keyword
		System.out.print("Geben Sie ein Schlüsselwort ein: ");
		String s2 = scanner.next();
		ctrl.setKeyword(s2);
		System.out.println();

		System.out.println("Hier Ihre Nachrichten in Österreich:");

		ctrl.process();

		NewsResponse newsResponse= ctrl.getData();

		if(newsResponse != null){
			List<Article> articles = newsResponse.getArticles();
			articles.stream().forEach(article -> System.out.println(article.toString()));
		}

		while(!analysisSet){
			System.out.print("\nMöchten Sie unseren T.O.P.-Algorithmus verwenden um Ihre Nachrichten zu analysieren? (j/n): ");
			String s3 = scanner.next();
			switch(s3){
				case "j": ctrl.analysis(newsResponse); analysisSet=true; break;
				case "n": analysisSet=true; break;
				default: System.out.println("Ungültige Eingabe"); break;
			}
			System.out.println();
		}
	}

	public void start() {
		try{
			Menu<Runnable> menu = new Menu<>("User Interface");
			menu.setTitle("Wählen Sie aus:");
			menu.insert("a", "Top Nachrichten zu COVID-19 in Österreich", this::getDataFromCtrl1);
			menu.insert("b", "Alle News zu Sport in Österreich", this::getDataFromCtrl2);
			menu.insert("c", "Die aktuellsten Nachrichten zu Business und Aktien in Österreich", this::getDataFromCtrl3);
			menu.insert("d", "Eigene Eingabe",this::getDataForCustomInput);
			menu.insert("e", "Letzte Suche herunterladen. (sequentiell) ",this::downloadArticlesSequential);
			menu.insert("f", "Letzte Suche herunterladen. (threaded) ",this::downloadArticlesThreaded);
			menu.insert("g", "Downloadzeiten vergleichen ",this::calculateTimeDifference);
			menu.insert("q", "Programm Beenden", null);
			Runnable choice;
			while ((choice = menu.exec()) != null) {
				choice.run();
			}
			System.out.println("Program finished");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private long seqProcTime=0;
	public void downloadArticlesSequential(){
		ctrl.downloadSequential();
		seqProcTime = ctrl.getSeqDLTime();
	}

	private long thrProcTime=0;
	public void downloadArticlesThreaded(){
		ctrl.downloadThreaded();
		thrProcTime = ctrl.getThrDLTime();
	}

	public void calculateTimeDifference(){
		if(seqProcTime == 0 || thrProcTime == 0 ) System.out.println("Bitte beide Methoden zum Downloaden benutzen!");

		DecimalFormat df4 = new DecimalFormat("#.####");
		df4.setRoundingMode(RoundingMode.DOWN);

		if(thrProcTime < seqProcTime){
			float time = (float)seqProcTime/thrProcTime;
			System.out.println("Die Downloadzeit mit Threads war " + df4.format(time) + " mal schneller");
		} else {
			float time = (float)thrProcTime/seqProcTime;
			System.out.println("Die Downloadzeit ohne Threads war " + df4.format(time) + " mal schneller");

		}


	}

	protected String readLine() {
		String value = "\0";
		BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
		try {
			value = inReader.readLine();
		} catch (IOException ignored) {
		}
		return value.trim();
	}

	protected Double readDouble(int lowerlimit, int upperlimit) 	{
		Double number = null;
		while (number == null) {
			String str = this.readLine();
			try {
				number = Double.parseDouble(str);
			} catch (NumberFormatException e) {
				number = null;
				System.out.println("Please enter a valid number:");
				continue;
			}
			if (number < lowerlimit) {
				System.out.println("Please enter a higher number:");
				number = null;
			} else if (number > upperlimit) {
				System.out.println("Please enter a lower number:");
				number = null;
			}
		}
		return number;
	}

	private boolean countrySelector(String country){
		switch (country) {
			case "ar" :	ctrl.setCountry(Country.ar); break;
			case "au" :	ctrl.setCountry(Country.au); break;
			case "at" :	ctrl.setCountry(Country.at); break;
			case "be" :	ctrl.setCountry(Country.be); break;
			case "br" :	ctrl.setCountry(Country.br); break;
			case "bg" :	ctrl.setCountry(Country.bg); break;
			case "ca" :	ctrl.setCountry(Country.ca); break;
			case "cn" :	ctrl.setCountry(Country.cn); break;
			case "co" :	ctrl.setCountry(Country.co); break;
			case "cu" :	ctrl.setCountry(Country.cu); break;
			case "cz" :	ctrl.setCountry(Country.cz); break;
			case "eg" :	ctrl.setCountry(Country.eg); break;
			case "fr" :	ctrl.setCountry(Country.fr); break;
			case "de" :	ctrl.setCountry(Country.de); break;
			case "gr" :	ctrl.setCountry(Country.gr); break;
			case "hk" :	ctrl.setCountry(Country.hk); break;
			case "hu" :	ctrl.setCountry(Country.hu); break;
			case "in" :	ctrl.setCountry(Country.in); break;
			case "id" :	ctrl.setCountry(Country.id); break;
			case "ie" :	ctrl.setCountry(Country.ie); break;
			case "il" :	ctrl.setCountry(Country.il); break;
			case "it" :	ctrl.setCountry(Country.it); break;
			case "jp" :	ctrl.setCountry(Country.jp); break;
			case "lv" :	ctrl.setCountry(Country.lv); break;
			case "lt" :	ctrl.setCountry(Country.lt); break;
			case "my" :	ctrl.setCountry(Country.my); break;
			case "mx" :	ctrl.setCountry(Country.mx); break;
			case "ma" :	ctrl.setCountry(Country.ma); break;
			case "nl" :	ctrl.setCountry(Country.nl); break;
			case "nz" :	ctrl.setCountry(Country.nz); break;
			case "ng" :	ctrl.setCountry(Country.ng); break;
			case "no" :	ctrl.setCountry(Country.no); break;
			case "ph" :	ctrl.setCountry(Country.ph); break;
			case "pl" :	ctrl.setCountry(Country.pl); break;
			case "pt" :	ctrl.setCountry(Country.pt); break;
			case "ro" :	ctrl.setCountry(Country.ro); break;
			case "ru" :	ctrl.setCountry(Country.ru); break;
			case "sa" :	ctrl.setCountry(Country.sa); break;
			case "rs" :	ctrl.setCountry(Country.rs); break;
			case "sg" :	ctrl.setCountry(Country.sg); break;
			case "sk" :	ctrl.setCountry(Country.sk); break;
			case "si" :	ctrl.setCountry(Country.si); break;
			case "za" :	ctrl.setCountry(Country.za); break;
			case "kr" :	ctrl.setCountry(Country.kr); break;
			case "se" :	ctrl.setCountry(Country.se); break;
			case "ch" :	ctrl.setCountry(Country.ch); break;
			case "tw" :	ctrl.setCountry(Country.tw); break;
			case "th" :	ctrl.setCountry(Country.th); break;
			case "tr" :	ctrl.setCountry(Country.tr); break;
			case "ae" :	ctrl.setCountry(Country.ae); break;
			case "ua" :	ctrl.setCountry(Country.ua); break;
			case "gb" :	ctrl.setCountry(Country.gb); break;
			case "us" :	ctrl.setCountry(Country.us); break;
			case "ve" :	ctrl.setCountry(Country.ve); break;
			default:
				System.out.println("Falsches Format.");
				return false;
		}
		return true;
	}
}
