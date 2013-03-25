/*
 *
 * KeywordExtraction.java, provides keyword/keyphrase extraction as a Java program/API
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

package kkmeansproject;

import  kkmeansproject.keyword.GateProcessorNotAvailableException;
import  kkmeansproject.keyword.UnsupportedFileTypeException;

import java.net.MalformedURLException;
import java.util.Map;

public interface KeywordExtraction {

	public static final String DOCUMENT_LANGUAGE_FEATURE_NAME = "language";
	public static final String DOCUMENT_LEXICONSIZE_FEATURE_NAME = "lexicon_size";
	public static final String DOCUMENT_SIZE_FEATURE_NAME = "document_size";
	public static final String DOCUMENT_KEYWORDS_FEATURE_NAME = "keywords";

	/**
	 * increase this in case multiple simultaneous requests are to be supported
	 */
	public static final int NUMBER_OF_PIPELINES = 1;

	/**
	 * Extracts Keywords/Keyphrases from a given text, referenced via URL.
	 * The URL must be specified as string and better be valid.
	 * 
	 * @param documentUrl
	 *            the url to extract the keywords from
	 * @return a Map<String,Double>, where the keys represent
	 *         keywords/keyphrases and the values represent significance
	 *         (between [0;1]), ordered (hopefully!) by significance
	 * @throws MalformedURLException
	 * 			in case the specified URL is not valid
	 * @throws GateProcessorNotAvailableException
	 *             in case all GateProcessors are busy/occupied
	 * @throws UnsupportedFileTypeException
	 * 			in case the URL references a file type that is not supported
	 */
	public Map<String, Double> getKeywordsFromUrl(String documentUrl)
			throws MalformedURLException, GateProcessorNotAvailableException,
			UnsupportedFileTypeException;

	
	/**
	 * Extracts Keywords/Keyphrases from a given text, supplied as string.
	 * 
	 * @param documentText
	 *            the string to extract the keywords from
	 * @return a Map<String,Double>, where the keys represent
	 *         keywords/keyphrases and the values represent significance
	 *         (between [0;1]), ordered by significance
	 * @throws GateProcessorNotAvailableException
	 *             in case all GateProcessors are busy/occupied
	 */
	public Map<String, Double> getKeywordsFromText(String documentText)
			throws GateProcessorNotAvailableException;

	
	/**
	 * Returns the textual content of the input.
	 * 
	 * @return the textual content as String, or null in case an error was
	 *         encountered during document instantiation.
	 */
	public String getInputContent();

	/**
	 * Returns the language of the input document.
	 * 
	 * @return the language of the document, as 2-letter code (ISO639 /
	 *         ISO3166-1), or null in case an error was encountered during
	 *         document instantiation
	 */
	public String getInputLanguage();

	/**
	 * Returns the number of words/tokens in the input document.
	 * 
	 * @return the number of words/tokens as int, or -1 in case an error was
	 *         encountered during document instantiation
	 */
	public int getInputDocumentSize();

	/**
	 * Returns the size of the lexicon, which is the number of distinct lemmas
	 * (words/tokens reduced to their stem/base form)
	 * 
	 * @return the size of the lexicon as int, or -1 in case an error was
	 *         encountered during document instantiation
	 */
	public int getInputLexiconSize();

}
