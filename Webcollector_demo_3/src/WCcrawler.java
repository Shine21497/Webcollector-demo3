import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.jsoup.nodes.Document;

/**
 * Crawling news from hfut news
 * 
 * tongji news is hard to crawl because it will ban your ip if you crawl too frequently
 *
 * 
 */
public class WCcrawler extends BreadthCrawler {
    
    public WCcrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        /*add 10 start pages and set their type to "list"
          "list" is not a reserved word, you can use other string instead
         */
        for(int i = 2; i <= 10; i++) {
            //this.addSeed("http://news.tongji.edu.cn/classid-5-" + i + ".html","list");
        	this.addSeed("http://news.hfut.edu.cn/list-1-" + i + ".html", "list");
        }

        setThreads(50);
        getConf().setTopN(100);


//        setResumable(true);
    }
    @Override
    public Page getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest(crawlDatum);
        request.setUserAgent(Useragnets.getuseragent());
        return request.responsePage();
    }
    @Override
    public void visit(Page page, CrawlDatums next) {
        String url = page.url();

        if (page.matchType("list")) {
            /*if type is "list"*/
            /*detect content page by css selector and mark their types as "content"*/
        	//System.out.println("URL:\n" + url);
            //next.add(page.links("div[class='news_list'] li>a")).type("content");
        	 next.add(page.links("div[class=' col-lg-8 '] li>a")).type("content");
        }else if(page.matchType("content")) {
            /*if type is "content"*/
            /*extract title and content of news by css selector*/
            /*String title = page.select("h1[id=news_title]").first().text();
            String content = page.selectText("div[class=news_content]", 0);*/
        	String title = page.select("div[id=Article]>h2").first().text();
            String content = page.selectText("div#artibody", 0);

            //read title_prefix and content_length_limit from configuration
            title = getConf().getString("title_prefix") + title;
            content = content.substring(0, getConf().getInteger("content_length_limit"));

            System.out.println("URL:\n" + url);
            System.out.println("title:\n" + title);
            System.out.println("content:\n" + content);
        }

    }

    public static void main(String[] args) throws Exception {
        WCcrawler crawler = new WCcrawler("crawl", false);

        crawler.getConf().setExecuteInterval(5000);

        crawler.getConf().set("title_prefix","PREFIX_");
        crawler.getConf().set("content_length_limit", 20);
        /*start crawl with depth of 4*/
        crawler.start(4);
    }

}