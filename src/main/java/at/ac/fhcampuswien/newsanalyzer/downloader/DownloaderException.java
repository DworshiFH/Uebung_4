package at.ac.fhcampuswien.newsanalyzer.downloader;

public class DownloaderException extends Exception{
    public DownloaderException(String errorMessage){
        super(errorMessage);
    }
}
