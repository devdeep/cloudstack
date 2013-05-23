// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package org.apache.cloudstack.dedicated.services;

import java.util.List;

import org.apache.cloudstack.dedicated.api.commands.ListDedicatedClustersCmd;
import org.apache.cloudstack.dedicated.api.commands.ListDedicatedHostsCmd;
import org.apache.cloudstack.dedicated.api.commands.ListDedicatedPodsCmd;
import org.apache.cloudstack.dedicated.api.commands.ListDedicatedZonesCmd;
import org.apache.cloudstack.dedicated.api.response.DedicateClusterResponse;
import org.apache.cloudstack.dedicated.api.response.DedicateHostResponse;
import org.apache.cloudstack.dedicated.api.response.DedicatePodResponse;
import org.apache.cloudstack.dedicated.api.response.DedicateZoneResponse;
import com.cloud.dc.DedicatedResourceVO;
import com.cloud.dc.DedicatedResources;
import com.cloud.utils.component.PluggableService;

public interface DedicatedService extends PluggableService{

    DedicatePodResponse createDedicatePodResponse(DedicatedResources resource);

    DedicateClusterResponse createDedicateClusterResponse(
            DedicatedResources resource);

    DedicateHostResponse createDedicateHostResponse(DedicatedResources resource);

    List<DedicatedResourceVO> listDedicatedPods(ListDedicatedPodsCmd cmd);

    List<DedicatedResourceVO> listDedicatedHosts(ListDedicatedHostsCmd cmd);

    List<DedicatedResourceVO> listDedicatedClusters(ListDedicatedClustersCmd cmd);

    boolean releaseDedicatedResource(Long zoneId, Long podId, Long clusterId, Long hostId);

    DedicateZoneResponse createDedicateZoneResponse(DedicatedResources resource);

    List<DedicatedResourceVO> listDedicatedZones(ListDedicatedZonesCmd cmd);

    List<DedicatedResourceVO> dedicateZone(Long zoneId, Long domainId, String accountName);

    List<DedicatedResourceVO> dedicatePod(Long podId, Long domainId, String accountName);

    List<DedicatedResourceVO> dedicateCluster(Long clusterId, Long domainId, String accountName);

    List<DedicatedResourceVO> dedicateHost(Long hostId, Long domainId, String accountName);

}
