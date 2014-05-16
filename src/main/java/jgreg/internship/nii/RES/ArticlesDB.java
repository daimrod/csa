// 
// Author:: Grégoire Jadi <daimrod@gmail.com>
// Copyright:: Copyright (c) 2014, Grégoire Jadi
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
// 
//    1. Redistributions of source code must retain the above copyright
//       notice, this list of conditions and the following disclaimer.
// 
//    2. Redistributions in binary form must reproduce the above
//       copyright notice, this list of conditions and the following
//       disclaimer in the documentation and/or other materials provided
//       with the distribution.
// 
// THIS SOFTWARE IS PROVIDED BY GRÉGOIRE JADI ``AS IS'' AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
// PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GRÉGOIRE JADI OR
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
// USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
// OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE.
// 
// The views and conclusions contained in the software and
// documentation are those of the authors and should not be
// interpreted as representing official policies, either expressed or
// implied, of Grégoire Jadi.
// 

package jgreg.internship.nii.RES;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

// TODO: Auto-generated Javadoc
/**
 * The Class ArticlesDB.
 */
public final class ArticlesDB implements SharedResourceObject {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(ArticlesDB.class
			.getCanonicalName());

	/** The db. */
	private Map<String, Article> db;

	/* (non-Javadoc)
	 * @see org.apache.uima.resource.SharedResourceObject#load(org.apache.uima.resource.DataResource)
	 */
	public void load(DataResource aData) throws ResourceInitializationException {
		db = new HashMap<>();
	}

	/**
	 * Gets the.
	 *
	 * @param PMID the pmid
	 * @return the article
	 */
	public Article get(String PMID) {
		return db.get(PMID);
	}

	/**
	 * Adds the.
	 *
	 * @param article the article
	 * @return the article
	 */
	public Article add(Article article) {
		return db.put(article.getPMID(), article);
	}

	/**
	 * Removes the.
	 *
	 * @param PMID the pmid
	 * @return the article
	 */
	public Article remove(String PMID) {
		return db.remove(PMID);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append('[');
		for (Entry<String, Article> entry : db.entrySet()) {
			ret.append(entry.getKey()).append(", ").append(entry.getValue()).append(' ');
		}
		ret.append(']');
		return ret.toString();
	}
}
