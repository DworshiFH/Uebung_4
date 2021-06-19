package at.ac.fhcampuswien.newsanalyzer.ctrl;

import at.ac.fhcampuswien.newsanalyzer.downloader.Downloader;
import at.ac.fhcampuswien.newsapi.NewsApi;
import at.ac.fhcampuswien.newsapi.NewsApiBuilder;
import at.ac.fhcampuswien.newsapi.NewsApiException;
import at.ac.fhcampuswien.newsapi.beans.Article;
import at.ac.fhcampuswien.newsapi.beans.NewsResponse;
import at.ac.fhcampuswien.newsapi.enums.Category;
import at.ac.fhcampuswien.newsapi.enums.Country;
import at.ac.fhcampuswien.newsapi.enums.Endpoint;
import at.ac.fhcampuswien.newsapi.enums.SortBy;
import at.ac.fhcampuswien.newsanalyzer.downloader.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {

	public static final String APIKEY = "889f4de6f3bf4029b384f8c5a3ad8c56";

	private Country country;
	private String keyword;
	private Category category;
	private SortBy sortBy;

	private NewsResponse newsResponse;


	public void process() {

		NewsApi newsApi = new NewsApiBuilder()
				.setApiKey(APIKEY)
				.setQ(this.keyword) //				.setEndPoint(Endpoint.EVERYTHING)
				.setEndPoint(Endpoint.TOP_HEADLINES)
				.setFrom("2021-05-20")
				.setSourceCountry(this.country)
				.setSourceCategory(this.category)
				.setSortBy(this.sortBy)
				.createNewsApi();
		try{
			newsResponse = newsApi.getNews();
		} catch (NewsApiException e){
			System.out.println("Process could not be executed:\n" + e.getMessage());
		}
	}

	public void setSortBy(SortBy sortBy){
		this.sortBy = sortBy;
	}

	public void setKeyword(String keyword){
		this.keyword = keyword;
	}

	public void setCountry(Country country){
		this.country = country;
	}

	public void setCategory(Category category){
		this.category = category;
	}

	String authorWithShortestName;
	int numberOfArticles;
	List<String> titles = new ArrayList<>();
	Map<String, Integer> providers = new HashMap<>();

	public void analysis(NewsResponse newsResponse){

		if(newsResponse != null){

			System.out.println("\nHier die Ergebnisse unserer hochentwickelten Analysesoftware: \n");

			List<Article> articles = newsResponse.getArticles();

			//Number of Articles
			numberOfArticles = (int) articles.stream().count();
			System.out.println("Anzahl der Artikel: "+numberOfArticles);

			//Which provider delivers the most articles
			articles.stream().forEach(article -> {
				if(!providers.containsKey(article.getSource().getName())){
					providers.put(article.getSource().getName(), 1);
				} else {
					providers.put(article.getSource().getName(), providers.get(article.getSource().getName()) + 1);
				}
			});

			String providerWithMostArticles = providers.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
			System.out.println("Die Quelle mit den meisten Artikeln ist: "+providerWithMostArticles);

			//Author with Shortest Name
			authorWithShortestName = articles.get(0).getAuthor();
			articles.stream().forEach(article -> {
				if(article != null && !(article.getAuthor() == null) && article.getAuthor().length() < authorWithShortestName.length())
					authorWithShortestName = article.getAuthor();
			});
			System.out.println("Der Autor mit dem kürzesten Namen ist: "+authorWithShortestName);

			//sort for length of articles
			articles.stream().forEach(article -> {
				if(!titles.contains(article.getTitle()))
					titles.add(article.getTitle());
			});

			titles.sort((t1, t2) -> t2.length() - t1.length());
			System.out.println("Hier die Titel absteigend nach Länge sortiert:");
			titles.stream().forEach(System.out::println);
		}
	}

	public NewsResponse getData() {
		return newsResponse;
	}

	SequentialDownloader downloader = new SequentialDownloader();
	public void downloadArticles(){
		int count;
		try{
			count = downloader.process(
					newsResponse.getArticles().
							stream().map( Article::getUrl ).
							collect( Collectors.toList() )
			);
			System.out.println(count + " Aritkel wurden heruntergeladen.");
		} catch (DownloaderException e){
			System.out.println(e.getMessage());
		}
	}
}
















