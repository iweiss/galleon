/*
 * Copyright 2016-2019 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.galleon.universe.maven.xml;

import java.util.Collection;

import javax.xml.stream.XMLStreamException;

import org.jboss.galleon.universe.maven.MavenProducerDescription;
import org.jboss.galleon.universe.maven.xml.MavenProducerXmlParser10.Attribute;
import org.jboss.galleon.universe.maven.xml.MavenProducerXmlParser10.Element;
import org.jboss.galleon.xml.BaseXmlWriter;
import org.jboss.galleon.xml.util.ElementNode;
import org.jboss.galleon.xml.util.TextNode;

/**
 *
 * @author Alexey Loubyansky
 */
public class MavenProducerXmlWriter extends BaseXmlWriter<MavenProducerDescription<?>> {

    private static final MavenProducerXmlWriter INSTANCE = new MavenProducerXmlWriter();

    public static MavenProducerXmlWriter getInstance() {
        return INSTANCE;
    }

    @Override
    protected ElementNode toElement(MavenProducerDescription<?> producer) throws XMLStreamException {
        final ElementNode producerEl = addElement(null, Element.PRODUCER);
        addAttribute(producerEl, Attribute.NAME, producer.getName());
        if(producer.getFeaturePackGroupId() != null) {
            addElement(producerEl, Element.FP_GROUP_ID).addChild(new TextNode(producer.getFeaturePackGroupId()));
        }
        if(producer.getFeaturePackArtifactId() != null) {
            addElement(producerEl, Element.FP_ARTIFACT_ID).addChild(new TextNode(producer.getFeaturePackArtifactId()));
        }
        if(producer.hasDefaultChannel()) {
            addElement(producerEl, Element.DEFAULT_CHANNEL).addChild(new TextNode(producer.getDefaultChannelName()));
        }
        final ElementNode frequenciesEl = addElement(producerEl, Element.FREQUENCIES);
        final Collection<String> frequencies = producer.getFrequencies();
        final String defaultFrequency = producer.getDefaultFrequency();
        for(String frequency : frequencies) {
            final ElementNode frequencyE = addElement(frequenciesEl, Element.FREQUENCY);
            if(defaultFrequency != null && frequency.equals(defaultFrequency)) {
                addAttribute(frequencyE, Attribute.DEFAULT, "true");
            }
            frequencyE.addChild(new TextNode(frequency));
        }
        return producerEl;
    }

}
