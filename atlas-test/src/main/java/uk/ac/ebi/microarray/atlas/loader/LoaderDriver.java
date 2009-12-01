package uk.ac.ebi.microarray.atlas.loader;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.gxa.index.builder.IndexBuilder;
import uk.ac.ebi.gxa.index.builder.IndexBuilderException;
import uk.ac.ebi.gxa.index.builder.listener.IndexBuilderEvent;
import uk.ac.ebi.gxa.index.builder.listener.IndexBuilderListener;
import uk.ac.ebi.gxa.loader.AtlasLoader;
import uk.ac.ebi.gxa.loader.AtlasLoaderException;
import uk.ac.ebi.gxa.loader.listener.AtlasLoaderEvent;
import uk.ac.ebi.gxa.loader.listener.AtlasLoaderListener;
import uk.ac.ebi.gxa.netcdf.generator.NetCDFGenerator;
import uk.ac.ebi.gxa.netcdf.generator.NetCDFGeneratorException;
import uk.ac.ebi.gxa.netcdf.generator.listener.NetCDFGenerationEvent;
import uk.ac.ebi.gxa.netcdf.generator.listener.NetCDFGeneratorListener;
import uk.ac.ebi.microarray.atlas.dao.AtlasDAO;
import uk.ac.ebi.microarray.atlas.dao.LoadStage;
import uk.ac.ebi.microarray.atlas.dao.LoadStatus;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.text.DecimalFormat;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 09-Sep-2009
 */
public class LoaderDriver {
    public static void main(String[] args) {
        // load spring config
        BeanFactory factory =
                new ClassPathXmlApplicationContext("loaderContext.xml");

        // loader
        final AtlasLoader loader = (AtlasLoader) factory.getBean("atlasLoader");
        // index
        final IndexBuilder builder = (IndexBuilder) factory.getBean("indexBuilder");
        // netcdfs
        final NetCDFGenerator generator = (NetCDFGenerator) factory.getBean("netcdfGenerator");

        // run the loader
//        try {
//            final URL url = URI.create("file:///home/tburdett/Documents/MAGE-TAB/E-GEOD-3790/E-GEOD-3790.idf.txt").toURL();
//            final long indexStart = System.currentTimeMillis();
//            loader.loadExperiment(url, new AtlasLoaderListener() {
//
//                public void loadSuccess(AtlasLoaderEvent event) {
//                    final long indexEnd = System.currentTimeMillis();
//
//                    String total = new DecimalFormat("#.##").format(
//                            (indexEnd - indexStart) / 60000);
//                    System.out.println(
//                            "Load completed successfully in " + total + " mins.");
//
//                    try {
//                        loader.shutdown();
//                    }
//                    catch (AtlasLoaderException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                public void loadError(AtlasLoaderEvent event) {
//                    System.out.println("Load failed");
//                    for (Throwable t : event.getErrors()) {
//                        t.printStackTrace();
//                        try {
//                            loader.shutdown();
//                        }
//                        catch (AtlasLoaderException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });
//        }
//        catch (MalformedURLException e) {
//            e.printStackTrace();
//            System.out.println("Load failed - inaccessible URL");
//        }

        // in case we don't run loader
        try {
            loader.shutdown();
        }
        catch (AtlasLoaderException e) {
            e.printStackTrace();
        }

        // run the index builder
        final long indexStart = System.currentTimeMillis();
        builder.buildIndex(new IndexBuilderListener() {

            public void buildSuccess(IndexBuilderEvent event) {
                final long indexEnd = System.currentTimeMillis();

                String total = new DecimalFormat("#.##").format(
                        (indexEnd - indexStart) / 60000);
                System.out.println(
                        "Index built successfully in " + total + " mins.");

                try {
                    builder.shutdown();
                }
                catch (IndexBuilderException e) {
                    e.printStackTrace();
                }
            }

            public void buildError(IndexBuilderEvent event) {
                System.out.println("Index failed to build");
                for (Throwable t : event.getErrors()) {
                    t.printStackTrace();
                    try {
                        builder.shutdown();
                    }
                    catch (IndexBuilderException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // in case we don't run indexbuilder
//        try {
//            builder.shutdown();
//        }
//        catch (IndexBuilderException e) {
//            e.printStackTrace();
//        }

        // run the NetCDFGenerator
//        final long netStart = System.currentTimeMillis();
//        generator.generateNetCDFsForExperiment(
//                "E-GEOD-1725",
//                new NetCDFGeneratorListener() {
//                    public void buildSuccess(NetCDFGenerationEvent event) {
//                        final long netEnd = System.currentTimeMillis();
//
//                        String total = new DecimalFormat("#.##").format(
//                                (netEnd - netStart) / 60000);
//                        System.out.println(
//                                "NetCDFs generated successfully in " + total + " mins.");
//
//                        try {
//                            generator.shutdown();
//                        }
//                        catch (NetCDFGeneratorException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    public void buildError(NetCDFGenerationEvent event) {
//                        System.out.println("NetCDF Generation failed!");
//                        for (Throwable t : event.getErrors()) {
//                            t.printStackTrace();
//                            try {
//                                generator.shutdown();
//                            }
//                            catch (NetCDFGeneratorException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });

        // in case we don't run netCDF generator
        try {
            generator.shutdown();
        }
        catch (NetCDFGeneratorException e) {
            e.printStackTrace();
        }

//        // do a test load_monitor update
//        final AtlasDAO atlasDAO =
//                (AtlasDAO) factory.getBean("atlasDAO");
//        atlasDAO.writeLoadDetails("TEST-1", LoadStage.LOAD, LoadStatus.WORKING);
//        System.out.println("Set TEST-1: load = working");
//
//        // wait 30 seconds
//        final Object o = new Object();
//        synchronized (o) {
//            try {
//                o.wait(30000);
//            }
//            catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        atlasDAO.writeLoadDetails("TEST-1", LoadStage.LOAD, LoadStatus.DONE);
//        System.out.println("Set TEST-1: load = done");

        
    }
}
