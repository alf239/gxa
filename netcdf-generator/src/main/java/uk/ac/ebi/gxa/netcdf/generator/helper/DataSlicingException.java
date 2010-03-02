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

import uk.ac.ebi.gxa.netcdf.generator.NetCDFGeneratorException;

/**
 * An exception generated whenever an attempt to evaluate a slice of data
 * appropriate for NetCDF generation fails.
 *
 * @author Tony Burdett
 * @date 22-Oct-2009
 */
public class DataSlicingException extends NetCDFGeneratorException {
  public DataSlicingException() {
    super();
  }

  public DataSlicingException(String s) {
    super(s);
  }

  public DataSlicingException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public DataSlicingException(Throwable throwable) {
    super(throwable);
  }
}
