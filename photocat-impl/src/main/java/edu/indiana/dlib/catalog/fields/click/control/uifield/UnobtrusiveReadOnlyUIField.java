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

import java.util.List;

import org.apache.click.Control;
import org.apache.click.control.AbstractControl;
import org.apache.click.util.Format;
import org.apache.click.util.HtmlStringBuffer;

import edu.indiana.dlib.catalog.config.CollectionConfiguration;
import edu.indiana.dlib.catalog.config.ConfigurationException;
import edu.indiana.dlib.catalog.config.FieldConfiguration;
import edu.indiana.dlib.catalog.config.FieldData;
import edu.indiana.dlib.catalog.config.FieldDefinition;
import edu.indiana.dlib.catalog.config.NameValuePair;
import edu.indiana.dlib.catalog.fields.UIField;

/**
 * A UIField implementation for a text field that is only displayed  
 * when a value is present and is displayed as read-only.
 */
public class UnobtrusiveReadOnlyUIField extends AbstractControl implements UIField {

    private FieldDefinition def;
    
    private FieldConfiguration conf;
    
    private FieldData fieldData;
    
    public UnobtrusiveReadOnlyUIField(FieldDefinition def, FieldConfiguration conf, CollectionConfiguration c) throws ConfigurationException {
        super(def.getType());
        if (!conf.isReadOnly()) {
            throw new ConfigurationException(getMessage("error-not-read-only", conf.getDisplayLabel()));
        }
        if (!conf.getFieldType().equals(def.getType())) {
            throw new ConfigurationException(getMessage("error-field-type-mismatch"));
        }
        
        this.def = def;
        this.conf = conf;
    }

    
    public Control asClickControl() {
        return this;
    }

    public FieldConfiguration getFieldConfiguration() {
        return conf;
    }

    public FieldData getFieldData() {
        return fieldData;
    }

    public FieldDefinition getFieldDefinition() {
        return def;
    }

    public String getFieldType() {
        return def.getType();
    }

    /**
     * @returns a summary of the value contained in all attributes and parts
     */
    public String getValueSummary() {
        StringBuffer sb = new StringBuffer();
        if (this.getFieldData() != null) {
            if (fieldData.getAttributes() != null) {
                for (NameValuePair attribute : fieldData.getAttributes()) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(attribute.getValue());
                }
            }
            if (fieldData.getParts() != null) {
                for (List<NameValuePair> parts : fieldData.getParts()) {
                    for (NameValuePair part : parts) {
                        if (sb.length() > 0) {
                            sb.append(", ");
                        }
                        sb.append(part.getValue());
                    }
                }
            }
        }
        return sb.toString();
    }
    
    /**
     * A default implementation that simply escapes the result of
     * getValueSummary() for XHTML rendering.  This method should
     * be overridden if formatting should be incorporated into the
     * default display of this field.
     */
    public String getValueSummaryHtml() {
        return new Format().html(getValueSummary());
    }

    /**
     * Does nothing, because this field is only for read-only fields.
     */
    public void setDefaultValue(FieldData values) {
    }

    public void setFieldData(FieldData values) {
        fieldData = values;
    }
    
    /**
     * Lazily returns true in all cases.
     */
    public boolean isValueValid(FieldData values) {
        return true;
    }

    /**
     * Renders this field's preview.
     */
    public void render(HtmlStringBuffer buffer) {
        buffer.append(this.getValueSummary());
    }
    
    /**
     * Implements UIField; returns true.
     */
    public boolean suppressIfEmptyAndReadOnly() {
        return true;
    }
    
    public boolean hasDerivativeParts() {
        return false;
    }


}
