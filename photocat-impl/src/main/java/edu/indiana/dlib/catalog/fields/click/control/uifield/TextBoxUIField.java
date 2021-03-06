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
package edu.indiana.dlib.catalog.fields.click.control.uifield;

import java.util.ArrayList;
import java.util.List;

import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.control.TextField;
import org.apache.log4j.Logger;

import edu.indiana.dlib.catalog.config.CollectionConfiguration;
import edu.indiana.dlib.catalog.config.ConfigurationException;
import edu.indiana.dlib.catalog.config.FieldConfiguration;
import edu.indiana.dlib.catalog.config.FieldData;
import edu.indiana.dlib.catalog.config.FieldDefinition;
import edu.indiana.dlib.catalog.config.NameValuePair;
import edu.indiana.dlib.catalog.config.VocabularySourceConfiguration;
import edu.indiana.dlib.catalog.fields.click.control.autocomplete.SourceSelectorField;
import edu.indiana.dlib.catalog.fields.click.control.autocomplete.VocabularySourceAutoCompleteTextField;
import edu.indiana.dlib.catalog.vocabulary.VocabularySource;
import edu.indiana.dlib.catalog.vocabulary.VocabularyTerm;
import edu.indiana.dlib.catalog.vocabulary.impl.VocabularySourceFactory;

/**
 * An extension of the GenericUIField that implements a UIField that
 * is represented primarily as a single (one line) text field.  The
 * data specification for that field must contain only ONE required 
 * field other than those with names "id" and "authority" which will
 * be used in the event of an appropriate vocabulary source or sources
 * being attached.
 */
public class TextBoxUIField extends GenericSinglePartUIField {
    
    private Logger LOGGER = Logger.getLogger(TextBoxUIField.class);
    
    private SourceSelectorField sourceSelector;
    
    public TextBoxUIField(FieldDefinition def, FieldConfiguration conf, CollectionConfiguration c) throws ConfigurationException {
        super(def, conf, c);
    }

    protected void addFields(RepeatableValueGroupContainer container, String partName) throws Exception {
        FieldConfiguration fc = this.getFieldConfiguration();
        //Field field = null;
        List<VocabularySourceConfiguration> sources = this.getFieldConfiguration().getVocabularySources();
        if (sources == null || sources.isEmpty()) {
            TextField field = new TextField();
            field.setWidth("32em");
            field.setReadonly(getFieldConfiguration().isReadOnly());
            container.setPartField(partName, field);
        } else {
            if (sources.size() == 1) {
                VocabularySourceConfiguration config = sources.get(0);
                if (!config.getValueBinding().equals(partName)) {
                    throw new ConfigurationException(this.getClass().getName() + " (implementing " + fc.getFieldType() + ") only supports binding to the \"" + partName + "\" part! (\"" + config.getValueBinding() + "\" was configured)");
                }
                VocabularySource source = VocabularySourceFactory.getInstance().getVocabularySource(config, this.getFieldDefinition().getDefinitions().getSourceDefinition(config.getType()), collection.getId()); 
                if (sources.get(0).getAuthorityBinding() == null) {
                    sourceSelector = new SourceSelectorField(this.getName() + "_autority", source);
                    container.setPartField("authority", sourceSelector, true);
                } else {
                    sourceSelector = new SourceSelectorField(this.getName() + "_autority", source);
                    container.setPartField("authority", sourceSelector, false);
                }

                if (!source.allowUnlistedTerms() && source.getTermCount() < 100) {
                    Select termSelect = new Select();
                    termSelect.setMultiple(false);
                    termSelect.add(new Option("", ""));
                    for (VocabularyTerm term : source.listAllTerms(100, 0)) {
                        termSelect.add(new Option(term.getId(), term.getDisplayName()));
                    }
                    container.setPartField(partName, termSelect);
                } else {
                    VocabularySourceAutoCompleteTextField textField = new VocabularySourceAutoCompleteTextField(this.getFieldConfiguration().getFieldType() + "_value", sourceSelector);
                    textField.setWidth("32em");
                    container.setPartField(partName, textField);
                }
                
            } else {
                for (VocabularySourceConfiguration config : sources) {
                    if (config.getValueBinding() != null && !partName.equals(config.getValueBinding())) {
                        throw new ConfigurationException(this.getClass().getName() + " (implementing " + fc.getFieldType() + ") only supports binding to the \"" + partName + "\" part! (\"" + config.getValueBinding() + "\" was configured)");
                    }
                }
                sourceSelector = new SourceSelectorField(this.getName() + "_authority", VocabularySourceFactory.getInstance().getVocabularySources(this.getFieldConfiguration(), this.getFieldDefinition().getDefinitions(), collection.getId()), "authority");
                
                container.setPartField("authority", sourceSelector);
                VocabularySourceAutoCompleteTextField textField = new VocabularySourceAutoCompleteTextField(this.getFieldConfiguration().getFieldType() + "_value", sourceSelector);
                textField.setWidth("32em");
                container.setPartField(partName, textField);
            }
        }
    }
    
    /**
     * @return true
     */
    public boolean supportsAttachedVocabularySources() {
        return true;
    }
    
    /**
     * Delegates to the superclass, but then modifies the result 
     * to remove any values which only have authority values.
     * 
     * TODO: this dirty trick could be eliminated if the Autocomplete
     * field and the selector were unified.
     */
    public FieldData getFieldData() {
        //boolean supportsAuthority = this.getFieldDefinition().getDataSpecification().getValidPartNames().contains("authority");
        //boolean supportsId = this.getFieldDefinition().getDataSpecification().getValidPartNames().contains("id");
        // TODO: prevent writing of these values if possible again, using the underlying field would be ideal
        String mainPartName = getMainPartName();
        FieldData data = super.getFieldData();
        List<List<NameValuePair>> values = new ArrayList<List<NameValuePair>>();
        for (List<NameValuePair> value : data.getParts()) {
            boolean hasValue = false;
            for (NameValuePair part : new ArrayList<NameValuePair>(value)) {
                if (part.getName().equals(mainPartName)) {
                    hasValue = true;
                }
            }
            if (hasValue) {
                values.add(value);
            }
        }
        data.getParts().clear();
        data.getParts().addAll(values);
        return data;
    }

}
