/*
 * Copyright 2008-2010 Microarray Informatics Team, EMBL-European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * For further details of the Gene Expression Atlas project, including source code,
 * downloads and documentation, please see:
 *
 * http://gxa.github.com/gxa
 */

package uk.ac.ebi.gxa.netcdf.generator.helper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ucar.ma2.DataType;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import uk.ac.ebi.gxa.netcdf.generator.NetCDFGeneratorException;
import uk.ac.ebi.microarray.atlas.model.Assay;
import uk.ac.ebi.microarray.atlas.model.Sample;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 30-Sep-2009
 */
public class NetCDFFormatter {
    private boolean assayInitialized = false;
    private Dimension assayDimension;

    private boolean sampleInitialized = false;
    private Dimension sampleDimension;

    private boolean designElementInitialized = false;
    private Dimension designElementDimension;

    private boolean uefvInitialized = false;
    private Dimension uefvDimension;

    private String dataSliceStr;

    // logging
    Log log = LogFactory.getLog(this.getClass());

    public synchronized void formatNetCDF(
            NetcdfFileWriteable netCDF, DataSlice dataSlice)
            throws NetCDFGeneratorException {
        clear();

        dataSliceStr = dataSlice.toString();

        // setup assay part of netCDF
        createAssayVariables(netCDF, dataSlice.getAssays());

        // setup sample part of netCDF
        createSampleVariables(netCDF, dataSlice.getSamples());

        // set assay to sample part of netCDF (BS2AS matrix)
        createSampleAssayVariable(netCDF);

        // setup design element part of netCDF
        createDesignElementVariables(netCDF, dataSlice.getDesignElements());

        // setup design element to gene part of netCDF (DE2GN matrix)
        createDesignElementGeneVariable(netCDF);

        // setup property parts of the netCDF -
        // this depends on AS and BS dimensions being in place
        createPropertyVariables(
                netCDF,
                dataSlice.getExperimentFactorMappings(),
                dataSlice.getSampleCharacteristicMappings());

        // setup expression values matrix -
        // this depends on AS and DE dimensions being in place
        createExpressionMatrixVariables(netCDF);

        // setup stats matrices -
        // this depends on DE and uEFV
        createStatsMatricesVariables(netCDF);
    }

    private void clear() {
        assayInitialized = false;
        assayDimension = null;

        sampleInitialized = false;
        sampleDimension = null;

        designElementInitialized = false;
        designElementDimension = null;

        uefvInitialized = false;
        uefvDimension = null;

        dataSliceStr = null;
    }

    /**
     * Creates dimensions and variables in a NetCDF for a list of assays.  This results in the creation of the "AS"
     * dimension and variable.
     *
     * @param netCDF the NetCDF model to modify
     * @param assays the list of assays that will be used to configure this NetCDF
     */
    private void createAssayVariables(NetcdfFileWriteable netCDF, List<Assay> assays) {
        if (assays.size() > 0) {
            // update the netCDF with the assay count
            assayDimension = netCDF.addDimension("AS", assays.size());
            // add assay data variable
            netCDF.addVariable("AS", DataType.INT, new Dimension[]{assayDimension});
        }
        else {
            log.warn("Encountered an empty set of assays whilst generating the NetCDF for " + dataSliceStr);
        }

        log.debug("Initialized assay dimensions and variables ok.");
        assayInitialized = true;
    }

    /**
     * Creates dimensions and variables in a NetCDF for a list of samples.  This results in the creation of the "BS"
     * dimension and variable.
     *
     * @param netCDF  the NetCDF model to modify
     * @param samples the list of samples that will be used to configure this NetCDF
     */
    private void createSampleVariables(NetcdfFileWriteable netCDF, List<Sample> samples) {
        if (samples.size() > 0) {
            // update the netCDF with the sample count
            sampleDimension = netCDF.addDimension("BS", samples.size());
            // add sample variable
            netCDF.addVariable("BS", DataType.INT, new Dimension[]{sampleDimension});
        }
        else {
            log.warn("Encountered an empty set of samples whilst generating the NetCDF for " + dataSliceStr);
        }

        log.debug("Initialized sample dimensions and variables ok.");
        sampleInitialized = true;
    }

    /**
     * Create the variables that map samples to assay.  This variable is a 2D matrix, sized by the sample dimension vs.
     * the assay dimension.  1's and 0's are inserted into each cell depending on whether there is a correspondence
     * between these two or not.
     * <p/>
     * Because this variable is sized by assays and samples, these dimensions must have been created first.  An
     * exception is thrown if these dimnesions have not been created first.  Note that if these dimnesions have not been
     * initialized because they have zero length, this method will not throw an exception but will rather result in no
     * variable being created.
     *
     * @param netCDF the NetCDF model to modify
     * @throws uk.ac.ebi.gxa.netcdf.generator.NetCDFGeneratorException
     *          if a dependent dimension has not first been created
     */
    private void createSampleAssayVariable(NetcdfFileWriteable netCDF)
            throws NetCDFGeneratorException {
        if (!sampleInitialized) {
            throw new NetCDFGeneratorException("Cannot create 'BS2AS' variable without first assessing 'BS' dimension");
        }
        if (!assayInitialized) {
            throw new NetCDFGeneratorException("Cannot create 'BS2AS' variable without first assessing 'BS' dimension");
        }

        if (sampleDimension != null && assayDimension != null) {
            // add assay to sample variable
            netCDF.addVariable("BS2AS", DataType.INT,
                               new Dimension[]{sampleDimension, assayDimension});
        }
        log.debug("Initialized assay2sample dimensions and variables ok.");
    }

    /**
     * Creates dimensions and variables in a NetCDF for a list of design element identifiers.  This results in the
     * creation of the "DE" dimension and variable.
     *
     * @param netCDF         the NetCDF model to modify
     * @param designElements the design elements map - the keyset is the list of unique identifiers for design elements
     *                       that will be used to configure this NetCDF
     */
    private void createDesignElementVariables(NetcdfFileWriteable netCDF, Map<Integer, String> designElements) {
        if (designElements.keySet().size() > 0) {
            // update the netCDF with the genes count
            designElementDimension = netCDF.addDimension("DE", designElements.keySet().size());
            // add gene variable
            netCDF.addVariable("DE", DataType.INT, new Dimension[]{designElementDimension});
            netCDF.addVariable("GN", DataType.INT, new Dimension[]{designElementDimension});
        }
        else {
            log.warn("Encountered an empty set of design elements whilst generating the NetCDF for " + dataSliceStr);
        }

        log.debug("Initialized design element dimensions and variables ok.");
        designElementInitialized = true;
    }

    /**
     * Create the variables that map design elements to genes.  Unlike the BS2AS matrix, this is not simply a matrix of
     * zeros and ones for each design element to gene - this would result in the creation of a huge, highly sparse
     * matrix.  Instead, design element to genes are stored as a matrix of integer pairs, storing the position in the
     * design element matrix and the position in the gene matrix for every unique mapping.  The size of this matrix is
     * therefore variable, and not necessarily the same size as either the DE or GN matirx (except where there is 1:1
     * mappings between all elements).
     *
     * @param netCDF the NetCDF model to modify
     * @throws uk.ac.ebi.gxa.netcdf.generator.NetCDFGeneratorException
     *          if a dependent dimension has not first been created
     */
    private void createDesignElementGeneVariable(NetcdfFileWriteable netCDF)
            throws NetCDFGeneratorException {
        if (!designElementInitialized) {
            throw new NetCDFGeneratorException("Cannot create 'DE2GN' variable without first assessing 'DE' dimension");
        }

        // DE2GN is an array of unlimited length (i.e. increases with each unique pair) by 2, (i.e. pairs of De to GN mappings)
        Dimension pairsDimension = netCDF.addUnlimitedDimension("DE2GNPairs");
        Dimension mappingsDimension = netCDF.addDimension("DE2GNMapping", 2);
        if (designElementDimension != null) {
            // add assay to sample variable
            netCDF.addVariable("DE2GN", DataType.INT, new Dimension[]{pairsDimension, mappingsDimension});
            log.debug("DE2GN variable added, unlimited length array of pairs");
        }
        log.debug("Initialized designelement2gene dimensions and variables ok.");
    }

    /**
     * Creates the variables for the property-based matrices.  There are several matrices created here, "EF", "EFV",
     * "uEFV" and "uEFVnum".  In turn, these represent the experiment factors (or assay properties) in the data, the
     * experiment factor values, the unique experiment factor/experiment factor value combinations, and the number of
     * times a unique combination of experiment factor/experiment factor value was seen.
     * <p/>
     * Some of these matrices map values to assays, and some to samples, and as such the "AS" and "BS" dimensions should
     * already be present in the supplied NetCDF.
     *
     * @param netCDF                  the NetcdfFileWriteable currently being set up
     * @param experimentFactorMap     the mapping between experiment factors and their values
     * @param sampleCharacteristicMap the mapping between sample characteristics and their values
     * @throws uk.ac.ebi.gxa.netcdf.generator.NetCDFGeneratorException
     *          if dependent matrices "AS" or "BS" have not previously been configured for this NetCDF.
     */
    private void createPropertyVariables(NetcdfFileWriteable netCDF,
                                         Map<String, List<String>> experimentFactorMap,
                                         Map<String, List<String>> sampleCharacteristicMap)
            throws NetCDFGeneratorException {
        if (!sampleInitialized) {
            throw new NetCDFGeneratorException(
                    "Cannot create property variables without first assessing 'BS' dimension");
        }
        if (!assayInitialized) {
            throw new NetCDFGeneratorException(
                    "Cannot create property variables without first assessing 'BS' dimension");
        }

        if (assayDimension != null && sampleDimension != null) {
            // first up, EF - length = number of experiment factors
            if (experimentFactorMap.keySet().size() > 0) {
                Dimension efDimension = netCDF.addDimension("EF", experimentFactorMap.keySet().size());

                // ef, efv variables are sized by string length
                int maxEFLength = 0;
                for (String propertyName : experimentFactorMap.keySet()) {
                    if (propertyName.length() > maxEFLength) {
                        maxEFLength = propertyName.length();
                    }
                }
                int maxEFVLength = 0;
                for (List<String> propertyValues : experimentFactorMap.values()) {
                    // get the length of all concatenated property names
                    int listLength = 0;
                    for (String propertyValue : propertyValues) {
                        listLength += (propertyValue.length() + 2);
                    }
                    if (listLength > maxEFVLength) {
                        maxEFVLength = listLength;
                    }
                }

                // derive longest text value for EF/EFV
                int maxLength = maxEFLength + maxEFVLength + 2;

                // next up, EFV length - this is equal to max number of values mapped to one property
                Dimension efvDimension = netCDF.addDimension("EFlen", maxLength);

                // last up, uEFVs - this is all unique EF:EFV patterns
                Set<String> uniqueFactorValues = new LinkedHashSet<String>();
                for (String property : experimentFactorMap.keySet()) {
                    for (String propertyValue : experimentFactorMap.get(property)) {
                        uniqueFactorValues.add(property.concat(":").concat(propertyValue));
                    }
                }
                uefvDimension = netCDF.addDimension("uEFV", uniqueFactorValues.size());

                // now add variables
                netCDF.addVariable("EF", DataType.CHAR, new Dimension[]{efDimension, efvDimension});
                netCDF.addVariable("EFV", DataType.CHAR, new Dimension[]{efDimension, assayDimension, efvDimension});
                netCDF.addVariable("uEFV", DataType.CHAR, new Dimension[]{uefvDimension, efvDimension});
                netCDF.addVariable("uEFVnum", DataType.INT, new Dimension[]{efDimension});
            }
            else {
                log.warn("Encountered an empty set of assay properties whilst generating the NetCDF for " +
                        dataSliceStr);
            }


            // finally, do the same thing for sample properties
            if (sampleCharacteristicMap.size() > 0) {
                Dimension scDimension =
                        netCDF.addDimension("SC", sampleCharacteristicMap.keySet().size());
                // sc,scv variables are sized by string length
                int maxSCLength = 0;
                for (String propertyName : sampleCharacteristicMap.keySet()) {
                    if (propertyName.length() > maxSCLength) {
                        maxSCLength = propertyName.length();
                    }
                }
                int maxSCVLength = 0;
                for (List<String> propertyValues : sampleCharacteristicMap.values()) {
                    // get the length of all concatenated property names
                    int listLength = 0;
                    for (String propertyValue : propertyValues) {
                        listLength += (propertyValue.length() + 2);
                    }
                    if (listLength > maxSCVLength) {
                        maxSCVLength = listLength;
                    }
                }
                // derive longest text value for SC/SCV
                int maxLength = maxSCLength > maxSCVLength ? maxSCLength : maxSCVLength;

                Dimension sclDimension = netCDF.addDimension("SClen", maxLength);

                // and add variables
                netCDF.addVariable("SC", DataType.CHAR, new Dimension[]{scDimension, sclDimension});
                netCDF.addVariable("SCV", DataType.CHAR, new Dimension[]{scDimension, sampleDimension, sclDimension});
            }
            else {
                log.warn("Encountered an empty set of sample properties whilst " +
                        "generating the NetCDF for " + dataSliceStr);
            }
        }

        log.debug("Initialized property dimensions and variables ok.");
        uefvInitialized = true;
    }

    /**
     * Creates the variables for the expression value matrix.  This matrix is keyed on the name "BDC".  It is a 2D
     * matrix of expression values for design elements against assays, so this method requires that both "DE" and "AS"
     * dimensions have already been created in the NetcdfFileWriteable supplied.
     *
     * @param netCDF the NetcdfFileWriteable currently being set up
     * @throws uk.ac.ebi.gxa.netcdf.generator.NetCDFGeneratorException
     *          if the dependent matrix "AS" hasn't first been created
     */
    private void createExpressionMatrixVariables(NetcdfFileWriteable netCDF)
            throws NetCDFGeneratorException {
        if (!assayInitialized) {
            throw new NetCDFGeneratorException("Cannot create property variables " +
                    "without first assessing 'AS' dimension");
        }

        if (assayDimension != null) {
            netCDF.addVariable("BDC", DataType.DOUBLE, new Dimension[]{designElementDimension, assayDimension});
        }
        log.debug("Initialized expression dimensions and variables ok.");
    }

    /**
     * This creates the variables for the statistics matrices.  This actually builds two matrices, one of P value
     * statistics and one of T statistics. These matrices are both 2D matrices of T or P values for design elements
     * against unique property/property value combinations.  This method therefore requires that both these "DE" and
     * "uEFV" dimensions have already been created in the NetcdfFileWriteable supplied.
     *
     * @param netCDF the NetcdfFileWriteable currently being set up
     * @throws uk.ac.ebi.gxa.netcdf.generator.NetCDFGeneratorException
     *          if the dependent matrices "DE" and "uEFV" haven't first been created
     */
    private void createStatsMatricesVariables(NetcdfFileWriteable netCDF)
            throws NetCDFGeneratorException {
        if (!designElementInitialized) {
            throw new NetCDFGeneratorException("Cannot create stats variables " +
                    "without first assessing 'DE' dimension");
        }
        if (!uefvInitialized) {
            throw new NetCDFGeneratorException("Cannot create stats variables " +
                    "without first assessing 'uEFV' dimension");
        }

        if (designElementDimension != null && uefvDimension != null) {
            netCDF.addVariable("PVAL", DataType.DOUBLE, new Dimension[]{designElementDimension, uefvDimension});
            netCDF.addVariable("TSTAT", DataType.DOUBLE, new Dimension[]{designElementDimension, uefvDimension});
        }
        log.debug("Initialized stats dimensions and variables ok.");
    }
}
