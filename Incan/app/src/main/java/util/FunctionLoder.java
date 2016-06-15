
package util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FunctionLoder {

    private ExecutorService executorService;
    Context context;

    public FunctionLoder(Context context) {
        this.context = context;
        executorService = Executors.newFixedThreadPool(5);

    }

    @JavascriptInterface
    public void ShowTxt(String url, WebView webview, int size, boolean store) {
        queueTxt(url, webview);
    }

    //display image for home
    private void queueTxt(String url, WebView imageView) {
        try {
            TxtToLoad p = new TxtToLoad(url, imageView);
            executorService.submit(new PhotosLoader(p));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public String getTxt(String url) {
        return url;
    }


    //Task for the queue
    private class TxtToLoad {
        public String url;
        public WebView imageView;

        public TxtToLoad(String u, WebView i) {
            url = u;
            imageView = i;
        }
    }


    class PhotosLoader implements Runnable {
        TxtToLoad photoToLoad;

        PhotosLoader(TxtToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        @JavascriptInterface
        public void run() {

            try {
                String bmp = getTxt(photoToLoad.url);
                TxtDisplayer bd = new TxtDisplayer(bmp, photoToLoad);
                Activity a = (Activity) photoToLoad.imageView.getContext();
                a.runOnUiThread(bd);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }


    //Used to display bitmap in the UI thread

    class TxtDisplayer implements Runnable {


        String markdownStr;
        TxtToLoad photoToLoad;

        public TxtDisplayer(String b, TxtToLoad p) {
            markdownStr = b;
            photoToLoad = p;
        }

        @SuppressWarnings("deprecation")
        @JavascriptInterface
        public void run() {

            if (markdownStr != null) {
                String markdown = markdownStr;
//                String markdown = null;
//                AndDown converter=new AndDown();
//                markdown=converter.markdownToHtml(markdownStr);

//                String script = "<html>"
//                        + "<head>"
//                        + "<title>MathJax Test Page</title>"
//                        + "<!-- Copyright (c) 2009-2013 The MathJax Consortium -->"
//                        + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"
//                        + "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />"
//                        + "<script type=\"text/x-mathjax-config\">"
//                        + "MathJax.Hub.Config({"
//                        + "extensions: [\"tex2jax.js\"],"
//                        + "messageStyle: 'none',"
//                        + "jax: [\"input/TeX\",\"output/HTML-CSS\"],"
//                        + "tex2jax: {inlineMath: [['$','$']],displayMath: [['$$','$$']]}"
//                        //+"tex2jax: {inlineMath: [['$','$'], ['\\(','\\)']],displayMath: [['\\[','\\]'], ['$$','$$']]}"
//                        + "});"
//                        + "</script>"
//                        + "<script type='text/x-mathjax-config'>"
//                        + "</script>"
//                        + "<style type=\"text/css\">@font-face {font-family: \"Monotype Sabon\";src: url(\"file:///android_asset/fonts/Monotype Sabon.otf\")}"
//                        + "@font-face {font-family: \"Monotype Sabon\";src: url(\"file:///android_asset/fonts/Sentinel-Bold.otf\");font-weight: bold;}</style>"
//                        + "<font size=\"4.0\" face=\"Monotype Sabon\" style=\"line-height:150%\">"
//                        + "<script type=\"text/javascript\"src='file:///android_asset/MathJax/MathJax.js'\"></script>"
//                        + "<script>MathJax.Hub.Config({'HTML-CSS': { linebreaks: { automatic: true, width: 'container' } }, SVG: { linebreaks: { automatic: true, width: 'container' } }});</script>"
//                        + "<style type='text/css'>img { max-width: 100%; width: auto; height: auto; }</style>"
//                        + "</head>"
//                        + "<body>"
//                        + markdown
//                        + "</body>"
//                        + "</html>";


//                String script = "<html>"
//                        + "<head>"
//                        + "<style type=\"text/css\">"
////                        + "img { max-width: 100%; width: auto; height: auto; }"
//                        + "@font-face {font-family: \"Monotype Sabon\";src: url(\"file:///android_assets/MonotypeSabon_nn.ttf\");font-weight: normal;} "
//                        + "@font-face {font-family: \"Monotype Sabon\";src: url(\"file:///android_assets/Monotype_Sabon_Italic.ttf\");font-weight: normal;font-style:italic;} "
//                        + "@font-face {font-family: \"Monotype Sabon\";src: url(\"file:///android_assets/Mono_sobo_bold.ttf\");font-weight: bold;} "
////                        + "<font size=\"4.0\" face=\"Monotype Sabon\" style=\"line-height:120%\">"
////                        + "@font-face {font-family: \"Monotype Sabon\";src: url(\"file:///android_asset/Mono_sobo_bold.ttf\");font-weight: bold;}"
//                        + "@font-face {font-family: \"Charlotte\";src: url(\"file:///android_assets/Charlotte_Sans_nn.ttf\");font-weight: normal;} "
//                        + "@font-face {font-family: \"Charlotte\";src: url(\"file:///android_assets/Charlotte_Sans_Italic.ttf\");font-weight: normal;font-style:italic;} "
//                        + "p {font-family:'Monotype Sabon'} h1 {font-family:Charlotte}"
//                        + "</style>"
//                        + "</head>"
//                        + "<body style='max-width: 100%; width: auto; height: auto; color: red;>" //color: red
//                        + markdown
//                        + "</body>"
//                        + "</html>";


//System.out.println("===="+markdown);

// + "<style type='text/css'>img { max-width: 100%; width: auto; height: auto; }>"
                String script = "<html>"
                        + "<head>"
                        + "<style type=\"text/css\">"
                        + "@font-face {font-family: \"Monotype Sabon\";src: url(\"file:///android_asset/fonts/MonotypeSabon_nn.ttf\");}"
                        + "@font-face {font-family: \"Monotype Sabon\";src: url(\"file:///android_asset/fonts/Mono_sobo_bold.ttf\");font-weight: bold;}"
                        + "@font-face {font-family: \"Monotype Sabon\";src: url(\"file:///android_asset/fonts/Monotype_Sabon_Italic.ttf\");font-style: italic}"
                        + "@font-face {font-family: \"Monotype Sabon\";src: url(\"file:///android_asset/fonts/Monotype_Sabon_Bold_Italic.ttf\");font-style: italic;font-weight: bold}"
                        + "@font-face {font-family: \"Charlotte\";src: url(\"file:///android_asset/fonts/CharlotteSans_nn.ttf\");} "
                        + "@font-face {font-family: \"Charlotte\";src: url(\"file:///android_asset/fonts/Charlotte_Sans_Italic.ttf\");font-style:italic;} "
                        + "@font-face {font-family: \"Charlotte\";src: url(\"file:///android_asset/fonts/Charlotte_Sans.ttf\");font-weight: bold;} "
                        + "p {font-family:'Monotype Sabon'} h1,h2,h3,h4,h5,h6{font-family: 'Charlotte'} "
                        + "hr {\n" +
                        "  border-top: none;\n" +
                        "  text-align: center;\n" +
                        "  &:after {\n" +
                        "    content: \"*\";\n" +
                        "    color: lightgray;\n" +
                        "    position: relative;\n" +
                        "    top: -0.5em;\n" +
                        "    font-size: 1.5em;\n" +
                        "  }\n" +
                        "}"
                        + "</style>"
                        + "<style type='text/css'>img { max-width: 100%; width: auto; height: auto; }</style>"
                        + "</head>"
                        + "<body>"
                        + markdown
                        + "</body>"
                        + "</html>";
                //set font to web view Script


                photoToLoad.imageView.getSettings().setJavaScriptEnabled(true);
                photoToLoad.imageView.getSettings().setBuiltInZoomControls(false);
                photoToLoad.imageView.setHorizontalScrollBarEnabled(true);
                photoToLoad.imageView.setHorizontalFadingEdgeEnabled(false);
                photoToLoad.imageView.setHorizontalScrollbarOverlay(false);
                photoToLoad.imageView.setVerticalScrollBarEnabled(true);
                photoToLoad.imageView.setBackgroundColor(Color.parseColor("#ffffff"));
                photoToLoad.imageView.getSettings().setDefaultFontSize(18);
                photoToLoad.imageView.getSettings().setJavaScriptEnabled(true);
                photoToLoad.imageView.getSettings().setAllowFileAccess(true);
                photoToLoad.imageView.setWebChromeClient(new WebChromeClient());
                photoToLoad.imageView.getSettings().setAppCacheEnabled(true);
                photoToLoad.imageView.getSettings().setDomStorageEnabled(true);
                photoToLoad.imageView.getSettings().setPluginState(PluginState.ON);
                //prevent to load cache
                photoToLoad.imageView.getSettings().setAppCacheEnabled(false);
                photoToLoad.imageView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

                //resize webview size
                //load data here
                // photoToLoad.imageView.loadDataWithBaseURL("http://foo", script, "text/html", "utf-8", null);
                photoToLoad.imageView.loadUrl("javascript:MarkDownContent(\"" + StringEscapeUtils.escapeJava(markdown) + "\")");
//                this.photoToLoad.imageView.setWebViewClient(new WebViewClient() {
//
//                    @Override
//                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
//                        System.out.println("LINKNIKNIKNIKNKNIKNIKNIKNIKNIKNIKNIN");
////                        Intent openlink=new Intent(context,OpenLink.class);
////                        openlink.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
////                        openlink.putExtra("link", url);
////                        context.startActivity(openlink);
//                        return true;
//                    }
//                });
            }
        }
    }

}

