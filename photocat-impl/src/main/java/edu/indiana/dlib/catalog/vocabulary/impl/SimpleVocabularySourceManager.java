/**
 * Copyright 2015 Trustees of Indiana University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE TRUSTEES OF INDIANA UNIVERSITY ``AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE TRUSTEES OF INDIANA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of the Trustees of Indiana University.
 */
package edu.indiana.dlib.catalog.vocabulary.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.indiana.dlib.catalog.config.SourceDefinition;
import edu.indiana.dlib.catalog.config.VocabularySourceConfiguration;
import edu.indiana.dlib.catalog.vocabulary.VocabularySource;
import edu.indiana.dlib.catalog.vocabulary.VocabularySourceInitializationException;
import edu.indiana.dlib.catalog.vocabulary.VocabularySourceManager;

public class SimpleVocabularySourceManager implements VocabularySourceManager {

    private Logger LOGGER = Logger.getLogger(SimpleVocabularySourceManager.class);
    
    private File managedLocalSourceDir;
    
    private Map<VocabularySourceConfiguration, VocabularySource> sourceCache;
    
    public SimpleVocabularySourceManager(String directory) {
        String photocatHome = System.getenv("PHOTOCAT_HOME");
        if (photocatHome != null && !directory.startsWith("/")) {
            File homeDir = new File(photocatHome);
            managedLocalSourceDir = new File(homeDir, directory);
        } else {
            
            managedLocalSourceDir = new File(directory);
        }
        managedLocalSourceDir.mkdirs();
        sourceCache = new HashMap<VocabularySourceConfiguration, VocabularySource>();
        VocabularySourceFactory.getInstance().setVocabularySourceManager(this);
    }

    public VocabularySource getVocabularySource(VocabularySourceConfiguration config, SourceDefinition def, String collectionId) throws IOException, VocabularySourceInitializationException {
        VocabularySource source = sourceCache.get(config);
        if (source != null) {
            return source;
        }
        try {
            // instantiate the class
            Class sourceClass = Class.forName(def.getJavaImplementation().getJavaClassName());
            if (VocabularySource.class.isAssignableFrom(sourceClass)) {
                Object sourceClassInstance = sourceClass.getConstructor(SourceDefinition.class, VocabularySourceConfiguration.class, VocabularySourceManager.class, String.class).newInstance(def, config, this, collectionId);
                // add any properties
                if (!def.getJavaImplementation().getJavaClassProperties().isEmpty()) {
                    // implement this when we actually have a use case
                    throw new UnsupportedOperationException("Properties are not yet supported!");
                }
                sourceCache.put(config, (VocabularySource) sourceClassInstance);
                return (VocabularySource) sourceClassInstance;
            } else {
                throw new VocabularySourceInitializationException("Configured class, \"" + def.getJavaImplementation().getJavaClassName() + "\" for sourceType \"" + config.getType() + "\" is not an instance of " + VocabularySource.class.getName() + ".");
            }
        } catch (ClassNotFoundException ex) {
            throw new VocabularySourceInitializationException(ex);
        } catch (SecurityException ex) {
            throw new VocabularySourceInitializationException(ex);
        } catch (IllegalArgumentException ex) {
            throw new VocabularySourceInitializationException(ex);
        } catch (InstantiationException ex) {
            throw new VocabularySourceInitializationException(ex);
        } catch (IllegalAccessException ex) {
            throw new VocabularySourceInitializationException(ex);
        } catch (InvocationTargetException ex) {
            LOGGER.error("Error generating vocabulary source!", ex);
            throw new VocabularySourceInitializationException(ex);
        } catch (NoSuchMethodException ex) {
            throw new VocabularySourceInitializationException(ex);
        } 
    }

    public File getLocalVocabularyDataDirectory() {
        return managedLocalSourceDir;
    }
}
