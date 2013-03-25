/*
 *
 * GateProcessorConsumer.java, provides keyword/keyphrase extraction as a Java program/API
 * Copyright (C) 2008  Alexander Schutz
 * National University of Ireland, Galway
 * Digital Enterprise Research Institute
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */


package kkmeansproject.keyword;

import gate.Factory;
import gate.util.GateException;
import kkmeansproject.KeywordExtraction;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the consumption (and returning) of a GateProcessor from the
 * ProcessorPool
 * <p>
 * If the retrieval of a GateProcessor from the Processor Pool is successful
 * before a timeout occurs, it is used for processing a gate.Document, which is
 * added to the corpus over which the Processing Pipeline of the GateProcessor
 * is being executed.
 * <p>
 * After executing the Processing Pipeline, the relevant annotations and
 * amendments to the gate.Document are being post-processed, and transformed
 * into an acceptable output structure.
 * <p>
 * Finally, the GATE-internal structures are being cleaned up and GateProcessor
 * is being returned back to the Processor Pool so that other calls can be made
 * 
 * @author alesch
 * 
 */
public class GateProcessorConsumer {

	private static Logger logger = Logger.getLogger(GateProcessorConsumer.class
			.getName());

	private GateProcessorProvider pool;

	// the timeout in milliseconds
	private static final int TIMEOUT = 5000;

	public GateProcessorConsumer(GateProcessorProvider p) {
		this.pool = p;
	}

	public Map<String, Double> process(gate.Document document) throws GateProcessorNotAvailableException {

		Map<String, Double> keywordM = null;

		GateProcessor gateProc = null;
		try {
			// retrieve a GateProcessor from the Processor Pool, fail after a
			// timeout
			gateProc = pool.poll(TIMEOUT, TimeUnit.MILLISECONDS);

			if (gateProc != null) {

				// do processing here
				logger.log(Level.FINE, "Adding document to corpus");
				gateProc.getCorpus().add(document);
				logger.log(Level.FINE, "Executing pipeline over corpus ...");
				gateProc.getController().execute();

				// do stuff with annotated document, actually only return
				// keyword map stored as value for
				// DOCUMENT_KEYWORDS_FEATURE_NAME in document

				keywordM = (LinkedHashMap<String, Double>) document
						.getFeatures()
						.get(KeywordExtraction.DOCUMENT_KEYWORDS_FEATURE_NAME);

				// clean up
				gateProc.getCorpus().unloadDocument(document);
				gateProc.getCorpus().cleanup();
				gateProc.getCorpus().clear();
				document.cleanup();
				Factory.deleteResource((gate.Document) document);
				document = null;

			} else {
				throw new GateProcessorNotAvailableException(
						"no GateProcessor available at the moment, please try again later. could not retrieve GateProcessor, cannot process.");
			}

		} catch (GateException exn) {
			logger.log(Level.WARNING, exn.getMessage(), exn);
		} catch (InterruptedException exn) {
			logger.log(Level.WARNING, exn.getMessage(), exn);
		} finally {
			// and nicely return the GateProcessor back to the pool in case it
			// is not null
			if (gateProc != null && pool.offer(gateProc)) {
				logger.log(Level.INFO,
						"successfully returned GateProcessor back to the pool");
			}
			// TODO we need a mechanism that ensures the pool is always filled
			// close to its capacity, i.e. that in case something goes wrong
			// during processing, we nevertheless return a fully functional
			// GateProcessor back to the pool

		}

		return keywordM;
	}

}

