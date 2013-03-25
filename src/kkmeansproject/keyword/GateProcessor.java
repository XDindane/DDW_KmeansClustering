/*
 *
 * GateProcessor.java, provides keyword/keyphrase extraction as a Java program/API
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

import gate.Corpus;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.LanguageAnalyser;
import gate.ProcessingResource;
import gate.creole.AnalyserRunningStrategy;
import gate.creole.ConditionalSerialAnalyserController;
import gate.creole.ResourceInstantiationException;
import gate.creole.RunningStrategy;
import gate.util.GateException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Initializes a GATE Processing Pipeline (PP), which includes initializing the
 * necessary Processing Resources (PRs) and setting the affiliated corpus.
 * Encapsulates 2 GATE Resources -- SerialAnalyserController and Corpus, for
 * convenience reasons when providing more than 1 GateProcessor in a processor
 * pool (for multiple simultaneous requests)
 * <p>
 * The PP has been chosen to be of Conditional nature, as originally language
 * specific PRs are meant to be loaded, i.e. a German part-of-speech tagger in
 * case the document in question was found to be in German.
 * <p>
 * The running strategies for the PRs are set during initialization, and basic
 * logging is being provided.
 * 
 * @author alesch
 * 
 */
public class GateProcessor {

	private static Logger logger = Logger.getLogger(GateProcessor.class
			.getName());

	private ConditionalSerialAnalyserController controller = null;
	private Corpus corpus = null;

	public GateProcessor() throws GateException {
		try {
			setController();
			setCorpus();
		} catch (GateException exn) {
			logger.log(Level.SEVERE, "could not instantiate GateProcessor ",
					exn);
			throw exn;
		}
	}

	private void setController() throws GateException {
		String[] procResNames = { "gate.creole.annotdelete.AnnotationDeletePR",
				"ie.deri.sw.smile.nlp.gate.language.LanguageIdentifier",
				"gate.creole.tokeniser.SimpleTokeniser",
				"ie.deri.sw.smile.nlp.gate.stopword.StopwordMarker",
				"gate.creole.splitter.SentenceSplitter", };

		controller = (ConditionalSerialAnalyserController) Factory
				.createResource(
						"gate.creole.ConditionalSerialAnalyserController",
						Factory.newFeatureMap(), Factory.newFeatureMap(),
						"KEYPHRASE_EXTRACTOR_" + Gate.genSym());

		// we cannot make it simple and put PR names into an array and run
		// through it for initialization as we have to set parameters for
		// almost
		// each single one, so we only take that approach for the basic and
		// language independent resources

		for (int i = 0; i < procResNames.length; i++) {
			FeatureMap params = Factory.newFeatureMap();

			ProcessingResource pr = (ProcessingResource) Factory
					.createResource(procResNames[i], params);
// add the PR to the pipeline controller
			controller.add(pr);

			logger.log(Level.INFO, "addeed " + pr.getName() + " "
					+ pr.getClass().getCanonicalName());
                     
		}

		// "gate.creole.POSTagger",
		ProcessingResource en_POSTagger_PR = (ProcessingResource) Factory
				.createResource("gate.creole.POSTagger", Factory
						.newFeatureMap(), Factory.newFeatureMap(),
						"English POS Tagger");
		controller.add(en_POSTagger_PR);

		// "gate.creole.morph.Morph",
		ProcessingResource en_Morphology_PR = (ProcessingResource) Factory
				.createResource("gate.creole.morph.Morph", Factory
						.newFeatureMap(), Factory.newFeatureMap(),
						"English Morphology");
		en_Morphology_PR.setParameterValue("rootFeatureName", "lemma");
		controller.add(en_Morphology_PR);
		logger.log(Level.INFO, "added " + en_Morphology_PR.getName() + " "
				+ en_Morphology_PR.getClass().getCanonicalName());

		// "ie.deri.sw.smile.nlp.gate.postag.mapper.PosTagMapper",
		ProcessingResource PosTag_Mapper_PR = (ProcessingResource) Factory
				.createResource(
						"ie.deri.sw.smile.nlp.gate.postag.mapper.PosTagMapper",
						Factory.newFeatureMap(), Factory.newFeatureMap(),
						"POS-Tag Mapper");
		controller.add(PosTag_Mapper_PR);
		logger.log(Level.INFO, "added " + PosTag_Mapper_PR.getName() + " "
				+ PosTag_Mapper_PR.getClass().getCanonicalName());

		// "ie.deri.sw.smile.nlp.gate.chunking.SimpleNounChunker",
		ProcessingResource en_NounChunker_PR = (ProcessingResource) Factory
				.createResource(
						"ie.deri.sw.smile.nlp.gate.chunking.SimpleNounChunker",
						Factory.newFeatureMap(), Factory.newFeatureMap(),
						"English NounChunker");
		controller.add(en_NounChunker_PR);
		logger.log(Level.INFO, "added " + en_NounChunker_PR.getName() + " "
				+ en_NounChunker_PR.getClass().getCanonicalName());

		// "ie.deri.sw.smile.nlp.gate.frequency.FrequencyAnalyser",
		ProcessingResource frequencyAnalyser_PR = (ProcessingResource) Factory
				.createResource(
						"ie.deri.sw.smile.nlp.gate.frequency.FrequencyAnalyser",
						Factory.newFeatureMap(), Factory.newFeatureMap(),
						"Frequency Analyser");
		controller.add(frequencyAnalyser_PR);
		logger.log(Level.INFO, "added " + frequencyAnalyser_PR.getName() + " "
				+ frequencyAnalyser_PR.getClass().getCanonicalName());

		// "ie.deri.sw.smile.nlp.gate.keyword.KeywordAnalyser",
		ProcessingResource keywordAnalyser_PR = (ProcessingResource) Factory
				.createResource(
						"ie.deri.sw.smile.nlp.gate.keyword.KeywordAnalyser",
						Factory.newFeatureMap(), Factory.newFeatureMap(),
						"Keyword Analyser");
		controller.add(keywordAnalyser_PR);
		logger.log(Level.INFO, "added " + keywordAnalyser_PR.getName() + " "
				+ keywordAnalyser_PR.getClass().getCanonicalName());

		List<ProcessingResource> prL = new ArrayList(controller.getPRs());
		for (int idx = 0; idx < prL.size(); idx++) {
			logger.log(Level.INFO, "INDEX: " + idx + " -- "
					+ prL.get(idx).getName());
		}

		// set running strategy for english POS tagger
		controller.setRunningStrategy(5, new AnalyserRunningStrategy(
				(LanguageAnalyser) prL.get(5), RunningStrategy.RUN_CONDITIONAL,
				"language", "en"));

		// set running strategy for english morphology
		controller.setRunningStrategy(6, new AnalyserRunningStrategy(
				(LanguageAnalyser) prL.get(6), RunningStrategy.RUN_CONDITIONAL,
				"language", "en"));

		// set running strategy for english noun chunker
		controller.setRunningStrategy(8, new AnalyserRunningStrategy(
				(LanguageAnalyser) prL.get(8), RunningStrategy.RUN_CONDITIONAL,
				"language", "en"));

		prL = null;
		procResNames = null;

	}

	private void setCorpus() throws ResourceInstantiationException {
		this.corpus = Factory.newCorpus("KeywordExtractionCorpus");
		logger.log(Level.INFO, "setting corpus..");
		controller.setCorpus(corpus);
	}

	public ConditionalSerialAnalyserController getController() {
		return this.controller;
	}

	public Corpus getCorpus() {
		return this.corpus;
	}

}