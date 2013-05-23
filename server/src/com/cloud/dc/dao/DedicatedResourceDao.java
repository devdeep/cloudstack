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
package com.cloud.dc.dao;

import java.util.List;

import com.cloud.dc.DedicatedResourceVO;
import com.cloud.utils.db.GenericDao;


public interface DedicatedResourceDao extends GenericDao<DedicatedResourceVO, Long> {

	List<DedicatedResourceVO> findZonesByDomainId(Long domainId);

	List<DedicatedResourceVO> findZonesByAccountId(Long accountId);

	List<DedicatedResourceVO> findHostsByAccountId(Long accountId);

	List<DedicatedResourceVO> findClustersByDomainId(Long domainId);

	List<DedicatedResourceVO> findPodsByAccountId(Long accountId);

	List<DedicatedResourceVO> findPodsByDomainId(Long domainId);

	DedicatedResourceVO findByZoneId(Long zoneId);

	DedicatedResourceVO findByPodId(Long podId);

	DedicatedResourceVO findByClusterId(Long clusterId);

	DedicatedResourceVO findByHostId(Long hostId);

    List<DedicatedResourceVO> findClustersByAccountId(Long accountId);

    List<DedicatedResourceVO> findHostsByDomainId(Long domainId);

    List<DedicatedResourceVO> listZones();

    List<DedicatedResourceVO> listPods();

    List<DedicatedResourceVO> listClusters();

    List<DedicatedResourceVO> listHosts();

    List<DedicatedResourceVO> listByAccountId(Long accountId);

    List<DedicatedResourceVO> listByDomainId(Long domainId);

    List<DedicatedResourceVO> listZonesNotInDomainIds(List<Long> domainIds);

}
