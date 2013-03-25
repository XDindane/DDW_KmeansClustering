/*
 *
 * GateProcessorProvider.java, provides keyword/keyphrase extraction as a Java program/API
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

import gate.util.GateException;

import kkmeansproject.KeywordExtraction;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a number of GateProcessors in a pool -- implemented as an
 * ArrayBlockingQueue of GateProcessors.
 * <p>
 * 
 * @author alesch
 * 
 */
public class GateProcessorProvider extends ArrayBlockingQueue<GateProcessor> {
	private static final long serialVersionUID = -5540696752870804820L;

	private static Logger logger = Logger.getLogger(GateProcessorProvider.class
			.getName());

	private static GateProcessorProvider _instance = null;
	
	/**
	 * 
	 */
	public static final GateProcessorProvider getInstance() throws GateException {
		if (_instance == null){
			_instance = new GateProcessorProvider();
		}
		return _instance;
	}
	
	private GateProcessorProvider() throws GateException {
		super(KeywordExtraction.NUMBER_OF_PIPELINES);
		init();
	}

	/**
	 * Initializes a number of GateProcessors as specified by the capacity
	 * parameter of the constructor
	 * 
	 * @throws GateException
	 *             TODO better exception handling
	 */
	private void init() throws GateException {
		while (this.remainingCapacity() > 0) {
			if (this.offer(new GateProcessor())) {
				logger.log(Level.INFO, "added " + GateProcessor.class.getName()
						+ " to ProcessorQueue");
			}
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	
}
