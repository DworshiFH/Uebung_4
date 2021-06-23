package at.ac.fhcampuswien.newsanalyzer.downloader;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelDownloader extends Downloader{
    private long downloadTime;
    private int numWorkers = Runtime.getRuntime().availableProcessors();
    ExecutorService pool = Executors.newFixedThreadPool(numWorkers);

    @Override
    public int process(List<String> urls) throws ExecutionException, InterruptedException {
        long start = System.nanoTime();
        int count = 0;

        for(String url : urls) {
            try{
                Future<String> future;
                if(!url.equals("") && !url.equals("null")){
                    future = pool.submit( () -> saveUrl2File(url));
                    if(future.get() != null) count++;
                }
            }catch(NullPointerException | InterruptedException e){
                System.out.println(e.getMessage());
            }
        }
        long end = System.nanoTime();
        this.downloadTime = (end-start);
        return count;
    }
    public long getDownloadTime(){
        return this.downloadTime;
    }
}
