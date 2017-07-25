package it.mltk.eebp.services;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class FlexmarkService {

    private Parser parser;
    private HtmlRenderer renderer;

    public FlexmarkService() {
        MutableDataSet options = new MutableDataSet();

        // uncomment to set optional extensions
        //options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));
        options.set(Parser.EXTENSIONS, Arrays.asList(StrikethroughExtension.create(), AutolinkExtension.create()));
        options.set(HtmlRenderer.FENCED_CODE_LANGUAGE_CLASS_PREFIX, "prettyprint lang-");

        // uncomment to convert soft-breaks to hard breaks
        //options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();
    }

    public String parseMarkdown(String markdown) {
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
