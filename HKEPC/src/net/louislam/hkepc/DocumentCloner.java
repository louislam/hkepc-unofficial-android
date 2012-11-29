package net.louislam.hkepc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author bleadof
 */
public class DocumentCloner {

    public static final String DEFAULT_BASE_HTML = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n"+
                                                   "    http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                                                   "<html xmlns=\"http://www.w3.org/1999/xhtml\"></html>";
    private String baseHTML;

    public DocumentCloner() {
        this.baseHTML = DEFAULT_BASE_HTML;
    }

    public DocumentCloner(String baseHTML) {
        this.setBaseHTML(baseHTML);
    }

    public void setBaseHTML(String baseHTML) {
        this.baseHTML = baseHTML;
    }

    public String getBaseHTML() {
        return this.baseHTML;
    }

    public Document cloneDocument(Document original) {
        Document clone = Jsoup.parse(this.getBaseHTML());
        this.cloneHeadTo(original.getElementsByTag("head").first(), clone.getElementsByTag("head").first());
        
        Element oBody = original.getElementsByTag("body").first();
        
        Element cBody = clone.getElementsByTag("body").first();
        cBody.attr("id", original.getElementsByTag("body").first().id());
        
        for(Element oElem : oBody.children()) {
            cBody.appendChild(this.cloneElement(oElem));
        }
        return clone;
    }

    public Element cloneElement(Element original) {
        Element clone = new Element(original.tag(),
                                    original.baseUri(),
                                    original.attributes());
        String ownText = original.ownText();
        if(ownText != null) {
            clone.text(ownText);
        }
        for(Element e : original.children()) {
            clone.appendChild(this.cloneElement(e));
        }
        return clone;
    }

    public Element cloneHeadTo(Element oHead, Element cHead) {
        for(Element e : oHead.children()) {
            cHead.appendChild(this.cloneElement(e));
        }
        return cHead;
    }
}
