/*
 * Copyright 2016-2020 Red Hat, Inc. and/or its affiliates
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
package org.jboss.galleon.maven.noloc.test;

import org.jboss.galleon.ProvisioningDescriptionException;
import org.jboss.galleon.ProvisioningException;
import org.jboss.galleon.config.FeaturePackConfig;
import org.jboss.galleon.config.ProvisioningConfig;
import org.jboss.galleon.creator.FeaturePackCreator;
import org.jboss.galleon.state.ProvisionedFeaturePack;
import org.jboss.galleon.state.ProvisionedState;
import org.jboss.galleon.universe.FeaturePackLocation;
import org.jboss.galleon.universe.MvnUniverse;
import org.jboss.galleon.universe.ProvisionFromUniverseTestBase;

/**
 *
 * @author Alexey Loubyansky
 */
public class OverrideFplVersionOfTransitiveDepWithGavVariation2TestCase extends ProvisionFromUniverseTestBase {

    private FeaturePackLocation template;
    private FeaturePackLocation wfly;
    private FeaturePackLocation servlet;
    private FeaturePackLocation core;

    @Override
    protected void createProducers(MvnUniverse universe) throws ProvisioningException {
        universe.createProducer("template");
        universe.createProducer("wfly");
        universe.createProducer("servlet");
        universe.createProducer("core");
    }

    @Override
    protected void createFeaturePacks(FeaturePackCreator creator) throws ProvisioningException {

        template = newFpl("template", "1", "1.0.0.Final");

        wfly = newFpl("wfly", "1", "1.0.0.Final");
        servlet = newFpl("servlet", "1", "1.0.0.Final");
        core = newFpl("core", "1", "1.0.0.Final");

        creator.newFeaturePack()
            .setFPID(template.getFPID())
            .addDependency(toMavenCoordsFpl(wfly))
            .addTransitiveDependency(toMavenCoordsFpl(servlet))
            .addTransitiveDependency(toMavenCoordsFpl(core))
            .newPackage("p1", true);

        creator.newFeaturePack()
            .setFPID(wfly.getFPID())
            .addDependency(servlet)
            .addTransitiveDependency(core)
            .newPackage("p1", true);

        creator.newFeaturePack()
            .setFPID(servlet.getFPID())
            .addDependency(core)
            .newPackage("p1", true);

        creator.newFeaturePack()
            .setFPID(core.getFPID())
            .newPackage("p1", true);
    }

    @Override
    protected ProvisioningConfig provisioningConfig() throws ProvisioningDescriptionException {
        return ProvisioningConfig.builder()
                .addFeaturePackDep(FeaturePackConfig.forLocation(toMavenCoordsFpl(template)))
                .build();
    }

    @Override
    protected ProvisioningConfig provisionedConfig() throws ProvisioningDescriptionException {
        return ProvisioningConfig.builder()
                .addFeaturePackDep(FeaturePackConfig.forTransitiveDep(servlet))
                .addFeaturePackDep(FeaturePackConfig.forTransitiveDep(core))
                .addFeaturePackDep(FeaturePackConfig.forTransitiveDep(wfly))
                .addFeaturePackDep(FeaturePackConfig.forLocation(template))
                .build();
    }

    @Override
    protected ProvisionedState provisionedState() throws ProvisioningException {
        return ProvisionedState.builder()
                .addFeaturePack(ProvisionedFeaturePack.builder(core.getFPID())
                        .addPackage("p1")
                        .build())
                .addFeaturePack(ProvisionedFeaturePack.builder(servlet.getFPID())
                        .addPackage("p1")
                        .build())
                .addFeaturePack(ProvisionedFeaturePack.builder(wfly.getFPID())
                        .addPackage("p1")
                        .build())
                .addFeaturePack(ProvisionedFeaturePack.builder(template.getFPID())
                        .addPackage("p1")
                        .build())
                .build();
    }
}