package at.ac.fhcampuswien.newsanalyzer.downloader;

import java.util.List;

public class SequentialDownloader extends Downloader {

    private long downloadTime;

    @Override
    public int process(List<String> urls) throws DownloaderException {

        long start = System.nanoTime();
        int count = 0;

        for (String url : urls) {
            String fileName = null;
            if(url != null && !url.equals("null") && !url.equals("")) fileName = saveUrl2File(url);
            if(fileName != null) count++;
        }
        long end = System.nanoTime();

        this.downloadTime = (end-start);

        return count;
    }

    public long getDownloadTime(){
        return this.downloadTime;
    }
}
