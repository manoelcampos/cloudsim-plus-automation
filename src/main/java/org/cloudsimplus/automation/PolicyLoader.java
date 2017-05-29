/*
 * CloudSim Plus Automation: A Human Readable Scenario Specification for Automated Creation of Simulations on CloudSim Plus.
 * https://github.com/manoelcampos/CloudSimAutomation
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Automation.
 *
 *     CloudSim Plus Automation is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus Automation is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Automation. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.automation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import cloudreports.models.DatacenterRegistry;
import cloudreports.models.HostRegistry;
import cloudreports.models.VirtualMachineRegistry;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.provisioners.PeProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;

/**
 * Dynamically creates instances of classes such as {@link VmScheduler}, {@link VmAllocationPolicy},
 * {@link CloudletScheduler}, {@link ResourceProvisioner} and others from the class name of
 * the object to be instantiated.
 *
 * @author Manoel Campos da Silva Filho
 */
public class PolicyLoader {
    /**
     * The base CloudSim package name.
     */
    private static final String PKG = "org.cloudbus.cloudsim";

    public static VmScheduler vmScheduler(String classSufix) throws RuntimeException {
        try {
            classSufix = generateFullClassName(PKG+".schedulers.vm","VmScheduler", classSufix);
            Class<? extends VmScheduler> klass = (Class<? extends VmScheduler>) Class.forName(classSufix);
            Constructor cons = klass.getConstructor(new Class[]{});
            return (VmScheduler) cons.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(PolicyLoader.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Gets an instance of a resource provisioner with a given
     * class name information.
     *
     * @param classPrefix The class prefix for the provisioner.
     * If you want to instantiate the provisioner class BwProvisionerSimple,
     * the provisioner prefix is "Bw"
     * @param classSufix The class suffix of the provisioner.
     * If you want to instantiate the provisioner class BwProvisionerSimple,
     * the provisioner suffix is just "Simple"
     * @return A new instance of the provisioner with the given name.
     * For instance, if the class suffix is "Simple",
     * returns an instance the ResourceProvisionerSimple class.
     * @throws RuntimeException
     */
    private static <T extends ResourceProvisioner> T resourceProvisioner(
        String classPrefix, String classSufix) throws RuntimeException {
        try {
            final String className = generateFullProvisionerClassName(classPrefix, classSufix);
            final Class resourceProvisionerClass = Class.forName(className);
            Constructor cons = resourceProvisionerClass.getConstructor(new Class[]{});
            return (T)cons.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(PolicyLoader.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    public static ResourceProvisioner newResourceProvisioner(final HostRegistry hr) throws RuntimeException {
        return resourceProvisioner("", hr.getBwProvisionerAlias());
    }

    public static PeProvisioner newPeProvisioner(final HostRegistry hr) throws RuntimeException {
        return resourceProvisioner("Pe", hr.getPeProvisionerAlias());
    }

    public static VmAllocationPolicy vmAllocationPolicy(final DatacenterRegistry dcr) throws RuntimeException {
        try {
            String classSufix = generateFullClassName(PKG+".allocationpolicies","VmAllocationPolicy", dcr.getAllocationPolicyAlias());
            Class<? extends VmScheduler> klass = (Class<? extends VmScheduler>) Class.forName(classSufix);
            Constructor cons = klass.getConstructor(new Class[]{});
            return (VmAllocationPolicy) cons.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(PolicyLoader.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    public static CloudletScheduler cloudletScheduler(final VirtualMachineRegistry vmr) throws RuntimeException {
        try {
            String classSufix = generateFullClassName(PKG+".schedulers.cloudlet","CloudletScheduler", vmr.getSchedulingPolicyAlias());
            Class<? extends VmScheduler> klass = (Class<? extends VmScheduler>) Class.forName(classSufix);
            Constructor cons = klass.getConstructor();
            return (CloudletScheduler) cons.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(PolicyLoader.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    private static String generateFullClassName(String packageName, String classPrefix, String classSuffix) {
        return String.format("%s.%s%s", packageName, classPrefix, classSuffix);
    }

    private static String generateFullProvisionerClassName(String classPrefix, String classSuffix) {
        classPrefix = (classPrefix.isEmpty() ? "ResourceProvisioner" : classPrefix+"Provisioner");
        return generateFullClassName(PKG+".provisioners", classPrefix, classSuffix);
    }

    public static UtilizationModel utilizationModel(String classSufix) throws RuntimeException {
        try {
            final String className = generateFullClassName(PKG+".utilizationmodels", "UtilizationModel", classSufix);
            Class<? extends VmScheduler> klass = (Class<? extends VmScheduler>) Class.forName(className);
            Constructor cons = klass.getConstructor();
            return (UtilizationModel) cons.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(PolicyLoader.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
}