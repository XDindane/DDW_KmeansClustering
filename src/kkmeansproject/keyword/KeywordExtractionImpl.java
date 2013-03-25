/*
 *
 * KeywordExtractionImpl.java, provides keyword/keyphrase extraction as a Java program/API
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

import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import kkmeansproject.KeywordExtraction;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeywordExtractionImpl implements KeywordExtraction {

	private static boolean gateInited = false;
	private static final String GATE_PLUGINS_HOME = "plugins";
	private static final String GATE_USER_CONFIG = "gate-user.xml";
	private static final String GATE_SITE_CONFIG = "gate-site.xml";
	private static final String GATE_SESSION = "gate.session";

	private GateProcessorProvider gateProcessorPool = null;

	private DocumentProperties gateDocumentProperties = null;

	private static Logger logger = Logger.getLogger(KeywordExtractionImpl.class
			.getName());

	private static Set<String> acceptableFileExtension = new HashSet<String>();
	static {
		acceptableFileExtension.add(".txt");
		acceptableFileExtension.add(".htm");
		acceptableFileExtension.add(".html");
		acceptableFileExtension.add(".xhtml");
		acceptableFileExtension.add(".sgml");
		acceptableFileExtension.add(".rtf");
		acceptableFileExtension.add(".pdf");
		acceptableFileExtension.add(".doc");
		acceptableFileExtension.add(".eml");
		acceptableFileExtension.add(".stm");
	}
	
	private static Set<String> acceptableContentTypes = new HashSet<String>();
	static {
		acceptableContentTypes.add("text/plain");
		acceptableContentTypes.add("text/html");
		acceptableContentTypes.add("text/sgml");
		acceptableContentTypes.add("text/enriched");
		acceptableContentTypes.add("application/rtf");
		acceptableContentTypes.add("application/pdf");
		acceptableContentTypes.add("application/msword");
		acceptableContentTypes.add("application/sgml");
		acceptableContentTypes.add("message/rfc822");
		acceptableContentTypes.add("message/news");
		acceptableContentTypes.add("text/xml");
	}


	public KeywordExtractionImpl() {
		try {
			init();
		} catch (GateException exn) {
			logger.log(Level.SEVERE, "could not initialize GateProcessorPool",
					exn);
		}
	}

	public Map<String, Double> getKeywordsFromText(String documentText)
			throws GateProcessorNotAvailableException {

		gateDocumentProperties = null;
		gate.Document gateDocument = null;

		try {
			gateDocument = gate.Factory.newDocument(documentText);
		} catch (ResourceInstantiationException rexn) {
			logger.log(Level.SEVERE, "could not instantiate gate.Document");
		}

		return getKeywords(gateDocument);
	}

	public Map<String, Double> getKeywordsFromUrl(String documentUrl)
			throws MalformedURLException, GateProcessorNotAvailableException,
			UnsupportedFileTypeException {

		gateDocumentProperties = null;
		gate.Document gateDocument = null;

		try {
			// try to parse url first
			URL url = new URL(documentUrl);

			// we can only deal with so many formats, nicely throw an exception
			// when we cannot handle one
			if (!acceptableContentTypes.contains(FileUtils.getMimeType(documentUrl)) &&  !acceptableFileExtension.contains(url.getFile().substring(
					url.getFile().lastIndexOf(".")).toLowerCase())) {

				throw new UnsupportedFileTypeException(
						"file format not supported: "
								+ url.getFile().substring(
										url.getFile().lastIndexOf("."))
										.toLowerCase());
			}

			// parsing succeeded, instantiating gate.Document from url
			try {
				gateDocument = gate.Factory.newDocument(url);
			} catch (ResourceInstantiationException rexn) {
				logger.log(Level.SEVERE, "could not instantiate gate.Document");
			}
			
		} catch (MalformedURLException exn) {
			logger.log(Level.WARNING, "supplied document URL is not valid");
			throw exn;
			
		} catch (IOException exn){
			logger.log(Level.WARNING, "could not determine mime type of input URL", exn);
			throw new UnsupportedFileTypeException(exn.getMessage());

		}

		return getKeywords(gateDocument);
	}

	private Map<String, Double> getKeywords(gate.Document gateDocument)
			throws GateProcessorNotAvailableException {

		Map<String, Double> response = null;
		if (gateInited && gateDocument != null) {
			try {
				response = new GateProcessorConsumer(gateProcessorPool)
						.process(gateDocument);
				gateDocumentProperties = new DocumentProperties();
				gateDocumentProperties.setContent(gateDocument.getContent()
						.toString());
				gateDocumentProperties.setLanguage((String) gateDocument
						.getFeatures()
						.get(KeywordExtraction.DOCUMENT_LANGUAGE_FEATURE_NAME));
				gateDocumentProperties.setDocumentSize(((Integer) gateDocument
						.getFeatures().get(
								KeywordExtraction.DOCUMENT_SIZE_FEATURE_NAME))
						.intValue());
				gateDocumentProperties
						.setLexiconSize(((Integer) gateDocument
								.getFeatures()
								.get(
										KeywordExtraction.DOCUMENT_LEXICONSIZE_FEATURE_NAME))
								.intValue());
			} finally {
				gateDocument = null;
			}
		}

		return response;
	}

	public String getInputContent() {
		String inputText = null;
		if (gateDocumentProperties != null) {
			inputText = gateDocumentProperties.getContent();
		}
		return inputText;
	};

	public String getInputLanguage() {
		String language = null;
		if (gateDocumentProperties != null) {
			language = gateDocumentProperties.getLanguage();
		}
		return language;
	};

	public int getInputDocumentSize() {
		int documentSize = -1;
		if (gateDocumentProperties != null) {
			documentSize = gateDocumentProperties.getDocumentSize();
		}
		return documentSize;
	}

	public int getInputLexiconSize() {
		int lexiconSize = -1;
		if (gateDocumentProperties != null) {
			lexiconSize = gateDocumentProperties.getLexiconSize();
		}
		return lexiconSize;
	}

	public boolean isRemote() {
		return false;
	}

	private void init() throws GateException {

		// stupid heuristic to see whether we have a system initialised already
		// the variable alone does not work as gate could be inited from outside
		// the scope of this class
		// unfortunately there is no direct way of doing this, so we have to
		// guess
		if (!gateInited) {

			try {

				try {
					File gateHome = new File(System.getProperty("gate.home"));

					Gate.setGateHome(gateHome);

					Gate
							.setSiteConfigFile(new File(gateHome,
									GATE_SITE_CONFIG));
					Gate
							.setUserConfigFile(new File(gateHome,
									GATE_USER_CONFIG));
					Gate.setUserSessionFile(new File(gateHome, GATE_SESSION));

					Gate.setPluginsHome(new File(gateHome, GATE_PLUGINS_HOME));

					Gate.init();
				} catch (IllegalStateException exn) {
					logger
							.log(Level.INFO,
									"GATE already initialised, bypassing init sequence..");
				}

				// now load plugins

				Iterator<URL> pluginItr = Gate.getKnownPlugins().iterator();
				while (pluginItr.hasNext()) {
					URL pluginURL = pluginItr.next();
					Gate.getCreoleRegister().registerDirectories(pluginURL);
				}

				gateInited = true;

				logger.log(Level.INFO, "configuring GATE processor pool..");

				gateProcessorPool = GateProcessorProvider.getInstance();

			} catch (GateException exn) {
				throw new GateException(
						"Exception while initializing GATE resources", exn);
			}

		}

	}

	private void initGateDocument(String stringOrURL)
			throws UnsupportedFileTypeException {

	}

}

class FileUtils {

	public static String getMimeType(String documentUrl)
			throws java.io.IOException, MalformedURLException {
		
		String type = null;
		URL url = new URL(documentUrl);
		URLConnection uc = null;
		uc = url.openConnection();
		type = uc.getContentType();
		
		if (type.contains(";")){
			return type.substring(0, type.indexOf(";"));
		} else {
			return type;
		}
	}

}

class DocumentProperties {

	int lexiconSize = -1;
	int documentSize = -1;
	String language = null;
	String content = null;

	void setLexiconSize(int size) {
		lexiconSize = size;
	}

	void setDocumentSize(int size) {
		documentSize = size;
	}

	void setLanguage(String lang) {
		language = lang;
	}

	void setContent(String cont) {
		content = cont;
	}

	public int getLexiconSize() {
		return lexiconSize;
	}

	public int getDocumentSize() {
		return documentSize;
	}

	public String getLanguage() {
		return language;
	}

	public String getContent() {
		return content;
	}

}
