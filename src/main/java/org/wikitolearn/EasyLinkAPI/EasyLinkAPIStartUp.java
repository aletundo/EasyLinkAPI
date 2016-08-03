package org.wikitolearn.EasyLinkAPI;

/**
 * Created by alessandro on 03/08/16.
 */

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import it.uniroma1.lcl.babelfy.commons.BabelfyConfiguration;
import it.uniroma1.lcl.babelnet.BabelNetConfiguration;
import it.uniroma1.lcl.jlt.Configuration;
import it.uniroma1.lcl.jlt.util.Language;
import org.wikitolearn.EasyLinkAPI.controllers.utils.ThreadProgress;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.core.Context;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebListener
public class EasyLinkAPIStartUp implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext application = event.getServletContext();
        Map<String, Language> languages = new HashMap<>();
        Configuration jltConfiguration = Configuration.getInstance();
        jltConfiguration
                .setConfigurationFile(new File(application.getRealPath("/") + "/WEB-INF/config/jlt.properties"));
        BabelNetConfiguration bnconf = BabelNetConfiguration.getInstance();
        bnconf.setConfigurationFile(new File(application.getRealPath("/") + "/WEB-INF/config/babelnet.properties"));
        bnconf.setBasePath(application.getRealPath("/") + "/WEB-INF/");
        BabelfyConfiguration.getInstance()
                .setConfigurationFile(new File(application.getRealPath("/") + "/WEB-INF/config/babelfy.properties"));

        languages.put("it", Language.IT);
        languages.put("en", Language.EN);
        languages.put("fr", Language.FR);
        languages.put("es", Language.ES);
        languages.put("de", Language.DE);

        application.setAttribute("activeThreads", new HashMap<UUID, ThreadProgress>());
        application.setAttribute("languages", languages);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("---- destroying servlet context -----");
        // clean up resources
    }
}